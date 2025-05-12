package com.example.GuitarApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ErrorMessageService {

    private final MessageSource messageSource;

    @Autowired
    public ErrorMessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public String getErrorMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
