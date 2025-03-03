package it.project.timesheet.domain.dto;

import it.project.timesheet.domain.entity.Presence;
import it.project.timesheet.domain.entity.Timesheet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimesheetDto {

    private Timesheet timesheet;

    private List<Presence> presenceList;
}
