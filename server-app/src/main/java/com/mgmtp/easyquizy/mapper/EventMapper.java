package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.event.EventDTO;
import com.mgmtp.easyquizy.model.event.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDTO eventEntityToEventDto(EventEntity eventEntity);

    @Mapping(target = "quizEntity", ignore = true)
    EventEntity eventDtoToEventEntity(EventDTO eventDTO);
}
