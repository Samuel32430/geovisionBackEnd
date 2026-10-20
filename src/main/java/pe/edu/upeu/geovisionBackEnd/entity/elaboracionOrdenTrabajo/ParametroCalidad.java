package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PARAMETRO_CALIDAD", uniqueConstraints = @UniqueConstraint(
        name = "PARAM_CALIDAD_par_uq",
        columnNames = {"id_control_calidad", "id_tipo_parametro_calidad", "id_medida_ojo"}
))
public class ParametroCalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro_calidad")
    private Long idParametroCalidad;

    @Column(name = "tolerancia", nullable = false, precision = 10, scale = 4)
    private BigDecimal tolerancia;

    @Column(name = "valor_esperado", nullable = false, precision = 10, scale = 4)
    private BigDecimal valorEsperado;

    @Column(name = "valor_obtenido", nullable = false, precision = 10, scale = 4)
    private BigDecimal valorObtenido;

    @Column(name = "periodo", precision = 10, scale = 4)
    private BigDecimal periodo;

    @Column(name = "aprobado", nullable = false)
    private Boolean aprobado;

    @Column(name = "id_control_calidad", nullable = false)
    private Long idControlCalidad;

    @Column(name = "id_tipo_parametro_calidad", nullable = false)
    private Long idTipoParametroCalidad;

    @Column(name = "id_medida_ojo", nullable = false)
    private Long idMedidaOjo;
}
