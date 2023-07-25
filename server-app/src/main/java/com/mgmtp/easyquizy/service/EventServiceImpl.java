package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.event.EventDTO;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.EventMapper;
import com.mgmtp.easyquizy.model.event.EventEntity;
import com.mgmtp.easyquizy.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Lazy
    @Autowired
    QuizService quizService;

    @Override
    public List<Integer> getAllYear() {
        return eventRepository.findDistinctByStartDateYear();
    }

    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        eventDTO.setId(null);
        EventEntity created = eventMapper.eventDtoToEventEntity(eventDTO);
        eventRepository.save(created);
        return eventMapper.eventEntityToEventDto(created);
    }

    @Override
    public Page<EventDTO> getAllEvent(String keyword, Integer year, int offset, int limit) {
        int pageNo = offset / limit;
        Specification<EventEntity> filterSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
            }
            if (year != null) {
                predicates.add(cb.equal(cb.function("year", Integer.class, root.get("startDate")), year));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").descending());
        Page<EventEntity> page = eventRepository.findAll(filterSpec, pageable);
        return page.map(eventMapper::eventEntityToEventDto);
    }

    @Override
    public EventDTO getEventById(Long id) throws RecordNotFoundException {
        if (id == null) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required");}
        Optional<EventEntity> eventEntity = eventRepository.findById(id);
        return eventMapper.eventEntityToEventDto(eventEntity.orElseThrow(() -> new RecordNotFoundException("No event records exist for the given id")));
    }

    @Override
    public EventDTO updateEvent(EventDTO eventDTO) {
        EventEntity updated = eventMapper.eventDtoToEventEntity(getEventById(eventDTO.getId()));
        updated.setTitle(eventDTO.getTitle());
        updated.setStartDate(eventDTO.getStartDate());
        updated.setEndDate(eventDTO.getEndDate());
        updated.setDescription(eventDTO.getDescription());
        eventRepository.save(updated);
        return eventMapper.eventEntityToEventDto(updated);
    }

    @Override
    public void deleteEventById(Long id) throws RecordNotFoundException {
        EventEntity eventEntity = eventRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No event records exist for the given id"));
        if(!eventEntity.getQuizEntity().isEmpty()) {
            eventEntity.getQuizEntity().forEach(quizEntity -> quizService.deleteQuizById(quizEntity.getId(), false));
        }
        eventRepository.deleteById(eventEntity.getId());
    }
}
