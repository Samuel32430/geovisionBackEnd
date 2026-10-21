package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TIPO_OJO")
public class TipoOjo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_ojo")
    private Long idTipoOjo;

    @NotBlank @Size(max = 200)
    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
