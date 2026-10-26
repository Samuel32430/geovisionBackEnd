package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RECETA", uniqueConstraints =
    @UniqueConstraint(name = "RECETA_examen_uq", columnNames = {"id_receta", "id_examen"})
)
@Check(constraints = "es_externo in (0,1) and (fecha_de_entrega is null or fecha_de_entrega >= fecha_registro)")
public class Receta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta")
    private Long idReceta;

    @NotNull @Column(name = "fecha_registro", nullable = false, columnDefinition = "DATE")
    private LocalDateTime fechaRegistro = LocalDateTime.now().withNano(0);

    @Size(max = 2000) @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @Column(name = "fecha_de_entrega", columnDefinition = "DATE")
    private LocalDateTime fechaDeEntrega;

    @NotNull @Min(0) @Max(1) @Column(name = "es_externo", nullable = false, precision = 1)
    private Integer esExterno = 0;

    @NotNull @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_examen", nullable = false, unique = true)
    private Examen examen;

    /** Employee ID owned by controlAccesos. */
    @NotNull @Positive @Column(name = "id_empleado_registro", nullable = false)
    private Long idEmpleadoRegistro;

    @AssertTrue(message = "La fecha de entrega no puede ser anterior al registro")
    public boolean isFechaEntregaValida() {
        return fechaDeEntrega == null || fechaRegistro == null || !fechaDeEntrega.isBefore(fechaRegistro);
    }
}
