package pe.edu.upeu.geovisionBackEnd.repository.controlInventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.ReservaInventario;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.EstadoReserva;

import java.util.List;

@Repository
public interface ReservaInventarioRepository extends JpaRepository<ReservaInventario, Long> {

    /**
     * Consulta reservas según ítem y su estado de ciclo de vida.
     */
    List<ReservaInventario> findByIdItemAndEstado(Long idItem, EstadoReserva estado);

    /**
     * Calcula la suma de unidades actualmente bloqueadas en reservas activas para un producto.
     */
    @Query("SELECT COALESCE(SUM(r.cantidad), 0) FROM ReservaInventario r WHERE r.idItem = :idItem AND r.estado = :estado")
    Integer sumCantidadByItemAndEstado(@Param("idItem") Long idItem, @Param("estado") EstadoReserva estado);

    /**
     * Consulta reservas asociadas a un ítem vendido específico.
     */
    List<ReservaInventario> findByIdItemVendido(Long idItemVendido);
}
