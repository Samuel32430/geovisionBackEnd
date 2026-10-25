package pe.edu.upeu.geovisionBackEnd.entity.controlInventario;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.MotivoMovimiento;

import java.time.LocalDateTime;

/**
 * Registro histórico inmutable de toda entrada, salida o ajuste que altera las existencias de un ítem.
 * Mapea la tabla MOVIMIENTO_INVENTARIO de Oracle 19c+.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "MOVIMIENTO_INVENTARIO")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_inventario")
    @EqualsAndHashCode.Include
    private Long idMovimientoInventario;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false, length = 20)
    private MotivoMovimiento motivo;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "id_item", nullable = false)
    private Long idItem;

    @Column(name = "id_item_vendido")
    private Long idItemVendido;

    @Column(name = "id_empleado", nullable = false)
    private Long idEmpleado;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        validarReglasCheckDDL();
    }

    @PreUpdate
    public void preUpdate() {
        validarReglasCheckDDL();
    }

    /**
     * Valida las restricciones CHECK del DDL de Oracle:
     * - cantidad <> 0
     * - MOV_venta_ck: motivo <> 'VENTA' OR (cantidad < 0 AND id_item_vendido IS NOT NULL)
     * - MOV_entrada_ck: motivo NOT IN ('INICIAL','COMPRA','DEVOLUCION') OR cantidad > 0
     * - MOV_rotura_ck: motivo <> 'ROTURA' OR cantidad < 0
     */
    private void validarReglasCheckDDL() {
        if (this.cantidad == null || this.cantidad == 0) {
            throw new IllegalStateException("La cantidad del movimiento no puede ser nula ni cero.");
        }
        if (this.motivo != null) {
            switch (this.motivo) {
                case INICIAL, COMPRA, DEVOLUCION -> {
                    if (this.cantidad <= 0) {
                        throw new IllegalStateException(
                            "Para los motivos INICIAL, COMPRA o DEVOLUCION la cantidad debe ser estrictamente positiva (> 0)."
                        );
                    }
                }
                case ROTURA -> {
                    if (this.cantidad >= 0) {
                        throw new IllegalStateException(
                            "Para el motivo ROTURA la cantidad debe ser estrictamente negativa (< 0)."
                        );
                    }
                }
                case VENTA -> {
                    if (this.cantidad >= 0) {
                        throw new IllegalStateException(
                            "Para el motivo VENTA la cantidad debe ser estrictamente negativa (< 0)."
                        );
                    }
                    if (this.idItemVendido == null) {
                        throw new IllegalStateException(
                            "Para el motivo VENTA es obligatorio especificar id_item_vendido."
                        );
                    }
                }
                case AJUSTE -> {
                    // Puede ser positivo o negativo, pero ya validamos que no sea cero.
                }
            }
        }
    }
}
