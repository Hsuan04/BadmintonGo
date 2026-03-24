package com.court.badmintongo.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(IReturnCode returnCode) {
        super(returnCode.getMessage());
        this.code = returnCode.getCode();
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}
