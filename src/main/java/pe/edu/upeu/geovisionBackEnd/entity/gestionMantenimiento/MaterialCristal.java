package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MATERIAL_CRISTAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCristal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material_cristal")
    private Long idMaterialCristal;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
