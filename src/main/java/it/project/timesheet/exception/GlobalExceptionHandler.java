package it.project.timesheet.exception;

import it.project.timesheet.domain.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorDto> handleStudentNotFoundException(BadRequestException badRequestException) {
        return ResponseEntity.ok(
                new ErrorDto(
                        badRequestException.getCode(),
                        badRequestException.getMessage())
        );
    }
}
