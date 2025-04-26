package com.example.GuitarApp.util;

import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorUtils {
    public static String generateFieldErrorMessage(List<FieldError> fieldErrors){
        StringBuilder message = new StringBuilder();
        for(FieldError error: fieldErrors ){
            message
                    .append("error caused by field ")
                    .append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage() == null ? error.getCode() : error.getDefaultMessage())
                    .append(";  ");
        }
        return message.toString();
    }

    public static String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        return throwable.toString() + "\n" +
                Arrays.stream(throwable.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n"));
    }
}
