package it.project.timesheet.exception.custom;

import it.project.timesheet.exception.BadRequestException;

public class ObjectNotFoundException extends BadRequestException {

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Exception exception) {
        super(message, exception);
    }

    public ObjectNotFoundException() {
        super();
    }
}
