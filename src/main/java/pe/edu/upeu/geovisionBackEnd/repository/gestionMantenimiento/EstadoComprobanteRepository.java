package pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.EstadoComprobante;

import java.util.Optional;

@Repository
public interface EstadoComprobanteRepository extends JpaRepository<EstadoComprobante, Long> {

    Optional<EstadoComprobante> findByNombreIgnoreCase(String nombre);
}
