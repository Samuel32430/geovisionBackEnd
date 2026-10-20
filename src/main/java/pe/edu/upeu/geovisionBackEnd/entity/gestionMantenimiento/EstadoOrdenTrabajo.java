package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ESTADO_ORDEN_TRABAJO")
public class EstadoOrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_orden_trabajo")
    private Long idEstadoOrdenTrabajo;

    @Column(name = "nombre", nullable = false, length = 200, unique = true)
    private String nombre;
}
