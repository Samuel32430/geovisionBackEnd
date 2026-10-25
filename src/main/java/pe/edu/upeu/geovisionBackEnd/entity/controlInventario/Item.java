package pe.edu.upeu.geovisionBackEnd.entity.controlInventario;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.UnidadVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad principal que representa el catálogo general de ítems (productos, monturas y cristales).
 * Mapea la tabla ITEM de Oracle 19c+.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
    name = "ITEM",
    uniqueConstraints = {
        @UniqueConstraint(name = "ITEM_tipo_uq", columnNames = {"id_item", "tipo"})
    }
)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    @EqualsAndHashCode.Include
    private Long idItem;

    @Column(name = "codigo", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 14, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoItem tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_venta", nullable = false, length = 10)
    private UnidadVenta unidadVenta;

    @Column(name = "controla_stock", nullable = false)
    @Builder.Default
    private Boolean controlaStock = true;

    @Column(name = "estado", nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria;

    @Column(name = "id_marca")
    private Long idMarca;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = true;
        }
        if (this.stock == null) {
            this.stock = 0;
        }
        if (this.controlaStock == null) {
            this.controlaStock = (this.tipo != TipoItem.CRISTAL);
        }
        validarReglasNegocioYCheck();
    }

    @PreUpdate
    public void preUpdate() {
        validarReglasNegocioYCheck();
    }

    /**
     * Valida y asegura el cumplimiento de las restricciones CHECK del DDL de Oracle:
     * - ITEM_cristal_stock_ck: tipo <> 'CRISTAL' OR controla_stock = 0
     * - ITEM_stock_cristal_ck: controla_stock = 1 OR stock = 0
     * - precio >= 0
     * - stock >= 0
     */
    private void validarReglasNegocioYCheck() {
        if (this.tipo == TipoItem.CRISTAL) {
            this.controlaStock = false;
            this.stock = 0;
        }
        if (Boolean.FALSE.equals(this.controlaStock)) {
            this.stock = 0;
        }
        if (this.stock != null && this.stock < 0) {
            throw new IllegalStateException("El stock del ítem no puede ser negativo.");
        }
        if (this.precio != null && this.precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("El precio del ítem no puede ser negativo.");
        }
    }
}
