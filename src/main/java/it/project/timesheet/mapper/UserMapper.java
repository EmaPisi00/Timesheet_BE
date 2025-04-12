package it.project.timesheet.mapper;

import it.project.timesheet.domain.dto.response.UserResponseDto;
import it.project.timesheet.domain.entity.Employee;
import it.project.timesheet.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "employee.name", target = "name")
    @Mapping(source = "employee.surname",target = "surname")
    @Mapping(source = "employee.uuid", target = "uuidEmployee")
    @Mapping(source = "user.uuid", target = "uuidUser")
    @Mapping(source = "user.role", target = "role")
    UserResponseDto convertUserToUserDto(User user, Employee employee);
}
