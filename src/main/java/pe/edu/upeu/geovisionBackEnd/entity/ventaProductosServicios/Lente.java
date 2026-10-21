package pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Receta;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Cristal;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Montura;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.DisenoLente;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lente")
    private Long idLente;

    @Column(name = "descripcion_montura_propia", length = 1000)
    private String descripcionMonturaPropia;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "sub_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_diseno_lente", nullable = false)
    private DisenoLente disenoLente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_montura")
    private Montura montura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cristal", nullable = false)
    private Cristal cristal;

    @OneToMany(mappedBy = "lente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LenteMedida> medidas = new ArrayList<>();
}
