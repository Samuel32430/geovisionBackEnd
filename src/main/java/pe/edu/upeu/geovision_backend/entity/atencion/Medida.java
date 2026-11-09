package pe.edu.upeu.geovision_backend.entity.atencion;

import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;

import java.math.BigDecimal;

@Entity
@Table(name = "MEDIDA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medida {

    // >>>> FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida")
    private Long id;

    @Convert(converter = TipoMedidaEnum.PersistenceConverter.class)
    @Column(name = "tipo_medida", nullable = false, precision = 1)
    private TipoMedidaEnum tipoMedida = TipoMedidaEnum.LEJOS;

    @Column(name = "distancia_pupilar", precision = 5, scale = 2)
    private BigDecimal distanciaPupilar;

    @Column(name = "prisma", precision = 5, scale = 2)
    private BigDecimal prisma;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta")
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medida_ojo_derecha")
    private MedidaOjo medidaOjoDerecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medida_ojo_izquierda")
    private MedidaOjo medidaOjoIzquierda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envio_laboratorio")
    private EnvioLaboratorio envioLaboratorio;

    // >>>> LIFECYCLE
    @PrePersist
    public void prePersist() {
        if (tipoMedida == null) {
            tipoMedida = TipoMedidaEnum.LEJOS;
        }
    }

}
