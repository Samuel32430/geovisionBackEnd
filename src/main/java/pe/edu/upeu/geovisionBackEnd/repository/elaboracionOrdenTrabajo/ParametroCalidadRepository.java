package pe.edu.upeu.geovisionBackEnd.repository.elaboracionOrdenTrabajo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.ParametroCalidad;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParametroCalidadRepository extends JpaRepository<ParametroCalidad, Long> {

    List<ParametroCalidad> findByControlCalidad_IdControlCalidad(Long idControlCalidad);

    Page<ParametroCalidad> findByControlCalidad_IdControlCalidad(Long idControlCalidad, Pageable pageable);

    boolean existsByControlCalidad_IdControlCalidadAndTipoParametroCalidad_IdTipoParametroCalidadAndMedidaOjo_IdMedidaOjo(
            Long idControlCalidad, Long idTipoParametroCalidad, Long idMedidaOjo);

    Optional<ParametroCalidad> findByControlCalidad_IdControlCalidadAndTipoParametroCalidad_IdTipoParametroCalidadAndMedidaOjo_IdMedidaOjo(
            Long idControlCalidad, Long idTipoParametroCalidad, Long idMedidaOjo);

    long countByControlCalidad_IdControlCalidad(Long idControlCalidad);

    /** La ausencia de fallos no implica aprobacion si el control aun no tiene resultados. */
    boolean existsByControlCalidad_IdControlCalidadAndAprobadoFalse(Long idControlCalidad);
}
