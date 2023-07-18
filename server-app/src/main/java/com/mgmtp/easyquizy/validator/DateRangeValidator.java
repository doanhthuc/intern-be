package com.mgmtp.easyquizy.validator;

import com.mgmtp.easyquizy.dto.event.EventDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class DateRangeValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EventDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventDTO eventDTO = (EventDTO) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", "endDate.null", "End date is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "startDate.null", "Start date is required");
        if (eventDTO.getEndDate() != null && eventDTO.getStartDate() != null && eventDTO.getEndDate().isBefore(eventDTO.getStartDate())) {
            errors.rejectValue("endDate", "endDate.invalid", "End date must be after start date");
        }
    }
}
