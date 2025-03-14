package com.deye.web.exception;

import com.deye.web.util.error.ErrorCodeUtils;
import lombok.Getter;

@Getter
public abstract class CommonException extends RuntimeException {
    private final Integer code;

    public CommonException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public CommonException(Throwable throwable) {
        super(throwable);
        code = ErrorCodeUtils.COMMON_ERROR_CODE;
    }

    public CommonException(String message) {
        super(message);
        code = null;
    }
}
