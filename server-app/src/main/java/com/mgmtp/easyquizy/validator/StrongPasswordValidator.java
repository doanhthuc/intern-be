package com.mgmtp.easyquizy.validator;

import com.mgmtp.easyquizy.model.auth.ChangePasswordRequest;
import io.micrometer.core.instrument.util.StringUtils;
import org.passay.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

@Component
public class StrongPasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePasswordRequest request = (ChangePasswordRequest) target;
        if (StringUtils.isEmpty(((ChangePasswordRequest) target).getNewPassword())) {
            errors.rejectValue("newPassword", "password.empty", "This is a required field");
            return;
        }
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8,255),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase,1),
                new CharacterRule(EnglishCharacterData.Digit,1),
                new CharacterRule(EnglishCharacterData.Special,1)
        ));
        RuleResult result = validator.validate(new PasswordData(request.getNewPassword()));

        if (!result.isValid()) {
            errors.rejectValue("newPassword", "password.invalid","New password must contain at least 8 characters, including one uppercase letter, one lowercase letter, and one digit, one special character.");
        }
    }
}