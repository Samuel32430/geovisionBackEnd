package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.OrdenTrabajo;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Venta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

    List<Venta> findByPaciente_IdPersona(Long idPaciente);

    @Query(value = "SELECT DISTINCT v FROM Venta v " +
                   "LEFT JOIN FETCH v.comprobante c " +
                   "LEFT JOIN FETCH v.estadoVenta ev " +
                   "LEFT JOIN v.paciente p " +
                   "LEFT JOIN p.persona per " +
                   "LEFT JOIN v.comprador comp " +
                   "WHERE (:termino IS NULL OR " +
                   "      LOWER(per.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                   "      LOWER(per.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                   "      LOWER(comp.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                   "      LOWER(comp.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                   "      LOWER(CONCAT(c.serie, '-', c.numero)) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                   "      CAST(v.idVenta AS string) LIKE CONCAT('%', :termino, '%') OR " +
                   "      EXISTS (SELECT 1 FROM Documento doc WHERE (doc.persona = per OR doc.persona = comp) AND doc.numero LIKE CONCAT('%', :termino, '%'))) " +
                   "AND (:idEstado IS NULL OR v.estadoVenta.idEstadoVenta = :idEstado) " +
                   "AND (:fechaInicio IS NULL OR v.fechaRegistro >= :fechaInicio) " +
                   "AND (:fechaFin IS NULL OR v.fechaRegistro <= :fechaFin) " +
                   "ORDER BY v.fechaRegistro DESC",
           countQuery = "SELECT COUNT(DISTINCT v) FROM Venta v " +
                        "LEFT JOIN v.comprobante c " +
                        "LEFT JOIN v.paciente p " +
                        "LEFT JOIN p.persona per " +
                        "LEFT JOIN v.comprador comp " +
                        "WHERE (:termino IS NULL OR " +
                        "      LOWER(per.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                        "      LOWER(per.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                        "      LOWER(comp.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                        "      LOWER(comp.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                        "      LOWER(CONCAT(c.serie, '-', c.numero)) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
                        "      CAST(v.idVenta AS string) LIKE CONCAT('%', :termino, '%') OR " +
                        "      EXISTS (SELECT 1 FROM Documento doc WHERE (doc.persona = per OR doc.persona = comp) AND doc.numero LIKE CONCAT('%', :termino, '%'))) " +
                        "AND (:idEstado IS NULL OR v.estadoVenta.idEstadoVenta = :idEstado) " +
                        "AND (:fechaInicio IS NULL OR v.fechaRegistro >= :fechaInicio) " +
                        "AND (:fechaFin IS NULL OR v.fechaRegistro <= :fechaFin)")
    Page<Venta> buscarHistorialPaginado(
            @Param("termino") String termino,
            @Param("idEstado") Long idEstado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    @Query("SELECT d.numero FROM Documento d WHERE d.persona.idPersona = :idPersona ORDER BY d.idDocumento ASC")
    List<String> findDocumentosByPersonaId(@Param("idPersona") Long idPersona);

    @Query("SELECT ot FROM OrdenTrabajo ot WHERE ot.idLente = :idLente")
    Optional<OrdenTrabajo> findOrdenTrabajoByIdLente(@Param("idLente") Long idLente);
}
