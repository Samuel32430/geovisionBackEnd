package pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TIPO_PARAMETRO_CALIDAD")
public class TipoParametroCalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_parametro_calidad")
    private Long idTipoParametroCalidad;

    @Column(name = "nombre", nullable = false, length = 200, unique = true)
    private String nombre;

    @Column(name = "unidad", nullable = false, length = 50)
    private String unidad;

    @Column(name = "tolerancia", precision = 10, scale = 4)
    private BigDecimal tolerancia;

    @Column(name = "periodo", precision = 10, scale = 4)
    private BigDecimal periodo;

    @Column(name = "valor_esperado", nullable = false, precision = 10, scale = 4)
    private BigDecimal valorEsperado;
}
