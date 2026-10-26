package pe.edu.upeu.geovisionBackEnd.repository.elaboracionOrdenTrabajo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.ControlCalidad;
import java.util.List;

@Repository
public interface ControlCalidadRepository extends JpaRepository<ControlCalidad, Long> {

    List<ControlCalidad> findByEnvioLaboratorio_IdEnvioLaboratorioOrderByFechaRegistroDesc(Long idEnvioLaboratorio);
}
