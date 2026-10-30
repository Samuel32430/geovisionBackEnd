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
import pe.edu.upeu.geovisionBackEnd.enums.SexoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoDocumentoEnum;

import java.time.LocalDate;

@Entity
@Table(name = "PERSONA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    @Column(name = "nombres", nullable = false, length = 150)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 150)
    private String apellidos;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Convert(converter = SexoConverter.class)
    @Column(name = "sexo")
    private SexoEnum sexo;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 50)
    private String numeroDocumento;

    @Convert(converter = TipoDocumentoConverter.class)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumentoEnum tipoDocumento;

    public Integer getSexoCodigo() {
        return this.sexo != null ? this.sexo.getCodigo() : null;
    }

    public void setSexoCodigo(Integer codigo) {
        this.sexo = SexoEnum.fromCodigo(codigo);
    }

    public Integer getTipoDocumentoCodigo() {
        return this.tipoDocumento != null ? this.tipoDocumento.getCodigo() : null;
    }

    public void setTipoDocumentoCodigo(Integer codigo) {
        this.tipoDocumento = TipoDocumentoEnum.fromCodigo(codigo);
    }

    @Converter
    public static class SexoConverter implements AttributeConverter<SexoEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(SexoEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public SexoEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? SexoEnum.fromCodigo(dbData) : null;
        }
    }

    @Converter
    public static class TipoDocumentoConverter implements AttributeConverter<TipoDocumentoEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TipoDocumentoEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public TipoDocumentoEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? TipoDocumentoEnum.fromCodigo(dbData) : null;
        }
    }
}
