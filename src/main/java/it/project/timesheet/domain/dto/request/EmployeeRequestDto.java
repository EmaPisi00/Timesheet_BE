package it.project.timesheet.domain.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class EmployeeRequestDto {

    private String email;
    private String password;

    private String name;
    private String surname;
}
