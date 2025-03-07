package it.project.timesheet.exception.custom;

import it.project.timesheet.exception.BadRequestException;

public class InconsistencyDatetimeException extends BadRequestException {

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
