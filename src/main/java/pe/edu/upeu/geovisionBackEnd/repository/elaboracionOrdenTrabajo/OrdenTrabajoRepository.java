package pe.edu.upeu.geovisionBackEnd.repository.elaboracionOrdenTrabajo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.OrdenTrabajo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    Optional<OrdenTrabajo> findByNumeroOrden(String numeroOrden);

    boolean existsByNumeroOrden(String numeroOrden);

    boolean existsByLente_IdLente(Long idLente);

    Optional<OrdenTrabajo> findByLente_IdLente(Long idLente);

    Page<OrdenTrabajo> findByEstadoOrdenTrabajo_IdEstadoOrdenTrabajo(Long idEstado, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrdenTrabajo o where o.idOrdenTrabajo = :id")
    Optional<OrdenTrabajo> findByIdForUpdate(@Param("id") Long id);
}
