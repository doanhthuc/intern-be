package com.mgmtp.easyquizy.validator;

import com.mgmtp.easyquizy.dto.question.QuestionDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

@Component
public class QuestionDTOValidator implements Validator {
    private static final List<Integer> VALID_TIME_LIMIT = Arrays.asList(10, 20, 30, 60, 90, 120, 180, 240);
    @Override
    public boolean supports(Class<?> clazz) {
        return QuestionDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        QuestionDTO questionDTOs = (QuestionDTO) target;

        if (!VALID_TIME_LIMIT.contains(questionDTOs.getTimeLimit())){
            errors.rejectValue("timeLimit", "timeLimit.invalid", "Invalid time limit. It must be one of the following values: [10, 20, 30, 60, 90, 120, 180, 240]!");
        }

        if (questionDTOs.getAnswers() == null) {
            errors.rejectValue("answers", "answers.required", "Please provide at least one answer!!!");
            return;
        }

        long count = questionDTOs.getAnswers().stream()
                .filter(answer -> answer.getIsCorrect() != null && answer.getIsCorrect())
                .count();

        if (count > 1) {
            errors.rejectValue("answers", "isCorrect.invalid", "Please select exactly one correct answer!!!");
        }
    }
}
