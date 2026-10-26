package pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.EstadoOrdenTrabajo;

@Repository
public interface EstadoOrdenTrabajoRepository extends JpaRepository<EstadoOrdenTrabajo, Long> {

    Optional<EstadoOrdenTrabajo> findByNombreIgnoreCase(String nombre);
}
