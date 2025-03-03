package it.project.timesheet.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.project.timesheet.domain.entity.common.MysqlBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = {"employee"}) // Esclude relazioni
@Entity
@Table(name = "timesheet")
public class Timesheet extends MysqlBaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uuid_employee", referencedColumnName = "uuid")
    @JsonBackReference
    private Employee employee;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Presence> presenceList;
}
