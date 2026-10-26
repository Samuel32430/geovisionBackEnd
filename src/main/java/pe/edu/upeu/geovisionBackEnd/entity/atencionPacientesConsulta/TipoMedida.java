package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TIPO_MEDIDA")
public class TipoMedida {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_medida")
    private Long idTipoMedida;

    @NotBlank @Size(max = 200)
    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
