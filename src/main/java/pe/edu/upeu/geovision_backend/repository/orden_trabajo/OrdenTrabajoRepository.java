package pe.edu.upeu.geovision_backend.repository.orden_trabajo;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long>, JpaSpecificationExecutor<OrdenTrabajo> {

    Optional<OrdenTrabajo> findByLenteId(Long idLente);

    boolean existsByLenteId(Long idLente);

    List<OrdenTrabajo> findByEstado(EstadoOrdenTrabajoEnum estado);

    Page<OrdenTrabajo> findByEstado(EstadoOrdenTrabajoEnum estado, Pageable pageable);

    Page<OrdenTrabajo> findByFechaRegistroBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrdenTrabajo o WHERE o.id = :id")
    Optional<OrdenTrabajo> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"lente.receta.paciente.persona", "lente.montura.producto", "lente.materialCristal"})
    @Query("SELECT o FROM OrdenTrabajo o WHERE o.id = :id")
    Optional<OrdenTrabajo> findByIdWithDetails(@Param("id") Long id);
}
