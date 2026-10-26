package pe.edu.upeu.geovisionBackEnd.entity.controlAccesos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUDITORIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @Column(name = "fecha_log", nullable = false)
    @Builder.Default
    private LocalDateTime fechaLog = LocalDateTime.now();

    @Column(name = "entidad", nullable = false, length = 100)
    private String entidad;

    @Column(name = "id_entidad", nullable = false)
    private Long idEntidad;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @Lob
    @Column(name = "datos_anteriores")
    private String datosAnteriores;

    @Lob
    @Column(name = "datos_nuevos")
    private String datosNuevos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        if (this.fechaLog == null) {
            this.fechaLog = LocalDateTime.now();
        }
    }
}
