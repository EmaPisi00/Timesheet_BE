package it.project.timesheet.mapper;

import it.project.timesheet.domain.dto.TimesheetDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.Timesheet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TimesheetMapper {
    TimesheetMapper INSTANCE = Mappers.getMapper(TimesheetMapper.class);

    @Mapping(source = "timesheet.uuid", target = "uuidTimesheet")
    @Mapping(source = "employee.user.uuid", target = "uuidUser")
    @Mapping(source = "employee.name", target = "name")
    @Mapping(source = "employee.surname",target = "surname")
    TimesheetDto convertTimesheetToTimesheetDto(Timesheet timesheet, Employee employee);
}
