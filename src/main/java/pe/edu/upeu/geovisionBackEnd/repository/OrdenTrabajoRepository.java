package pe.edu.upeu.geovisionBackEnd.repository;

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
import pe.edu.upeu.geovisionBackEnd.entity.OrdenTrabajo;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoOrdenTrabajoEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long>, JpaSpecificationExecutor<OrdenTrabajo> {

    Optional<OrdenTrabajo> findByLenteIdLente(Long idLente);

    boolean existsByLenteIdLente(Long idLente);

    List<OrdenTrabajo> findByEstado(EstadoOrdenTrabajoEnum estado);

    Page<OrdenTrabajo> findByEstado(EstadoOrdenTrabajoEnum estado, Pageable pageable);

    Page<OrdenTrabajo> findByFechaRegistroBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrdenTrabajo o WHERE o.idOrdenTrabajo = :id")
    Optional<OrdenTrabajo> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"lente.receta.paciente.persona", "lente.montura.producto", "lente.materialCristal"})
    @Query("SELECT o FROM OrdenTrabajo o WHERE o.idOrdenTrabajo = :id")
    Optional<OrdenTrabajo> findByIdWithDetails(@Param("id") Long id);
}
