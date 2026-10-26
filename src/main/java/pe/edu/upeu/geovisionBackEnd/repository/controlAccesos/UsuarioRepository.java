package pe.edu.upeu.geovisionBackEnd.repository.controlAccesos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombre(String nombre);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByNombreOrCorreo(String nombre, String correo);

    boolean existsByNombre(String nombre);

    boolean existsByCorreo(String correo);

    List<Usuario> findByEstado(Integer estado);
}
