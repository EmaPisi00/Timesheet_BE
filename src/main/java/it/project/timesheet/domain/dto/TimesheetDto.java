package it.project.timesheet.domain.dto;

import it.project.timesheet.domain.entity.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TimesheetDto {

    private User user;

    private String name;

    private String surname;

    private Integer month;

    private Integer year;
}
