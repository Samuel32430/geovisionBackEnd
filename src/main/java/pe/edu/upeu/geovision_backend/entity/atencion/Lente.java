package pe.edu.upeu.geovision_backend.entity.atencion;

import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OrderBy;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.LinkedHashSet;

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
    private Long id;

    @Column(name = "sub_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material_cristal", nullable = false)
    private MaterialCristal materialCristal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Montura montura;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "LENTE_MEDIDA",
            joinColumns = @JoinColumn(name = "id_lente", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "id_medida", nullable = false),
            uniqueConstraints = @UniqueConstraint(name = "LENTE_MEDIDAS_uq", columnNames = {"id_lente", "id_medida"}))
    @OrderBy("id ASC")
    private Set<Medida> medidas = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "LENTE_TRATAMIENTO_CRISTAL",
            joinColumns = @JoinColumn(name = "id_lente", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "id_tratamiento_cristal", nullable = false),
            uniqueConstraints = @UniqueConstraint(name = "LENTE_TRAT_uq", columnNames = {"id_lente", "id_tratamiento_cristal"}))
    @OrderBy("id ASC")
    private Set<TratamientoCristal> tratamientosCristal = new LinkedHashSet<>();
}
