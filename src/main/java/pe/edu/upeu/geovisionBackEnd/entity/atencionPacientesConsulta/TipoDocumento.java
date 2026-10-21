package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TIPO_DOCUMENTO")
public class TipoDocumento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_documento")
    private Long idTipoDocumento;

    @NotBlank @Size(max = 200)
    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
