package org.shoulder.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {
        HttpStatus status;

        switch (e.getErrorCode()) {
            case "EMAIL_NOT_FOUND" :
                status = HttpStatus.NOT_FOUND;
                break;
            case "EMAIL_DUPLICATE" :
                status = HttpStatus.CONFLICT;
                break;
            default :
                status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(e.getMessage());
    }
}
