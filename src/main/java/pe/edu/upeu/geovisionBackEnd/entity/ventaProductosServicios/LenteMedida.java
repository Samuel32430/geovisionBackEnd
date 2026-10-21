package pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Examen;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Medida;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Receta;

@Entity
@Table(name = "LENTE_MEDIDA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LenteMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lente_medida")
    private Long idLenteMedida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lente", nullable = false)
    private Lente lente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_examen", nullable = false)
    private Examen examen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medida", nullable = false)
    private Medida medida;
}
