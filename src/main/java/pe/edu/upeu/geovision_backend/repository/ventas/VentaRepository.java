package pe.edu.upeu.geovision_backend.repository.ventas;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

    @Query("SELECT new pe.edu.upeu.geovision_backend.dto.orden_trabajo.PendienteOrdenTrabajoDTO(" +
           "v.idVenta, v.numeroComprobante, l.idLente, v.receta.idReceta, p.idPersona, " +
           "CONCAT(CONCAT(per.nombres, ' '), per.apellidos), v.total) " +
           "FROM Venta v JOIN v.paciente p JOIN p.persona per JOIN Lente l ON l.receta = v.receta " +
           "WHERE v.estado <> pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum.ANULADA " +
           "AND (SELECT COUNT(v2.idVenta) FROM Venta v2 WHERE v2.receta = v.receta) = 1 " +
           "AND EXISTS (SELECT pago.idPago FROM Pago pago WHERE pago.venta = v AND pago.monto > 0) " +
           "AND EXISTS (SELECT lm.idLenteMedidas FROM LenteMedida lm WHERE lm.lente = l) " +
           "AND NOT EXISTS (SELECT o.idOrdenTrabajo FROM OrdenTrabajo o WHERE o.lente = l) " +
           "AND (:termino IS NULL OR LOWER(per.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) ESCAPE '!' " +
           "OR LOWER(per.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) ESCAPE '!' " +
           "OR per.numeroDocumento LIKE CONCAT('%', :termino, '%') ESCAPE '!' " +
           "OR v.numeroComprobante LIKE CONCAT('%', :termino, '%') ESCAPE '!') " +
           "ORDER BY v.fechaRegistro DESC, v.idVenta DESC, l.idLente ASC")
    Page<pe.edu.upeu.geovision_backend.dto.orden_trabajo.PendienteOrdenTrabajoDTO> buscarPendientesOrdenTrabajo(
            @Param("termino") String termino, Pageable pageable);

    List<Venta> findByRecetaIdReceta(Long idReceta);

    List<Venta> findByPacienteIdPersona(Long idPaciente);

    List<Venta> findByEmpleadoIdPersona(Long idEmpleado);

    List<Venta> findByEstado(EstadoVentaEnum estado);

    Optional<Venta> findByNumeroComprobante(String numeroComprobante);

    boolean existsByNumeroComprobante(String numeroComprobante);

    boolean existsByRecetaIdReceta(Long idReceta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Venta v WHERE v.idVenta = :id")
    Optional<Venta> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT v FROM Venta v LEFT JOIN v.paciente p LEFT JOIN p.persona per " +
           "WHERE (:termino IS NULL " +
           "OR LOWER(per.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(per.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR per.numeroDocumento LIKE CONCAT('%', :termino, '%') " +
           "OR v.numeroComprobante LIKE CONCAT('%', :termino, '%')) " +
           "AND (:estado IS NULL OR v.estado = :estado) " +
           "AND (:fechaInicio IS NULL OR v.fechaRegistro >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR v.fechaRegistro <= :fechaFin) " +
           "ORDER BY v.fechaRegistro DESC")
    Page<Venta> buscarHistorialVentas(
            @Param("termino") String termino,
            @Param("estado") EstadoVentaEnum estado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    @Query("SELECT v FROM Venta v WHERE v.estado = pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum.PENDIENTE")
    List<Venta> findVentasConPagosPendientes();
}
