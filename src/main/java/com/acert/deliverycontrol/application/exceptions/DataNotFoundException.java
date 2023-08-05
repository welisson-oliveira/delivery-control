package com.acert.deliverycontrol.application.exceptions;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(final String message) {
        super(message);
    }
}

