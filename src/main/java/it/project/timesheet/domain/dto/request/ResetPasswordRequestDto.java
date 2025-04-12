package it.project.timesheet.domain.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ResetPasswordRequestDto {

    private String password;
    private String repeatPassword;
}
