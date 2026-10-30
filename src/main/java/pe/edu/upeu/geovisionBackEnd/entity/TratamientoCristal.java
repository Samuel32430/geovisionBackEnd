package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TRATAMIENTO_CRISTAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TratamientoCristal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento_cristal")
    private Long idTratamientoCristal;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;
}
