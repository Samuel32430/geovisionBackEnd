package pe.edu.upeu.geovisionBackEnd.entity.gestionConfiguracion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CONFIGURACION_EMPRESA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion_empresa")
    private Long idConfiguracionEmpresa;

    @Column(name = "nombre_legal", nullable = false, length = 200)
    private String nombreLegal;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "ruc", nullable = false, unique = true, length = 20)
    private String ruc;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "correo", length = 200)
    private String correo;

    @Column(name = "logo", length = 1000)
    private String logo;
}
