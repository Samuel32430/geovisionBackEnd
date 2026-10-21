package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MATERIAL_MONTURA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialMontura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material_montura")
    private Long idMaterialMontura;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
