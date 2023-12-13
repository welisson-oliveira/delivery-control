package com.acert.deliverycontrol.application.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Slf4j
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(final String message) {
        super(message);
        DataNotFoundException.log.warn(message);
    }
}

