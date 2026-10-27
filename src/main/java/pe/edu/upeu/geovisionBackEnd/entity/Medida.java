package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "MEDIDA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida")
    private Long idMedida;

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
}
