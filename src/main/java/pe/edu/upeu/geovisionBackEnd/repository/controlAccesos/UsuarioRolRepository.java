package pe.edu.upeu.geovisionBackEnd.repository.controlAccesos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.UsuarioRol;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioIdUsuario(Long idUsuario);

    List<UsuarioRol> findByRolIdRol(Long idRol);

    Optional<UsuarioRol> findByUsuarioIdUsuarioAndRolIdRol(Long idUsuario, Long idRol);

    boolean existsByUsuarioIdUsuarioAndRolIdRol(Long idUsuario, Long idRol);

    void deleteByUsuarioIdUsuario(Long idUsuario);

    @Query("SELECT ur.rol.nombre FROM UsuarioRol ur WHERE ur.usuario.idUsuario = :idUsuario")
    List<String> findNombresRolesByUsuarioId(@Param("idUsuario") Long idUsuario);
}
