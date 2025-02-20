package it.project.timesheet.domain.entity.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder
@MappedSuperclass
public class MysqlBaseEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    private LocalDateTime deletedAt;

    // Metodo che viene chiamato prima del salvataggio dell'entità (INSERT)
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();  // Imposta la data/ora di creazione
        this.updatedAt = LocalDateTime.now();  // Imposta la data/ora di aggiornamento iniziale
    }

    // Metodo che viene chiamato prima di ogni aggiornamento (UPDATE)
    @PreUpdate
    public void preUpdate() {
        if (!isDeleted()) {
            this.updatedAt = LocalDateTime.now();  // Imposta la data/ora di aggiornamento
        }
    }

    @JsonIgnore
    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void deleted() {
        if (!isDeleted()) {
            deletedAt = LocalDateTime.now();
        }
    }
}
