package it.project.timesheet.exception;

import it.project.timesheet.domain.dto.response.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(BadRequestException badRequestException) {
        return ResponseEntity.ok(
                new ErrorResponseDto(
                        badRequestException.getCode(),
                        badRequestException.getMessage())
        );
    }

    @ExceptionHandler({InternalServerErrorException.class})
    public ResponseEntity<ErrorResponseDto> handleInternalServerErrorException(InternalServerErrorException internalServerErrorException) {
        return ResponseEntity.ok(
                new ErrorResponseDto(
                        internalServerErrorException.getCode(),
                        internalServerErrorException.getMessage())
        );
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedException(UnauthorizedException unauthorizedException) {
        return ResponseEntity.ok(
                new ErrorResponseDto(
                        unauthorizedException.getCode(),
                        unauthorizedException.getMessage())
        );
    }
}
