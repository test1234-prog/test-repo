package uz.momoit.makesense_dbridge.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BadRequestAlertException.class)
    public ResponseEntity<ErrRespModel> handleException(BadRequestAlertException exception) {
        return ResponseEntity.badRequest()
                .body(ErrRespModel.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .detail(exception.getDefaultMessage())
                        .entityName(exception.getEntityName())
                        .errorKey(exception.getErrorKey())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
