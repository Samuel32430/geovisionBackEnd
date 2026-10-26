package pe.edu.upeu.geovisionBackEnd.repository.elaboracionOrdenTrabajo;

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
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.OrdenTrabajo;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long>,
        JpaSpecificationExecutor<OrdenTrabajo> {

    Optional<OrdenTrabajo> findByNumeroOrden(String numeroOrden);

    boolean existsByNumeroOrden(String numeroOrden);

    boolean existsByNumeroOrdenAndIdOrdenTrabajoNot(String numeroOrden, Long idOrdenTrabajo);

    boolean existsByLente_IdLente(Long idLente);

    Optional<OrdenTrabajo> findByLente_IdLente(Long idLente);

    Page<OrdenTrabajo> findByEstadoOrdenTrabajo_IdEstadoOrdenTrabajo(Long idEstado, Pageable pageable);

    Page<OrdenTrabajo> findByEmpleado_IdPersona(Long idEmpleado, Pageable pageable);

    Page<OrdenTrabajo> findByFechaRegistroBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    /** Carga referencias de solo lectura para construir el detalle sin consultas por cada campo. */
    @EntityGraph(attributePaths = {"lente.receta.examen", "empleado.persona", "estadoOrdenTrabajo"})
    Optional<OrdenTrabajo> findOneByIdOrdenTrabajo(Long idOrdenTrabajo);

    /** Requiere una transaccion activa al modificar estado, notificar o entregar. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrdenTrabajo o where o.idOrdenTrabajo = :id")
    Optional<OrdenTrabajo> findByIdForUpdate(@Param("id") Long id);
}
