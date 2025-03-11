package it.project.timesheet.domain.dto.request;

import it.project.timesheet.domain.enums.RoleEnum;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserRequestDto {
    private UUID uuid;
    private String email;
    private String password;
    private RoleEnum role;
}
