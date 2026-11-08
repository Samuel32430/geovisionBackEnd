package pe.edu.upeu.geovision_backend.repository.orden_trabajo;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioLaboratorioRepository extends JpaRepository<EnvioLaboratorio, Long>, JpaSpecificationExecutor<EnvioLaboratorio> {

    @Query("SELECT e.ordenTrabajo.id FROM EnvioLaboratorio e WHERE e.id = :id")
    Optional<Long> findIdOrdenByIdEnvio(@Param("id") Long id);

    List<EnvioLaboratorio> findByOrdenTrabajoIdOrderByFechaEnvioDesc(Long idOrdenTrabajo);

    Page<EnvioLaboratorio> findByOrdenTrabajoId(Long idOrdenTrabajo, Pageable pageable);

    Page<EnvioLaboratorio> findByLaboratorioId(Long idLaboratorio, Pageable pageable);

    List<EnvioLaboratorio> findByFechaRecepcionIsNull();

    Page<EnvioLaboratorio> findByFechaRecepcionIsNull(Pageable pageable);

    Optional<EnvioLaboratorio> findFirstByOrdenTrabajoIdOrderByFechaEnvioDescIdDesc(Long idOrdenTrabajo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EnvioLaboratorio e WHERE e.id = :id")
    Optional<EnvioLaboratorio> findByIdForUpdate(@Param("id") Long id);
}
