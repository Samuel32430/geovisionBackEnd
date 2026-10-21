package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "DOCUMENTO", uniqueConstraints = @UniqueConstraint(
        name = "DOCUMENTO_numero_uq", columnNames = {"id_tipo_documento", "numero"}
))
public class Documento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long idDocumento;

    @NotBlank @Size(max = 50)
    @Column(name = "numero", nullable = false, length = 50)
    private String numero;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;
}
