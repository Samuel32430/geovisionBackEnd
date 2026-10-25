package pe.edu.upeu.geovisionBackEnd.entity.controlInventario;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

/**
 * Subtipo para armazones y monturas.
 * Mapea la tabla MONTURA de Oracle 19c+.
 * Hereda la clave primaria de ITEM mediante clave compartida (id_item).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "MONTURA")
public class Montura {

    @Id
    @Column(name = "id_item")
    @EqualsAndHashCode.Include
    private Long idItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    @Builder.Default
    private TipoItem tipo = TipoItem.MONTURA;

    @Column(name = "color", length = 100)
    private String color;

    @Column(name = "medidas", length = 200)
    private String medidas;

    @Column(name = "id_modelo_montura")
    private Long idModeloMontura;

    @Column(name = "id_material_montura")
    private Long idMaterialMontura;

    @PrePersist
    @PreUpdate
    public void validarRestricciones() {
        this.tipo = TipoItem.MONTURA;
    }
}
