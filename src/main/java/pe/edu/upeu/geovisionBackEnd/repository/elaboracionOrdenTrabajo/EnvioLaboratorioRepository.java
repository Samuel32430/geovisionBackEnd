package pe.edu.upeu.geovisionBackEnd.repository.elaboracionOrdenTrabajo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.EnvioLaboratorio;
import java.util.List;

@Repository
public interface EnvioLaboratorioRepository extends JpaRepository<EnvioLaboratorio, Long> {

    List<EnvioLaboratorio> findByOrdenTrabajo_IdOrdenTrabajoOrderByFechaEnvioDesc(Long idOrdenTrabajo);
}
