package pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.Servicio;

import java.math.BigDecimal;

@Entity
@Table(name = "SERVICIO_VENDIDO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicioVendido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio_vendido")
    private Long idServicioVendido;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @Column(name = "precio", nullable = false, precision = 14, scale = 2)
    private BigDecimal precio;

    @Column(name = "sub_total", insertable = false, updatable = false, precision = 14, scale = 2)
    private BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @PrePersist
    public void prePersist() {
        if (cantidad == null) {
            cantidad = 1;
        }
    }
}
