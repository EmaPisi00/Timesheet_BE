package it.project.timesheet.domain.dto.request;

import it.project.timesheet.domain.dto.PresenceDto;
import it.project.timesheet.domain.dto.TimesheetDto;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class TimesheetRequestDto {

    private TimesheetDto timesheetDto;

    private List<PresenceDto> presenceList;
}
