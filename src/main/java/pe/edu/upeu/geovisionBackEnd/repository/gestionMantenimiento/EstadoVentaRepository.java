package pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.EstadoVenta;

import java.util.Optional;

@Repository
public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {

    Optional<EstadoVenta> findByNombreIgnoreCase(String nombre);
}
