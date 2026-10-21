package pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.EstadoComprobante;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMPROBANTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long idComprobante;

    @Column(name = "serie", nullable = false, length = 20)
    private String serie;

    @Column(name = "numero", nullable = false, length = 30)
    private String numero;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "total_emitido", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalEmitido = BigDecimal.ZERO;

    @Column(name = "estado_documento", nullable = false, length = 10)
    private String estadoDocumento = "EMITIDO";

    @Column(name = "fecha_anulacion")
    private LocalDate fechaAnulacion;

    @Column(name = "motivo_anulacion", length = 1000)
    private String motivoAnulacion;

    @Column(name = "estado_cobro", nullable = false, length = 15)
    private String estadoCobro = "PENDIENTE";

    @Column(name = "fecha_cancelacion")
    private LocalDate fechaCancelacion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false, unique = true)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado_comprobante", nullable = false)
    private EstadoComprobante estadoComprobante;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (fechaEmision == null) {
            fechaEmision = LocalDate.now();
        }
        if (estadoDocumento == null) {
            estadoDocumento = "EMITIDO";
        }
        if (estadoCobro == null) {
            estadoCobro = "PENDIENTE";
        }
    }

    public void agregarPago(Pago pago) {
        pagos.add(pago);
        pago.setComprobante(this);
    }
}
