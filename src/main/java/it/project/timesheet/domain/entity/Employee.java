package it.project.timesheet.domain.entity;

import it.project.timesheet.domain.entity.common.MysqlBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "employee")
public class Employee extends MysqlBaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uuid_user", referencedColumnName = "uuid")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String surname;
    
}
