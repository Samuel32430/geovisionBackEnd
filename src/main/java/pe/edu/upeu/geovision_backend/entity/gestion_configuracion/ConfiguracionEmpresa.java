package pe.edu.upeu.geovision_backend.entity.gestion_configuracion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long id;

    @Column(name = "nombre_legal", nullable = false, length = 200)
    private String nombreLegal;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "ruc", nullable = false, unique = true, length = 20)
    private String ruc;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "logo", length = 200)
    private String logo;
}
