package it.project.timesheet.exception;

import it.project.timesheet.exception.common.BaseException;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseException {

    private final static int INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public InternalServerErrorException(String message) {
        super(INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerErrorException(String message, Exception exception) {
        super(INTERNAL_SERVER_ERROR,message, exception);
    }

    public InternalServerErrorException() {
        super(INTERNAL_SERVER_ERROR);
    }
}
