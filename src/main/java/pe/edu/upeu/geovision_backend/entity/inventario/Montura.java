package pe.edu.upeu.geovision_backend.entity.inventario;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;

@Entity
@Table(name = "MONTURA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Montura {

    // >>>> FIELDS
    @Id
    @Column(name = "id_producto")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "color", length = 100)
    private String color;

    @Convert(converter = ModeloMonturaEnum.PersistenceConverter.class)
    @Column(name = "modelo")
    private ModeloMonturaEnum modelo;

    @Convert(converter = MaterialMonturaEnum.PersistenceConverter.class)
    @Column(name = "material")
    private MaterialMonturaEnum material;

    // >>>> OPERATIONS
    public Integer getModeloCodigo() {
        return this.modelo != null ? this.modelo.getCodigo() : null;
    }

    public void setModeloCodigo(Integer codigo) {
        this.modelo = ModeloMonturaEnum.fromCodigo(codigo);
    }

    public Integer getMaterialCodigo() {
        return this.material != null ? this.material.getCodigo() : null;
    }

    public void setMaterialCodigo(Integer codigo) {
        this.material = MaterialMonturaEnum.fromCodigo(codigo);
    }

}
