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
import pe.edu.upeu.geovisionBackEnd.enums.TipoLogEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_log")
    private Long idUsuarioLog;

    @Column(name = "fecha_log", nullable = false)
    private LocalDateTime fechaLog;

    @Convert(converter = TipoLogConverter.class)
    @Column(name = "tipo_log")
    private TipoLogEnum tipoLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        if (this.fechaLog == null) {
            this.fechaLog = LocalDateTime.now();
        }
    }

    public Integer getTipoLogCodigo() {
        return this.tipoLog != null ? this.tipoLog.getCodigo() : null;
    }

    public void setTipoLogCodigo(Integer codigo) {
        this.tipoLog = TipoLogEnum.fromCodigo(codigo);
    }

    @Converter
    public static class TipoLogConverter implements AttributeConverter<TipoLogEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TipoLogEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public TipoLogEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? TipoLogEnum.fromCodigo(dbData) : null;
        }
    }
}
