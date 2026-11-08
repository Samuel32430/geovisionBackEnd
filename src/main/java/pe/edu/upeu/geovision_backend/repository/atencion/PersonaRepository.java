package pe.edu.upeu.geovision_backend.repository.atencion;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.dto.security.PersonaUsuarioResumenDTO;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @Query(value = """
        SELECT new pe.edu.upeu.geovision_backend.dto.security.PersonaUsuarioResumenDTO(
            p.idPersona, p.nombres, p.apellidos, p.tipoDocumento, p.numeroDocumento,
            u.idUsuario, u.nombre, u.activo,
            CASE WHEN e.idPersona IS NOT NULL THEN true ELSE false END,
            CASE WHEN e.activo = true THEN true ELSE false END)
        FROM Persona p
        LEFT JOIN Usuario u ON u.persona.idPersona = p.idPersona
        JOIN Empleado e ON e.idPersona = p.idPersona
        WHERE e.activo = true
          AND (:sinUsuario = false OR u.idUsuario IS NULL)
          AND (LOWER(CONCAT(CONCAT(p.nombres, ' '), p.apellidos)) LIKE :patron ESCAPE '!'
               OR LOWER(p.numeroDocumento) LIKE :patron ESCAPE '!')
        """, countQuery = """
        SELECT COUNT(p) FROM Persona p
        LEFT JOIN Usuario u ON u.persona.idPersona = p.idPersona
        JOIN Empleado e ON e.idPersona = p.idPersona
        WHERE e.activo = true
          AND (:sinUsuario = false OR u.idUsuario IS NULL)
          AND (LOWER(CONCAT(CONCAT(p.nombres, ' '), p.apellidos)) LIKE :patron ESCAPE '!'
               OR LOWER(p.numeroDocumento) LIKE :patron ESCAPE '!')
        """)
    Page<PersonaUsuarioResumenDTO> buscarParaUsuario(@Param("patron") String patron,
            @Param("sinUsuario") boolean sinUsuario, Pageable pageable);

    Optional<Persona> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdPersonaNot(String numeroDocumento, Long idPersona);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Persona p WHERE p.idPersona = :id")
    Optional<Persona> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT p FROM Persona p WHERE LOWER(p.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR p.numeroDocumento LIKE CONCAT('%', :termino, '%')")
    Page<Persona> buscarPorNombreODocumento(@Param("termino") String termino, Pageable pageable);
}
