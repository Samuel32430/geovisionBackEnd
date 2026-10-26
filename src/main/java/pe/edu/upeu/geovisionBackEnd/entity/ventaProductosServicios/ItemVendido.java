package pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Item;

import java.math.BigDecimal;

@Entity
@Table(name = "ITEM_VENDIDO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_vendido")
    private Long idItemVendido;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unit", precision = 14, scale = 2)
    private BigDecimal precioUnit;

    @Column(name = "descuento", nullable = false, precision = 14, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "sub_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lente")
    private Lente lente;

    @PrePersist
    public void prePersist() {
        if (descuento == null) {
            descuento = BigDecimal.ZERO;
        }
        if (subTotal == null) {
            subTotal = precioUnit != null && cantidad != null
                    ? precioUnit.multiply(BigDecimal.valueOf(cantidad)).subtract(descuento)
                    : BigDecimal.ZERO;
        }
    }
}
