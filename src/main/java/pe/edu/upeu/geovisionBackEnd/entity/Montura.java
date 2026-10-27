package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.enums.MaterialMonturaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.ModeloMonturaEnum;

@Entity
@Table(name = "MONTURA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Montura {

    @Id
    @Column(name = "id_producto")
    private Long idProducto;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "color", length = 100)
    private String color;

    @Convert(converter = ModeloMonturaConverter.class)
    @Column(name = "modelo")
    private ModeloMonturaEnum modelo;

    @Convert(converter = MaterialMonturaConverter.class)
    @Column(name = "material")
    private MaterialMonturaEnum material;

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

    @Converter
    public static class ModeloMonturaConverter implements AttributeConverter<ModeloMonturaEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ModeloMonturaEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public ModeloMonturaEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? ModeloMonturaEnum.fromCodigo(dbData) : null;
        }
    }

    @Converter
    public static class MaterialMonturaConverter implements AttributeConverter<MaterialMonturaEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(MaterialMonturaEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public MaterialMonturaEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? MaterialMonturaEnum.fromCodigo(dbData) : null;
        }
    }
}
