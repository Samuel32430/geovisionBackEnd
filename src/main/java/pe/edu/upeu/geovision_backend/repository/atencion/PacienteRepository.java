package pe.edu.upeu.geovision_backend.repository.atencion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long>, JpaSpecificationExecutor<Paciente> {

    Optional<Paciente> findByIdPersonaAndEstado(Long idPersona, EstadoGeneralEnum estado);

    Optional<Paciente> findByUsuarioIdUsuario(Long idUsuario);

    List<Paciente> findByEstado(EstadoGeneralEnum estado);

    @EntityGraph(attributePaths = "persona")
    Optional<Paciente> findByPersonaNumeroDocumento(String numeroDocumento);

    @EntityGraph(attributePaths = {"persona", "usuario"})
    @Query("SELECT p FROM Paciente p WHERE p.idPersona = :id")
    Optional<Paciente> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = "persona")
    @Query("SELECT p FROM Paciente p JOIN p.persona per WHERE (:estado IS NULL OR p.estado = :estado) " +
           "AND (:termino IS NULL OR LOWER(CONCAT(CONCAT(per.nombres, ' '), per.apellidos)) LIKE LOWER(CONCAT('%', :termino, '%')) ESCAPE '!' " +
           "OR per.numeroDocumento LIKE CONCAT('%', :termino, '%') ESCAPE '!' " +
           "OR per.telefono LIKE CONCAT('%', :termino, '%') ESCAPE '!')")
    Page<Paciente> buscarPacientes(@Param("termino") String termino, @Param("estado") EstadoGeneralEnum estado, Pageable pageable);
}
