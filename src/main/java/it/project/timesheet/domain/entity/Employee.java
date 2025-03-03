package it.project.timesheet.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.project.timesheet.domain.entity.common.MysqlBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = {"user"}) // Esclude relazioni
@Entity
@Table(name = "employee")
public class Employee extends MysqlBaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uuid_user", referencedColumnName = "uuid")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Timesheet> timesheetList;
}
