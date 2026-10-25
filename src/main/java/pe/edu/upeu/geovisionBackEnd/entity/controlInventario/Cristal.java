package pe.edu.upeu.geovisionBackEnd.entity.controlInventario;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "CRISTAL")
public class Cristal {

    @Id
    @Column(name = "id_item")
    @EqualsAndHashCode.Include
    private Long idItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    @Builder.Default
    private TipoItem tipo = TipoItem.CRISTAL;

    @Column(name = "indice_refraccion", precision = 5, scale = 3)
    private BigDecimal indiceRefraccion;

    @Column(name = "id_material_cristal", nullable = false)
    private Long idMaterialCristal;

    @PrePersist
    @PreUpdate
    public void validarRestricciones() {
        this.tipo = TipoItem.CRISTAL;
        // Restricción DDL: CRISTAL_indice_ck CHECK (indice_refraccion > 1)
        if (this.indiceRefraccion != null && this.indiceRefraccion.compareTo(BigDecimal.ONE) <= 0) {
            throw new IllegalStateException("El índice de refracción del cristal debe ser estrictamente mayor a 1.");
        }
    }
}
