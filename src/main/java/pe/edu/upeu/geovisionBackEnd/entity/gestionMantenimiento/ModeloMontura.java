package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MODELO_MONTURA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModeloMontura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modelo_montura")
    private Long idModeloMontura;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
