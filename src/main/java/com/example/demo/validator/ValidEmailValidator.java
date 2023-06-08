package com.example.demo.validator;

import com.example.demo.validator.annotation.ValidEmail;
import com.example.demo.repository.IUserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {
    @Autowired
    private IUserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context){
        if (userRepository == null)
            return true;
        return userRepository.findByEmail(email) == null;
    }
}