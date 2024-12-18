package org.shoulder.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("target", e.getTarget());
        resultMap.put("message", e.getMessage());

        return ResponseEntity.status(status).body(resultMap);
    }
}
