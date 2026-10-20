package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "PERSONA")
@Check(constraints = "sexo in (0,1)")
public class Persona {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    @NotBlank @Size(max = 200)
    @Column(name = "nombres", nullable = false, length = 200)
    private String nombres;

    @NotBlank @Size(max = 200)
    @Column(name = "apellidos", nullable = false, length = 200)
    private String apellidos;

    @Size(max = 50) @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Min(0) @Max(1) @Column(name = "sexo", precision = 1)
    private Integer sexo;

    @Size(max = 500) @Column(name = "direccion", length = 500)
    private String direccion;
}
