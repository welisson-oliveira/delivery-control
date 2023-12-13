package com.acert.deliverycontrol.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class InvalidStatusException extends IllegalArgumentException {
    public InvalidStatusException(final String from, final String to) {
        super("can't change from: " + from + " to: " + to);
        InvalidStatusException.log.warn("can't change from: " + from + " to: " + to);
    }

}
