package pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LABORATORIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_laboratorio")
    private Long idLaboratorio;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "estado", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @PrePersist
    public void prePersist() {
        if (this.activo == null) {
            this.activo = true;
        }
    }

}
