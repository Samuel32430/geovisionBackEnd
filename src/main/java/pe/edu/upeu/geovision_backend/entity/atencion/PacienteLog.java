package pe.edu.upeu.geovision_backend.entity.atencion;

import pe.edu.upeu.geovision_backend.entity.security.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PACIENTE_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PacienteLog {

    // >>>> FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente_log")
    private Long id;

    @Column(name = "fecha_log", nullable = false, columnDefinition = "DATE")
    private LocalDateTime fechaLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Paciente paciente;

    // >>>> LIFECYCLE
    @PrePersist
    public void prePersist() {
        if (this.fechaLog == null) {
            this.fechaLog = LocalDateTime.now().withNano(0);
        }
    }
}
