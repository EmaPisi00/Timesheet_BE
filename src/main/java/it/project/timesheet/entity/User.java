package it.project.timesheet.entity;

import it.project.timesheet.entity.common.MysqlBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "user")
public class User extends MysqlBaseEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;
    
}
