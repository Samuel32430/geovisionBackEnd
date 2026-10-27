package pe.edu.upeu.geovisionBackEnd.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

    List<Venta> findByPacienteIdPersona(Long idPaciente);

    List<Venta> findByEmpleadoIdPersona(Long idEmpleado);

    List<Venta> findByEstado(EstadoVentaEnum estado);

    Optional<Venta> findByNumeroComprobante(String numeroComprobante);

    boolean existsByNumeroComprobante(String numeroComprobante);

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

    @Query("SELECT v FROM Venta v WHERE v.estado = pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum.PENDIENTE")
    List<Venta> findVentasConPagosPendientes();
}
