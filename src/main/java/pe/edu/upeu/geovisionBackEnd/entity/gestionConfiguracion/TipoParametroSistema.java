package pe.edu.upeu.geovisionBackEnd.entity.gestionConfiguracion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TIPO_PARAMETRO_SISTEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoParametroSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_parametro_sistema")
    private Long idTipoParametroSistema;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;
}
