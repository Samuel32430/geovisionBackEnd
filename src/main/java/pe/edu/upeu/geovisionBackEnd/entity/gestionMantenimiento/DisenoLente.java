package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DISENO_LENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisenoLente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diseno_lente")
    private Long idDisenoLente;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
