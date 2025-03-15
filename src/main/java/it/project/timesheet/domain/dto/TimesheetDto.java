package it.project.timesheet.domain.dto;

import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.User;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TimesheetDto {

    private UUID uuidUser;

    private String name;

    private String surname;

    private Integer month;

    private Integer year;
}
