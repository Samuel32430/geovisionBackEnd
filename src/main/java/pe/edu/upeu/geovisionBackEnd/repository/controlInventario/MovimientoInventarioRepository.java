package pe.edu.upeu.geovisionBackEnd.repository.controlInventario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.MovimientoInventario;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.MotivoMovimiento;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /**
     * Consulta el historial cronológico inmutable de movimientos de un producto (RF-83).
     */
    List<MovimientoInventario> findByIdItemOrderByFechaRegistroDesc(Long idItem);

    /**
     * Consulta paginada del historial de movimientos de un ítem.
     */
    Page<MovimientoInventario> findByIdItemOrderByFechaRegistroDesc(Long idItem, Pageable pageable);

    /**
     * Filtra movimientos por motivo específico.
     */
    List<MovimientoInventario> findByMotivoOrderByFechaRegistroDesc(MotivoMovimiento motivo);

    /**
     * Consulta movimientos registrados por un empleado específico.
     */
    List<MovimientoInventario> findByIdEmpleadoOrderByFechaRegistroDesc(Long idEmpleado);
}
