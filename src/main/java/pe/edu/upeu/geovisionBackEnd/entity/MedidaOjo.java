package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "MEDIDA_OJO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedidaOjo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida_ojo")
    private Long idMedidaOjo;

    @Column(name = "agudeza_visual", length = 50)
    private String agudezaVisual;

    @Column(name = "esfera", precision = 5, scale = 2)
    private BigDecimal esfera;

    @Column(name = "cilindro", precision = 5, scale = 2)
    private BigDecimal cilindro;

    @Column(name = "eje")
    private Integer eje;

    @Column(name = "adicion", precision = 4, scale = 2)
    private BigDecimal adicion;
}
