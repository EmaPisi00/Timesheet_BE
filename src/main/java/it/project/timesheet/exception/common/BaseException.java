package it.project.timesheet.exception.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BaseException extends Exception{

    private int code;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String message, Exception exception) {
        super(message, exception);
        this.code = code;
    }

    public BaseException(int code){
        this.code = code;
    }
}
