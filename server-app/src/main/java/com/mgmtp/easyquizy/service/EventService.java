package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.EventDTO;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import org.springframework.data.domain.Page;

public interface EventService {
    EventDTO createEvent(EventDTO eventDTO);

    Page<EventDTO> getAllEvent(String keyword, int offset, int limit);

    EventDTO getEventById(Long id) throws RecordNotFoundException;

    EventDTO updateEvent(EventDTO eventDTO);

    void deleteEventById(Long id) throws RecordNotFoundException;
}
