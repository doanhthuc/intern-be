package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.EventDTO;
import com.mgmtp.easyquizy.model.event.EventEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDTO eventEntityToEventDto(EventEntity eventEntity);

    EventEntity eventDtoToEventEntity(EventDTO eventDTO);

    List<EventDTO> listEventEntityToListEventDto(List<EventEntity> listEventEntity);
}
