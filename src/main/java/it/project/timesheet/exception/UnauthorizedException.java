package it.project.timesheet.exception;

import it.project.timesheet.exception.common.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {

    private final static int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedException(String message) {
        super(UNAUTHORIZED, message);
    }

    public UnauthorizedException(String message, Exception exception) {
        super(UNAUTHORIZED,message, exception);
    }

    public UnauthorizedException() {
        super(UNAUTHORIZED);
    }
}
