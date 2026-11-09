package pe.edu.upeu.geovision_backend.entity.atencion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovision_backend.enums.atencion.SexoEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;

import java.time.LocalDate;

@Entity
@Table(name = "PERSONA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    // >>>> FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long id;

    @Column(name = "nombres", nullable = false, length = 150)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 150)
    private String apellidos;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Convert(converter = SexoEnum.PersistenceConverter.class)
    @Column(name = "sexo")
    private SexoEnum sexo;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 50)
    private String numeroDocumento;

    @Convert(converter = TipoDocumentoEnum.PersistenceConverter.class)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumentoEnum tipoDocumento;

    // >>>> OPERATIONS
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

}
