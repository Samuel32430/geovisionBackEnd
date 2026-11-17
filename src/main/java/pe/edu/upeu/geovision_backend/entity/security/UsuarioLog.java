package pe.edu.upeu.geovision_backend.entity.security;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovision_backend.enums.TipoLogEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLog {

    // >>>> FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_log")
    private Long id;

    @Column(name = "fecha_log", nullable = false)
    private LocalDateTime fechaLog;

    @Convert(converter = TipoLogEnum.PersistenceConverter.class)
    @Column(name = "tipo_log")
    private TipoLogEnum tipoLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // >>>> LIFECYCLE
    @PrePersist
    public void prePersist() {
        if (this.fechaLog == null) {
            this.fechaLog = LocalDateTime.now();
        }
    }

    // >>>> OPERATIONS
    public Integer getTipoLogCodigo() {
        return this.tipoLog != null ? this.tipoLog.getCodigo() : null;
    }

    public void setTipoLogCodigo(Integer codigo) {
        this.tipoLog = TipoLogEnum.fromCodigo(codigo);
    }

}
