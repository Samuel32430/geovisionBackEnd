package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "EXAMEN")
@Check(constraints = "es_externo in (0,1)")
public class Examen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen")
    private Long idExamen;

    @NotNull @Column(name = "fecha_registro", nullable = false, columnDefinition = "DATE")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @NotNull @Min(0) @Max(1) @Column(name = "es_externo", nullable = false, precision = 1)
    private Integer esExterno = 0;

    @Size(max = 200) @Column(name = "profesional_externo", length = 200)
    private String profesionalExterno;

    @Size(max = 1000) @Column(name = "documento_externo", length = 1000)
    private String documentoExterno;

    @Size(max = 2000) @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    /** Employee ID owned by controlAccesos. */
    @NotNull @Positive @Column(name = "id_empleado_registro", nullable = false)
    private Long idEmpleadoRegistro;
}
