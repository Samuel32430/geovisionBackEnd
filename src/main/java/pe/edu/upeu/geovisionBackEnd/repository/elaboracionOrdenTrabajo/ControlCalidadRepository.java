package pe.edu.upeu.geovisionBackEnd.repository.elaboracionOrdenTrabajo;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.ControlCalidad;
import pe.edu.upeu.geovisionBackEnd.enums.elaboracionOrdenTrabajo.EstadoControlCalidad;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControlCalidadRepository extends JpaRepository<ControlCalidad, Long>,
        JpaSpecificationExecutor<ControlCalidad> {

    List<ControlCalidad> findByEnvioLaboratorio_IdEnvioLaboratorioOrderByFechaRegistroDesc(Long idEnvioLaboratorio);

    Page<ControlCalidad> findByEnvioLaboratorio_IdEnvioLaboratorio(Long idEnvioLaboratorio, Pageable pageable);

    Page<ControlCalidad> findByEstado(EstadoControlCalidad estado, Pageable pageable);

    Page<ControlCalidad> findByFechaRegistroBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    /**
     * Se consulta para el envio vigente. Una aprobacion de un envio anterior
     * no equivale a la aprobacion del ultimo trabajo recibido.
     */
    Optional<ControlCalidad> findFirstByEnvioLaboratorio_IdEnvioLaboratorioOrderByFechaRegistroDescIdControlCalidadDesc(
            Long idEnvioLaboratorio);

    /** Requiere una transaccion activa al registrar resultados o cerrar el control. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ControlCalidad c where c.idControlCalidad = :id")
    Optional<ControlCalidad> findByIdForUpdate(@Param("id") Long id);
}
