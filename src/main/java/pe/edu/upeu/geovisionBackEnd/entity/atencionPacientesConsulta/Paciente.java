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
@Table(name = "PACIENTE")
@Check(constraints = "estado in (0,1)")
public class Paciente {
    @Id @Column(name = "id_persona")
    private Long idPersona;

    @NotNull @MapsId @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @NotNull @Min(0) @Max(1) @Column(name = "estado", nullable = false, precision = 1)
    private Integer estado = 1;

    @NotNull @Column(name = "fecha_registro", nullable = false, columnDefinition = "DATE")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
