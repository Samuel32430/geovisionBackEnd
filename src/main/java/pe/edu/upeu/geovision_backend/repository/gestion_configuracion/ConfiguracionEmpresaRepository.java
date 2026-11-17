package pe.edu.upeu.geovision_backend.repository.gestion_configuracion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;

import java.util.Optional;

@Repository
public interface ConfiguracionEmpresaRepository extends JpaRepository<ConfiguracionEmpresa, Long> {

    // >>>> QUERIES
    Optional<ConfiguracionEmpresa> findByRuc(String ruc);

    Optional<ConfiguracionEmpresa> findFirstByOrderByIdAsc();

    // >>>> EXISTENCE CHECKS
    boolean existsByRuc(String ruc);

}
