package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Entity
@Table(name = "MEDIDA", uniqueConstraints = {
    @UniqueConstraint(name = "MEDIDA_tipo_uq", columnNames = {"id_examen", "id_tipo_medida"}),
    @UniqueConstraint(name = "MEDIDA_examen_uq", columnNames = {"id_medida", "id_examen"})
})
public class Medida {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida")
    private Long idMedida;

    @Size(max = 1000) @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_examen", nullable = false)
    private Examen examen;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_medida", nullable = false)
    private TipoMedida tipoMedida;
}
