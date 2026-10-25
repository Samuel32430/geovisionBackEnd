package pe.edu.upeu.geovisionBackEnd.entity.controlInventario;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.EstadoReserva;

import java.time.LocalDateTime;

/**
 * Bloqueo temporal de existencias durante el proceso de venta para evitar sobreventas concurrentes.
 * Mapea la tabla RESERVA_INVENTARIO de Oracle 19c+.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "RESERVA_INVENTARIO")
public class ReservaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_inventario")
    @EqualsAndHashCode.Include
    private Long idReservaInventario;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.ACTIVA;

    @Column(name = "id_item_vendido", nullable = false)
    private Long idItemVendido;

    @Column(name = "id_item", nullable = false)
    private Long idItem;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoReserva.ACTIVA;
        }
        validarRestricciones();
    }

    @PreUpdate
    public void preUpdate() {
        validarRestricciones();
    }

    private void validarRestricciones() {
        if (this.cantidad == null || this.cantidad <= 0) {
            throw new IllegalStateException("La cantidad de la reserva debe ser estrictamente positiva (> 0).");
        }
    }
}
