package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.event.EventDTO;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import org.springframework.data.domain.Page;
import java.util.List;

public interface EventService {
    EventDTO createEvent(EventDTO eventDTO);

    Page<EventDTO> getAllEvent(String keyword, Integer year, int offset, int limit);

    EventDTO getEventById(Long id) throws RecordNotFoundException;

    EventDTO updateEvent(EventDTO eventDTO);

    void deleteEventById(Long id) throws RecordNotFoundException;

    List<Integer> getAllYear();
}
