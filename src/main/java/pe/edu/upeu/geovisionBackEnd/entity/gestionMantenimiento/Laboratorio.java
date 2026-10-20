package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "LABORATORIO")
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_laboratorio")
    private Long idLaboratorio;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "ruc", nullable = false, length = 20, unique = true)
    private String ruc;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "correo", length = 200)
    private String correo;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
