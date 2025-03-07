package it.project.timesheet.exception.custom;

import it.project.timesheet.exception.InternalServerErrorException;

public class InconsistencyDatetimeException extends InternalServerErrorException {

    public InconsistencyDatetimeException(String message) {
        super(message);
    }

    public InconsistencyDatetimeException(String message, Exception exception) {
        super(message, exception);
    }

    public InconsistencyDatetimeException() {
        super();
    }
}
