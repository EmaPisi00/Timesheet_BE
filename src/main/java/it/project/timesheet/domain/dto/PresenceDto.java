package it.project.timesheet.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.project.timesheet.domain.enums.TypeDayEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PresenceDto {

    private LocalDate workDay;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime entryTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime exitTime;

    private TypeDayEnum typeDayEnum;
}
