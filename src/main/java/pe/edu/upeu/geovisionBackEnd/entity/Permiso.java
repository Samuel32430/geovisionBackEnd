package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.enums.ModuloEnum;

@Entity
@Table(name = "PERMISO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Convert(converter = ModuloConverter.class)
    @Column(name = "modulo")
    private ModuloEnum modulo;

    public Integer getModuloCodigo() {
        return this.modulo != null ? this.modulo.getCodigo() : null;
    }

    public void setModuloCodigo(Integer codigo) {
        this.modulo = ModuloEnum.fromCodigo(codigo);
    }

    @Converter
    public static class ModuloConverter implements AttributeConverter<ModuloEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ModuloEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public ModuloEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? ModuloEnum.fromCodigo(dbData) : null;
        }
    }
}
