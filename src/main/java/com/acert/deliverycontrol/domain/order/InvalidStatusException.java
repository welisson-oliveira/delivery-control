package com.acert.deliverycontrol.domain.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStatusException extends IllegalArgumentException {
    public InvalidStatusException(final String from, final String to) {
        super("can't change from: " + from + "to: " + to);
    }

}
