package pe.edu.upeu.geovisionBackEnd.repository.elaboracionOrdenTrabajo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.ParametroCalidad;
import java.util.List;

@Repository
public interface ParametroCalidadRepository extends JpaRepository<ParametroCalidad, Long> {

    List<ParametroCalidad> findByControlCalidad_IdControlCalidad(Long idControlCalidad);

    boolean existsByControlCalidad_IdControlCalidadAndTipoParametroCalidad_IdTipoParametroCalidadAndMedidaOjo_IdMedidaOjo(
            Long idControlCalidad, Long idTipoParametroCalidad, Long idMedidaOjo);
}
