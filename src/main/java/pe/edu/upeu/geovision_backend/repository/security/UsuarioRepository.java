package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Usuario e WHERE e.id = :id")
    Optional<Usuario> findByIdForUpdate(@Param("id") Long id);

    Optional<Usuario> findByNombre(String nombre);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByNombreOrCorreo(String nombre, String correo);

    boolean existsByNombre(String nombre);

    boolean existsByCorreo(String correo);

    boolean existsByNombreAndIdNot(String nombre, Long idUsuario);

    boolean existsByCorreoAndIdNot(String correo, Long idUsuario);

    List<Usuario> findByActivo(Boolean estado);

    Optional<Usuario> findByPersonaId(Long idPersona);
}
