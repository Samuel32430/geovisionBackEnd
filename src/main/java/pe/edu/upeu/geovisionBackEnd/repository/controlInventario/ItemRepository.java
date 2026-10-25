package pe.edu.upeu.geovisionBackEnd.repository.controlInventario;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Item;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    /**
     * Valida la existencia de un código para prevenir duplicidad (RF-69).
     */
    boolean existsByCodigo(String codigo);

    /**
     * Valida duplicidad de código excluyendo el propio ítem al actualizar.
     */
    boolean existsByCodigoAndIdItemNot(String codigo, Long idItem);

    /**
     * Busca un ítem por su código único de catálogo.
     */
    Optional<Item> findByCodigo(String codigo);

    /**
     * Obtiene el ítem aplicando un bloqueo pesimista exclusivo para operaciones concurrentes
     * seguras sobre el stock físico (RF-78, RF-81).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.idItem = :idItem")
    Optional<Item> findByIdForUpdate(@Param("idItem") Long idItem);

    /**
     * Consulta productos habilitados para la venta que cuenten con stock disponible (RF-80).
     * Si no controla stock (ej. CRISTAL), se considera disponible siempre que esté activo.
     */
    @Query("SELECT i FROM Item i WHERE i.estado = true AND (i.controlaStock = false OR i.stock > 0)")
    List<Item> findActivosDisponiblesParaVenta();

    /**
     * Consulta productos con existencias iguales o inferiores al umbral indicado (RF-82).
     */
    @Query("SELECT i FROM Item i WHERE i.controlaStock = true AND i.stock <= :umbralStock ORDER BY i.stock ASC")
    List<Item> findProductosConStockBajoOAgotados(@Param("umbralStock") Integer umbralStock);

    /**
     * Filtra ítems por tipo.
     */
    List<Item> findByTipo(TipoItem tipo);

    /**
     * Filtra ítems por categoría.
     */
    List<Item> findByIdCategoria(Long idCategoria);

    /**
     * Filtra ítems por estado lógico (activo / inactivo).
     */
    List<Item> findByEstado(Boolean estado);
}
