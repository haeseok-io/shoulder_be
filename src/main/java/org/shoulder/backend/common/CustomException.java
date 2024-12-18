package org.shoulder.backend.common;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final String errorCode;
    private final String target;
    private final String message;

    public CustomException(String errorCode, String target, String message) {
        super(message);
        this.errorCode = errorCode;
        this.target = target;
        this.message = message;
    }
}
