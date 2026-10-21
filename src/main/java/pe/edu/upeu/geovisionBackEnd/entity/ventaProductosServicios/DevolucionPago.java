package pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DEVOLUCION_PAGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion_pago")
    private Long idDevolucionPago;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "monto", nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    @Column(name = "motivo", nullable = false, length = 1000)
    private String motivo;

    @Column(name = "referencia_operacion", length = 200)
    private String referenciaOperacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}
