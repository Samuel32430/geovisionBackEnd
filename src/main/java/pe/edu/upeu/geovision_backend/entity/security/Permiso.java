package pe.edu.upeu.geovision_backend.entity.security;

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
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;

@Entity
@Table(name = "PERMISO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permiso {

    // >>>> FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Convert(converter = ModuloEnum.PersistenceConverter.class)
    @Column(name = "modulo")
    private ModuloEnum modulo;

    // >>>> OPERATIONS
    public Integer getModuloCodigo() {
        return this.modulo != null ? this.modulo.getCodigo() : null;
    }

    public void setModuloCodigo(Integer codigo) {
        this.modulo = ModuloEnum.fromCodigo(codigo);
    }

}
