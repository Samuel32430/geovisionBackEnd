package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MEDIDA_OJO", uniqueConstraints =
    @UniqueConstraint(name = "MEDIDA_OJO_par_uq", columnNames = {"id_medida", "id_tipo_ojo"})
)
@Check(constraints = "eje between 0 and 180 and distancia_pupilar_mm > 0 and prisma >= 0")
public class MedidaOjo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida_ojo")
    private Long idMedidaOjo;

    /** Decimal scale; no clinical range has been specified. */
    @Column(name = "agudeza_visual", columnDefinition = "NUMBER")
    private BigDecimal agudezaVisual;

    @Digits(integer = 5, fraction = 2) @Column(name = "esfera", precision = 7, scale = 2)
    private BigDecimal esfera;

    @Digits(integer = 5, fraction = 2) @Column(name = "cilindro", precision = 7, scale = 2)
    private BigDecimal cilindro;

    @DecimalMin("0") @DecimalMax("180") @Digits(integer = 4, fraction = 2)
    @Column(name = "eje", precision = 6, scale = 2)
    private BigDecimal eje;

    @Digits(integer = 5, fraction = 2) @Column(name = "adicion", precision = 7, scale = 2)
    private BigDecimal adicion;

    @DecimalMin(value = "0", inclusive = false) @Digits(integer = 4, fraction = 2)
    @Column(name = "distancia_pupilar_mm", precision = 6, scale = 2)
    private BigDecimal distanciaPupilarMm;

    @DecimalMin("0") @Digits(integer = 5, fraction = 2)
    @Column(name = "prisma", precision = 7, scale = 2)
    private BigDecimal prisma;

    @Size(max = 100) @Column(name = "base_prisma", length = 100)
    private String basePrisma;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medida", nullable = false)
    private Medida medida;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_ojo", nullable = false)
    private TipoOjo tipoOjo;
}
