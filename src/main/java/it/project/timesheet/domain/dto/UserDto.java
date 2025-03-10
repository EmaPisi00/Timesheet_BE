package it.project.timesheet.domain.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserDto {
    private UUID uuid;
    private String email;
    private String password;
}
