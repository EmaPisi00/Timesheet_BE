package it.project.timesheet.exception.custom;

import it.project.timesheet.exception.InternalServerErrorException;

public class ObjectFoundException extends InternalServerErrorException {

    public ObjectFoundException(String message) {
        super(message);
    }

    public ObjectFoundException(String message, Exception exception) {
        super(message, exception);
    }

    public ObjectFoundException() {
        super();
    }
}
