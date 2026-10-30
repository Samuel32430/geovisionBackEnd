package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombre(String nombre);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByNombreOrCorreo(String nombre, String correo);

    boolean existsByNombre(String nombre);

    boolean existsByCorreo(String correo);

    boolean existsByNombreAndIdUsuarioNot(String nombre, Long idUsuario);

    boolean existsByCorreoAndIdUsuarioNot(String correo, Long idUsuario);

    List<Usuario> findByEstado(EstadoGeneralEnum estado);

    Optional<Usuario> findByPersonaIdPersona(Long idPersona);
}
