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
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.EnvioLaboratorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioLaboratorioRepository extends JpaRepository<EnvioLaboratorio, Long>,
        JpaSpecificationExecutor<EnvioLaboratorio> {

    List<EnvioLaboratorio> findByOrdenTrabajo_IdOrdenTrabajoOrderByFechaEnvioDesc(Long idOrdenTrabajo);

    Page<EnvioLaboratorio> findByOrdenTrabajo_IdOrdenTrabajo(Long idOrdenTrabajo, Pageable pageable);

    Page<EnvioLaboratorio> findByLaboratorio_IdLaboratorio(Long idLaboratorio, Pageable pageable);

    Page<EnvioLaboratorio> findByFechaRecepcionIsNull(Pageable pageable);

    Page<EnvioLaboratorio> findByFechaEnvioBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    /** El ID desempata envios registrados en el mismo segundo en Oracle DATE. */
    Optional<EnvioLaboratorio> findFirstByOrdenTrabajo_IdOrdenTrabajoOrderByFechaEnvioDescIdEnvioLaboratorioDesc(
            Long idOrdenTrabajo);

    /** Requiere una transaccion activa para serializar el registro de recepcion. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EnvioLaboratorio e where e.idEnvioLaboratorio = :id")
    Optional<EnvioLaboratorio> findByIdForUpdate(@Param("id") Long id);
}
