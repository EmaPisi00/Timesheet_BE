package it.project.timesheet.domain.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ErrorResponseDto {

    private int code;
    private String message;

}
