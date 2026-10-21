package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TIPO_COMPROBANTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class    TipoComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_comprobante")
    private Long idTipoComprobante;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
