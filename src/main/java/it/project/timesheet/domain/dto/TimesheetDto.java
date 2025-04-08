package it.project.timesheet.domain.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TimesheetDto {

    private UUID uuidUser;

    private UUID uuidTimesheet;

    private String name;

    private String surname;

    private Integer month;

    private Integer year;

    private Boolean locked;
}
