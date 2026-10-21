package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TRATAMIENTO_CRISTAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoCristal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento_cristal")
    private Long idTratamientoCristal;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
