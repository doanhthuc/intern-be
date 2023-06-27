package com.mgmtp.easyquizy.validator;

import com.mgmtp.easyquizy.dto.AnswerDTO;
import com.mgmtp.easyquizy.dto.QuestionDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class QuestionDTOValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return QuestionDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        QuestionDTO questionDTOs = (QuestionDTO) target;
        long count = questionDTOs.getAnswers().stream()
                .filter(AnswerDTO::getIsCorrect)
                .count();
        if(count != 1) {
            errors.rejectValue("answers", "isCorrect.invalid", "Please select exactly one correct answer!!!");
        }
    }
}
