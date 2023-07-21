package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.answer.AnswerDTO;
import com.mgmtp.easyquizy.dto.question.QuestionDTO;
import com.mgmtp.easyquizy.dto.question.QuestionListViewDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.QuestionAssociatedWithQuizzesException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.exception.kahoot.KahootUploadImageException;
import com.mgmtp.easyquizy.mapper.AnswerMapper;
import com.mgmtp.easyquizy.mapper.QuestionMapper;
import com.mgmtp.easyquizy.model.answer.AnswerEntity;
import com.mgmtp.easyquizy.model.attachment.AttachmentEntity;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import com.mgmtp.easyquizy.model.question.Difficulty;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.repository.AnswerRepository;
import com.mgmtp.easyquizy.repository.AttachmentRepository;
import com.mgmtp.easyquizy.repository.CategoryRepository;
import com.mgmtp.easyquizy.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private KahootService kahootService;

    @Override
    public QuestionDTO getQuestionById(Long id) throws RecordNotFoundException {
        QuestionEntity question = questionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("No Question record exists for the given id: " + id));
        return questionMapper.questionToQuestionDTO(question);
    }

    @Override
    @Transactional
    public QuestionDTO createQuestion(QuestionDTO questionDTO) throws RecordNotFoundException {
        questionDTO.setId(null);
        QuestionEntity question = questionMapper.questionDTOToQuestion(questionDTO);

        CategoryEntity category = getCategoryById(questionDTO.getCategory().getId());
        question.setCategory(category);

        List<AnswerDTO> answerDTOs = questionDTO.getAnswers();
        List<AnswerEntity> answers = answerDTOs.stream().map(answerMapper::answerDTOtoAnswer).toList();
        answers.forEach(answer -> answer.setId(null));
        question.setAnswers(answers);

        if (question.getAttachment() != null) {
            question.getAttachment().setId(null);
            question.getAttachment().setIsUploaded(false);
        }

        QuestionEntity savedQuestion = questionRepository.save(question);
        // Asynchronously upload image to Kahoot to let user get response faster
        uploadQuestionImage(savedQuestion);

        return questionMapper.questionToQuestionDTO(savedQuestion);
    }

    // Upload image to Kahoot and update attachment url in database asynchronously (run in background)
    private void uploadQuestionImage(QuestionEntity question) {
        if (question.getAttachment() == null) {
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CompletableFuture.supplyAsync(() -> kahootService.getKahootAccount(), executorService).thenApply((account) -> {
            if (account == null) {
                return null;
            }
            return kahootService.uploadImages(new AttachmentEntity[]{question.getAttachment()}, account.getAccessToken());
        }).thenApply(result -> {
            if (result == null) {
                return null;
            }
            AttachmentEntity attachment = question.getAttachment();
            attachment.setKahootUrl(result.get(attachment.getId()));
            attachment.setIsUploaded(true);
            attachmentRepository.save(attachment);
            return result;
        }).whenComplete((result, exception) -> executorService.shutdown()).exceptionally(exception -> {
            throw new KahootUploadImageException();
        });
    }

    @Override
    @Transactional
    public QuestionDTO updateQuestion(QuestionDTO questionDTO) throws RecordNotFoundException, InvalidFieldsException {
        if (questionDTO.getId() == null) {
            InvalidFieldsException.FieldError idError = new InvalidFieldsException.FieldError("id", "This field is required");
            throw new InvalidFieldsException(List.of(idError));
        }

        QuestionEntity existingQuestion = questionRepository.findById(questionDTO.getId()).orElseThrow(() -> new RecordNotFoundException("No Question record exists for the given id: " + questionDTO.getId()));
        QuestionEntity question = questionMapper.questionDTOToQuestion(questionDTO);

        if (question.getAttachment() != null) {
            question.getAttachment().setId(null);
            String newImage = question.getAttachment().getImageData();
            String existingImage = existingQuestion.getAttachment() != null ? existingQuestion.getAttachment().getImageData() : null;
            question.getAttachment().setIsUploaded(newImage.equals(existingImage));
        }

        if (existingQuestion.getAttachment() != null) {
            attachmentRepository.deleteById(existingQuestion.getAttachment().getId());
        }

        CategoryEntity category = getCategoryById(questionDTO.getCategory().getId());
        question.setCategory(category);

        answerRepository.deleteByQuestionId(questionDTO.getId());
        List<AnswerDTO> answerDTOs = questionDTO.getAnswers();
        List<AnswerEntity> answers = answerDTOs.stream().map(answerMapper::answerDTOtoAnswer).toList();
        answers.forEach(answer -> answer.setId(null));
        question.setAnswers(answers);

        QuestionEntity savedQuestion = questionRepository.save(question);

        if (savedQuestion.getAttachment() != null && !savedQuestion.getAttachment().getIsUploaded()) {
            uploadQuestionImage(savedQuestion);
        }

        return questionMapper.questionToQuestionDTO(savedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestionById(Long id) {
        QuestionEntity question = questionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("No Question record exists for the given id: " + id));
        if (!question.getQuizzes().isEmpty()) {
            throw new QuestionAssociatedWithQuizzesException("Cannot delete this question because it is associated with quizzes!");
        }
        questionRepository.deleteById(id);
    }

    @Override
    public Page<QuestionListViewDTO> getAllQuestions(String keyword, Difficulty difficulty, Integer categoryId, int offset, int limit) {
        int pageNo = offset / limit;
        Specification<QuestionEntity> filterSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
            }
            if (difficulty != null) {
                predicates.add(cb.equal(root.get("difficulty"), difficulty));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").descending());

        Page<QuestionEntity> page = questionRepository.findAll(filterSpec, pageable);
        return page.map(questionMapper::questionToQuestionListViewDTO);
    }

    private CategoryEntity getCategoryById(Long id) throws RecordNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("No category record exists!!!"));
    }
}
