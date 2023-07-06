package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.EventDTO;
import com.mgmtp.easyquizy.dto.QuestionDTO;
import com.mgmtp.easyquizy.dto.QuizDTO;
import com.mgmtp.easyquizy.dto.QuizDtoDetail;
import com.mgmtp.easyquizy.exception.DuplicatedQuestionException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.EventMapper;
import com.mgmtp.easyquizy.mapper.QuizMapper;
import com.mgmtp.easyquizy.model.event.EventEntity;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import com.mgmtp.easyquizy.repository.EventRepository;
import com.mgmtp.easyquizy.repository.QuestionRepository;
import com.mgmtp.easyquizy.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    private final EventRepository eventRepository;

    private final QuestionRepository questionRepository;

    private final QuizMapper quizMapper;

    private final EventMapper eventMapper;

    private final EventService eventService;

    private List<QuestionEntity> createListQuestionEntity(QuizDtoDetail quizDtoDetail) throws RecordNotFoundException, DuplicatedQuestionException{
        List<Long> questionIds = quizDtoDetail.getQuestions()
                .stream()
                .map(QuestionDTO::getId).distinct()
                .toList();
        if(questionIds.size() != quizDtoDetail.getQuestions().size()) {
            throw new DuplicatedQuestionException("Duplicated question in the quiz");
        }
        List<QuestionEntity> questionEntityList = questionRepository.findAllById(questionIds);
        if(questionEntityList.size() != quizDtoDetail.getQuestions().size()) {
            throw new RecordNotFoundException("No question records exist for the given id");
        }
        return questionEntityList;
    }

    private Integer calculateTotalTime(List<QuestionEntity> questionEntities) {
        return questionEntities.stream().map(QuestionEntity::getTimeLimit).mapToInt(Integer::intValue).sum();
    }

    public QuizDtoDetail createQuiz(QuizDtoDetail quizDtoDetail) throws RecordNotFoundException, DuplicatedQuestionException {
        EventEntity eventEntity = eventRepository.findById(quizDtoDetail.getEvent().getId())
                .orElseThrow(() -> new RecordNotFoundException("No event records exist for the given id"));
        QuizEntity createdQuiz = quizMapper.quizDtoDetailToQuizEntity(quizDtoDetail);
        List<QuestionEntity> questionEntities = createListQuestionEntity(quizDtoDetail);
        createdQuiz.setEventEntity(eventEntity);
        createdQuiz.setQuestions(questionEntities);
        questionEntities.forEach(questionEntity -> questionEntity.getQuizzes().add(createdQuiz));
        quizRepository.save(createdQuiz);
        QuizDtoDetail result = quizMapper.quizEntityToQuizDtoDetail(createdQuiz);
        result.setTotalTime(calculateTotalTime(questionEntities));
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
        Pageable pageable = PageRequest.of(pageNo, limit);
        Page<QuizEntity> page = quizRepository.findAll(filterSpec, pageable);
        List<List<QuestionEntity>> questionEntities = new ArrayList<>();
        page.getContent().forEach(quizEntity -> questionEntities.add(quizEntity.getQuestions()));
        Page<QuizDTO> quizDTOPage = page.map(quizMapper::quizEntityToQuizDTO);
        quizDTOPage.forEach(quizDTO -> quizDTO.setTotalTime(calculateTotalTime(questionEntities.remove(0))));
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
    public QuizDtoDetail updateQuiz(QuizDtoDetail quizDtoDetail) throws RecordNotFoundException, DuplicatedQuestionException {
        QuizEntity updatedQuiz = quizRepository.findById(quizDtoDetail.getId())
                .orElseThrow(() -> new RecordNotFoundException("No quiz records exist for the given id "));
        List<QuestionEntity> questionEntities = createListQuestionEntity(quizDtoDetail);
        updatedQuiz.setId(quizDtoDetail.getId());
        updatedQuiz.setTitle(quizDtoDetail.getTitle());
        updatedQuiz.setEventEntity(eventMapper.eventDtoToEventEntity(quizDtoDetail.getEvent()));
        quizRepository.removeQuestionFromQuiz(quizDtoDetail.getId());
        questionEntities.forEach(questionEntity -> questionEntity.getQuizzes().add(updatedQuiz));
        updatedQuiz.setQuestions(questionEntities);
        quizRepository.save(updatedQuiz);
        QuizDtoDetail result = quizMapper.quizEntityToQuizDtoDetail(updatedQuiz);
        result.setTotalTime(calculateTotalTime(updatedQuiz.getQuestions()));
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
}
