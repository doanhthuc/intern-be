package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.event.EventDTO;
import com.mgmtp.easyquizy.dto.question.QuestionListViewDTO;
import com.mgmtp.easyquizy.dto.quiz.GenerateQuizRequestDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDtoDetail;
import com.mgmtp.easyquizy.exception.DuplicatedQuestionException;
import com.mgmtp.easyquizy.exception.NoMatchEventIdException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.QuestionMapper;
import com.mgmtp.easyquizy.mapper.QuizMapper;
import com.mgmtp.easyquizy.model.event.EventEntity;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import com.mgmtp.easyquizy.repository.CategoryRepository;
import com.mgmtp.easyquizy.repository.EventRepository;
import com.mgmtp.easyquizy.repository.QuestionRepository;
import com.mgmtp.easyquizy.repository.QuizRepository;
import com.mgmtp.easyquizy.utils.QuizGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    private final EventRepository eventRepository;

    private final QuestionRepository questionRepository;

    private final CategoryRepository categoryRepository;

    private final QuizMapper quizMapper;

    private final QuestionMapper questionMapper;

    private final EventService eventService;

    private List<QuestionEntity> createListQuestionEntity(List<Long> questionIds) throws RecordNotFoundException, DuplicatedQuestionException{
        List<Long> distinctQuestionIds = questionIds.stream().distinct().toList();
        if(distinctQuestionIds.size() != questionIds.size()) {
            throw new DuplicatedQuestionException("Duplicated question in the quiz");
        }
        List<QuestionEntity> questionEntityList = questionRepository.findAllById(distinctQuestionIds);
        if(questionEntityList.size() != questionIds.size()) {
            throw new RecordNotFoundException("No question records exist for the given id");
        }
        return questionEntityList;
    }

    private Integer calculateTotalTime(List<QuestionEntity> questionEntities) {
        return questionEntities.stream().map(QuestionEntity::getTimeLimit).mapToInt(Integer::intValue).sum();
    }

    public QuizDTO createQuiz(QuizDTO quizDTO) throws RecordNotFoundException, DuplicatedQuestionException {
        EventEntity eventEntity = eventRepository.findById(quizDTO.getEventId())
                .orElseThrow(() -> new RecordNotFoundException("No event records exist for the given id"));
        QuizEntity createdQuiz = new QuizEntity();
        createdQuiz.setTitle(quizDTO.getTitle());
        createdQuiz.setEventEntity(eventEntity);
        List<QuestionEntity> questionEntities = createListQuestionEntity(quizDTO.getQuestionIds());
        createdQuiz.setQuestions(questionEntities);
        quizRepository.save(createdQuiz);
        QuizDTO result = quizMapper.quizEntityToQuizDTO(createdQuiz);
        result.setTotalTime(calculateTotalTime(questionEntities));
        result.setQuestionIds(createdQuiz.getQuestions().stream().map(QuestionEntity::getId).toList());
        return result;
    }

    @Override
    public Page<QuizDTO> getAllQuizOfEvent(Long eventId, String keyword, int offset, int limit) throws RecordNotFoundException {
        int pageNo = offset / limit;
        EventDTO eventDTO = eventService.getEventById(eventId);
        Specification<QuizEntity> filterSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (eventDTO != null) {
                predicates.add(cb.equal(root.get("eventEntity").get("id"), eventDTO.getId()));
            }
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").descending());
        Page<QuizEntity> page = quizRepository.findAll(filterSpec, pageable);
        List<List<QuestionEntity>> listQuestionEntities = new ArrayList<>();
        page.getContent().forEach(quizEntity -> listQuestionEntities.add(quizEntity.getQuestions()));
        Page<QuizDTO> quizDTOPage = page.map(quizMapper::quizEntityToQuizDTO);
        quizDTOPage.forEach(quizDTO -> {
            List<QuestionEntity> questionEntities = listQuestionEntities.remove(0);
            quizDTO.setQuestionIds(questionEntities.stream().map(QuestionEntity::getId).collect(Collectors.toList()));
            quizDTO.setTotalTime(calculateTotalTime(questionEntities));
        });
        return quizDTOPage;
    }

    @Override
    public QuizDtoDetail getQuizById(Long id) throws RecordNotFoundException {
        QuizEntity quizEntity = quizRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No quiz records exist for the given id "));
        QuizDtoDetail result = quizMapper.quizEntityToQuizDtoDetail(quizEntity);
        result.setTotalTime(calculateTotalTime(quizEntity.getQuestions()));
        return result;
    }

    @Override
    public QuizDTO updateQuiz(QuizDTO quizDTO) throws RecordNotFoundException, DuplicatedQuestionException, NoMatchEventIdException {
        QuizEntity updatedQuiz = quizRepository.findById(quizDTO.getId())
                .orElseThrow(() -> new RecordNotFoundException("No quiz records exist for the given id "));
        EventEntity eventEntity = eventRepository.findById(updatedQuiz.getEventEntity().getId())
                .orElseThrow(() -> new RecordNotFoundException("No event records exist for the given id"));
        if (!eventEntity.getId().equals(quizDTO.getEventId()))
            throw new NoMatchEventIdException("Event's id does not match with exist quiz");
        List<QuestionEntity> questionEntities = createListQuestionEntity(quizDTO.getQuestionIds());
        updatedQuiz.setId(quizDTO.getId());
        updatedQuiz.setTitle(quizDTO.getTitle());
        updatedQuiz.setEventEntity(eventEntity);
        quizRepository.removeQuestionFromQuiz(quizDTO.getId());
        updatedQuiz.setQuestions(questionEntities);
        quizRepository.save(updatedQuiz);
        QuizDTO result = quizMapper.quizEntityToQuizDTO(updatedQuiz);
        result.setTotalTime(calculateTotalTime(updatedQuiz.getQuestions()));
        result.setQuestionIds(updatedQuiz.getQuestions().stream().map(QuestionEntity::getId).toList());
        return result;
    }

    @Override
    public void deleteQuizById(Long id) {
        QuizEntity quizEntity = quizRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No quiz records exist for the given id"));
        quizEntity.getQuestions().forEach(
                questionEntity -> questionEntity.getQuizzes().remove(quizEntity));
        quizRepository.deleteById(id);
    }

    @Override
    public List<QuestionListViewDTO> generateQuiz(GenerateQuizRequestDTO generateQuizRequestDTO) {
        // Validate category IDs
        generateQuizRequestDTO.getCategoryPercentages().keySet().forEach(
                categoryId -> {
                    if (!categoryRepository.existsById(categoryId)) {
                        throw new RecordNotFoundException("One or more categories with the given category IDs do not exist.");
                    }
                }
        );
        //  Get the questions for the required categories
        Set<Long> requiredCategoryIds = generateQuizRequestDTO.getCategoryPercentages().keySet();
        List<QuestionEntity> questions = questionRepository.findByCategoryIdIn(requiredCategoryIds);

        //  Generate the quiz and convert to question list view dto
        return QuizGenerator.generateQuiz(questions, generateQuizRequestDTO.getTotalTime(), generateQuizRequestDTO.getCategoryPercentages())
                .stream().map(questionMapper::questionToQuestionListViewDTO).toList();
    }
}
