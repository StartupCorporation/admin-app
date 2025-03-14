package com.deye.web.exception.dlq;

public class ActionNotAllowedSkipDLQException extends SkipDLQException {
    public ActionNotAllowedSkipDLQException(String message) {
        super(message);
    }
}
