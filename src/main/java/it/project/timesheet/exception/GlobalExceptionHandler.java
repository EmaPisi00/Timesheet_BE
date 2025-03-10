package it.project.timesheet.exception;

import it.project.timesheet.domain.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException badRequestException) {
        return ResponseEntity.ok(
                new ErrorDto(
                        badRequestException.getCode(),
                        badRequestException.getMessage())
        );
    }

    @ExceptionHandler({InternalServerErrorException.class})
    public ResponseEntity<ErrorDto> handleInternalServerErrorException(InternalServerErrorException internalServerErrorException) {
        return ResponseEntity.ok(
                new ErrorDto(
                        internalServerErrorException.getCode(),
                        internalServerErrorException.getMessage())
        );
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<ErrorDto> handleUnauthorizedException(UnauthorizedException unauthorizedException) {
        return ResponseEntity.ok(
                new ErrorDto(
                        unauthorizedException.getCode(),
                        unauthorizedException.getMessage())
        );
    }
}
