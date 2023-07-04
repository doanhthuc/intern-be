package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.AnswerDTO;
import com.mgmtp.easyquizy.dto.QuestionDTO;
import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.AnswerMapper;
import com.mgmtp.easyquizy.mapper.QuestionMapper;
import com.mgmtp.easyquizy.model.answer.AnswerEntity;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import com.mgmtp.easyquizy.model.question.Difficulty;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.repository.AnswerRepository;
import com.mgmtp.easyquizy.repository.AttachmentRepository;
import com.mgmtp.easyquizy.repository.CategoryRepository;
import com.mgmtp.easyquizy.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
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

    @Override
    public QuestionDTO getQuestionById(Long id) throws RecordNotFoundException {
        QuestionEntity question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No Question record exists for the given id: " + id));
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
        List<AnswerEntity> answers = answerDTOs.stream()
                .map(answerMapper::answerDTOtoAnswer)
                .toList();
        answers.forEach(answer -> answer.setId(null));
        question.setAnswers(answers);

        if (question.getAttachment() != null) {
            question.getAttachment().setId(null);
        }

        QuestionEntity savedQuestion = questionRepository.save(question);

        return questionMapper.questionToQuestionDTO(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionDTO updateQuestion(QuestionDTO questionDTO) throws RecordNotFoundException, InvalidFieldsException {
        if (questionDTO.getId() == null) {
            InvalidFieldsException.FieldError idError = new InvalidFieldsException.FieldError("id", "This field is required");
            throw new InvalidFieldsException(List.of(idError));
        }

        QuestionEntity existingQuestion = questionRepository.findById(questionDTO.getId()).orElseThrow(() ->
                new RecordNotFoundException("No Question record exists for the given id: " + questionDTO.getId()));
        QuestionEntity question = questionMapper.questionDTOToQuestion(questionDTO);

        if (question.getAttachment() != null) {
            question.getAttachment().setId(null);
        }
        if (existingQuestion.getAttachment() != null) {
            attachmentRepository.deleteById(existingQuestion.getAttachment().getId());
        }

        CategoryEntity category = getCategoryById(questionDTO.getCategory().getId());
        question.setCategory(category);

        answerRepository.deleteByQuestionId(questionDTO.getId());
        List<AnswerDTO> answerDTOs = questionDTO.getAnswers();
        List<AnswerEntity> answers = answerDTOs.stream()
                .map(answerMapper::answerDTOtoAnswer)
                .toList();
        answers.forEach(answer -> answer.setId(null));
        question.setAnswers(answers);

        QuestionEntity savedQuestion = questionRepository.save(question);

        return questionMapper.questionToQuestionDTO(savedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestionById(Long id) throws RecordNotFoundException {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
        } else {
            throw new RecordNotFoundException("No Question record exists for the given id: " + id);
        }
    }

    @Override
    public Page<QuestionListViewDTO> getAllQuestions(
            String keyword, Difficulty difficulty, Integer categoryId, int offset, int limit) {
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

        Pageable pageable = PageRequest.of(pageNo, limit);

        Page<QuestionEntity> page = questionRepository.findAll(filterSpec, pageable);
        return page.map(questionMapper::questionToQuestionListViewDTO);
    }

    private CategoryEntity getCategoryById(Long id) throws RecordNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No category record exists!!!"));
    }
}
