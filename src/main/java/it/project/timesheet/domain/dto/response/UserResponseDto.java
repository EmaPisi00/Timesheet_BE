package it.project.timesheet.domain.dto.response;

import it.project.timesheet.domain.enums.RoleEnum;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserResponseDto {
    private UUID uuidUser;
    private String email;
    //private String password;

    private UUID uuidEmployee;
    private String name;
    private String surname;

    private RoleEnum role;
}
