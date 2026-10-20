package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ENVIO_LABORATORIO")
public class EnvioLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio_laboratorio")
    private Long idEnvioLaboratorio;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "motivo", nullable = false, length = 20)
    private String motivo = "FABRICACION";

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "costo_optica", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoOptica = BigDecimal.ZERO;

    @Column(name = "id_orden_trabajo", nullable = false)
    private Long idOrdenTrabajo;

    @Column(name = "id_laboratorio", nullable = false)
    private Long idLaboratorio;

    @PrePersist
    void prePersist() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
    }
}
