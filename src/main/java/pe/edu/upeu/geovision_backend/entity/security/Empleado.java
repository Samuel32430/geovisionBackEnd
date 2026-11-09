package pe.edu.upeu.geovision_backend.entity.security;

import pe.edu.upeu.geovision_backend.entity.atencion.Persona;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "EMPLEADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {

    // >>>> FIELDS
    @Id
    @Column(name = "id_persona")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "estado", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_contrato", nullable = false)
    private LocalDate fechaContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // >>>> LIFECYCLE
    @PrePersist
    public void prePersist() {
        if (this.fechaContrato == null) {
            this.fechaContrato = LocalDate.now();
        }
        if (this.activo == null) {
            this.activo = true;
        }
    }

}
