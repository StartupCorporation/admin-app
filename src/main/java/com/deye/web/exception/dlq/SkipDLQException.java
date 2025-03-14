package com.deye.web.exception.dlq;

public class SkipDLQException extends RuntimeException {
    public SkipDLQException(String message) {
        super(message);
    }
}
