package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.UsuarioRol;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioId(Long idUsuario);

    List<UsuarioRol> findByRolId(Long idRol);

    Optional<UsuarioRol> findByUsuarioIdAndRolId(Long idUsuario, Long idRol);

    boolean existsByUsuarioIdAndRolId(Long idUsuario, Long idRol);

    void deleteByUsuarioId(Long idUsuario);

    @Query("SELECT ur.rol.nombre FROM UsuarioRol ur WHERE ur.usuario.id = :idUsuario")
    List<String> findNombresRolesByUsuarioId(@Param("idUsuario") Long idUsuario);
}
