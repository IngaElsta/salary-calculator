package com.github.ingaelsta.salarycalculator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ConstantValueMissingException extends RuntimeException {
    public ConstantValueMissingException(String message) {
        super(message);
    }
}
