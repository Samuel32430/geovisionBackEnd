package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.Laboratorio;
import pe.edu.upeu.geovisionBackEnd.enums.elaboracionOrdenTrabajo.MotivoEnvioLaboratorio;

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

    @Column(name = "fecha_envio", nullable = false, columnDefinition = "DATE")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(name = "fecha_recepcion", columnDefinition = "DATE")
    private LocalDateTime fechaRecepcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false, length = 20)
    private MotivoEnvioLaboratorio motivo = MotivoEnvioLaboratorio.FABRICACION;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "costo_optica", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoOptica = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orden_trabajo", nullable = false)
    private OrdenTrabajo ordenTrabajo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_laboratorio", nullable = false)
    private Laboratorio laboratorio;

    @JsonIgnore
    public boolean isFechasValidas() {
        return fechaRecepcion == null || fechaEnvio == null || !fechaRecepcion.isBefore(fechaEnvio);
    }

    @PrePersist
    void prePersist() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
        validarFechas();
    }

    @PreUpdate
    void validarFechas() {
        if (!isFechasValidas()) {
            throw new IllegalArgumentException("La recepcion no puede ser anterior al envio");
        }
    }
}
