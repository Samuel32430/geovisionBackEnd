package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "SERVICIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long idServicio;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 14, scale = 2)
    private BigDecimal precio;

    @Column(name = "estado", nullable = false)
    private Integer estado = 1;
}
