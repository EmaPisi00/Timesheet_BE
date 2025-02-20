package it.project.timesheet.exception;

import it.project.timesheet.exception.common.BaseException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {

    private final static int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();

    public BadRequestException(String message) {
        super(BAD_REQUEST, message);
    }

    public BadRequestException(String message, Exception exception) {
        super(BAD_REQUEST,message, exception);
    }

    public BadRequestException() {
        super(BAD_REQUEST);
    }
}
