package pe.edu.upeu.geovisionBackEnd.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.EnvioLaboratorio;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioLaboratorioRepository extends JpaRepository<EnvioLaboratorio, Long>, JpaSpecificationExecutor<EnvioLaboratorio> {

    List<EnvioLaboratorio> findByOrdenTrabajoIdOrdenTrabajoOrderByFechaEnvioDesc(Long idOrdenTrabajo);

    Page<EnvioLaboratorio> findByOrdenTrabajoIdOrdenTrabajo(Long idOrdenTrabajo, Pageable pageable);

    Page<EnvioLaboratorio> findByLaboratorioIdLaboratorio(Long idLaboratorio, Pageable pageable);

    List<EnvioLaboratorio> findByFechaRecepcionIsNull();

    Page<EnvioLaboratorio> findByFechaRecepcionIsNull(Pageable pageable);

    Optional<EnvioLaboratorio> findFirstByOrdenTrabajoIdOrdenTrabajoOrderByFechaEnvioDescIdEnvioLaboratorioDesc(Long idOrdenTrabajo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EnvioLaboratorio e WHERE e.idEnvioLaboratorio = :id")
    Optional<EnvioLaboratorio> findByIdForUpdate(@Param("id") Long id);
}
