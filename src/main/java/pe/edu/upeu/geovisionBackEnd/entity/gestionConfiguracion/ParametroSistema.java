package pe.edu.upeu.geovisionBackEnd.entity.gestionConfiguracion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PARAMETRO_SISTEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametroSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro_sistema")
    private Long idParametroSistema;

    @Column(name = "clave", nullable = false, unique = true, length = 200)
    private String clave;

    @Column(name = "valor", length = 2000)
    private String valor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_parametro_sistema", nullable = false)
    private TipoParametroSistema tipoParametroSistema;
}
