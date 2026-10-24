package pe.edu.upeu.geovisionBackEnd.entity.controlAccesos;

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
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.Cargo;

import java.time.LocalDate;

@Entity
@Table(name = "EMPLEADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {

    @Id
    @Column(name = "id_persona")
    private Long idPersona;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @Column(name = "estado", nullable = false)
    private Integer estado = 1;

    @Column(name = "fecha_contrato", nullable = false)
    private LocalDate fechaContrato = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cargo", nullable = false)
    private Cargo cargo;

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = 1;
        }
        if (fechaContrato == null) {
            fechaContrato = LocalDate.now();
        }
    }
}
