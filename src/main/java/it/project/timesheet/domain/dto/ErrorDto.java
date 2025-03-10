package it.project.timesheet.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ErrorDto {

    private int code;
    private String message;

}
