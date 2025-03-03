package it.project.timesheet.domain.dto;

import it.project.timesheet.domain.entity.Timesheet;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RequestTimesheetDto {

    private TimesheetDto timesheetDto;

    private List<PresenceDto> presenceList;
}
