package pe.edu.upeu.geovisionBackEnd.entity.controlInventario;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla intermedia que asocia los tratamientos aplicados a un cristal específico.
 * Mapea la tabla CRISTAL_TRATAMIENTO de Oracle 19c+.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
    name = "CRISTAL_TRATAMIENTO",
    uniqueConstraints = {
        @UniqueConstraint(name = "CRISTAL_TRAT_par_uq", columnNames = {"id_cristal", "id_tratamiento"})
    }
)
public class CristalTratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cristal_tratamiento")
    @EqualsAndHashCode.Include
    private Long idCristalTratamiento;

    @Column(name = "id_cristal", nullable = false)
    private Long idCristal;

    @Column(name = "id_tratamiento", nullable = false)
    private Long idTratamiento;
}
