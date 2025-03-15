package it.project.timesheet.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import it.project.timesheet.domain.entity.common.MysqlBaseEntity;
import it.project.timesheet.domain.enums.StatusDayEnum;
import it.project.timesheet.domain.enums.StatusHoursEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = {"timesheet"}) // Esclude relazioni
@Entity
@Table(name = "presence")
public class Presence extends MysqlBaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uuid_timesheet", referencedColumnName = "uuid")
    @JsonBackReference
    private Timesheet timesheet;

    @Column(name = "work_day")
    private LocalDate workDay;

    @Column(name = "entry_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime entryTime;

    @Column(name = "exit_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime exitTime;

    @Column(name = "description")
    private String description;

    @Column(name = "status_day")
    private String statusDayEnum;

    @Column(name = "status_hours")
    private String statusHoursEnum;

    @Column(name = "total_hours")
    private Double totalHours;

}
