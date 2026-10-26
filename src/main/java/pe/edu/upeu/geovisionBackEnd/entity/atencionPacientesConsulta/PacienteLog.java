package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PACIENTE_LOG")
public class PacienteLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente_log")
    private Long idPacienteLog;

    @NotNull @Column(name = "fecha_log", nullable = false, columnDefinition = "DATE")
    private LocalDateTime fechaLog = LocalDateTime.now().withNano(0);

    /** User ID owned by controlAccesos. */
    @NotNull @Positive @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Paciente paciente;
}
