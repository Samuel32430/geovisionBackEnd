package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ESTADO_COMPROBANTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_comprobante")
    private Long idEstadoComprobante;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
