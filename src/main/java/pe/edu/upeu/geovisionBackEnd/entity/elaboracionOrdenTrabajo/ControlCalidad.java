package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONTROL_CALIDAD")
public class ControlCalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_control_calidad")
    private Long idControlCalidad;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = "EN_REVISION";

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @Column(name = "aprobado", nullable = false)
    private Boolean aprobado;

    @Column(name = "id_envio_laboratorio", nullable = false)
    private Long idEnvioLaboratorio;

    @Column(name = "id_empleado", nullable = false)
    private Long idEmpleado;

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}
