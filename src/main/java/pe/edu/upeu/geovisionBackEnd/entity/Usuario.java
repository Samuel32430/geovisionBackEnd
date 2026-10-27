package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

@Entity
@Table(name = "USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @Convert(converter = EstadoGeneralConverter.class)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoGeneralEnum estado = EstadoGeneralEnum.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoGeneralEnum.ACTIVO;
        }
    }

    public Integer getEstadoCodigo() {
        return this.estado != null ? this.estado.getCodigo() : null;
    }

    public void setEstadoCodigo(Integer codigo) {
        this.estado = EstadoGeneralEnum.fromCodigo(codigo);
    }

    @Converter
    public static class EstadoGeneralConverter implements AttributeConverter<EstadoGeneralEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(EstadoGeneralEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public EstadoGeneralEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? EstadoGeneralEnum.fromCodigo(dbData) : null;
        }
    }
}
