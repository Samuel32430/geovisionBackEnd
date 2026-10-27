package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ConfiguracionEmpresa;

import java.util.Optional;

@Repository
public interface ConfiguracionEmpresaRepository extends JpaRepository<ConfiguracionEmpresa, Long> {

    Optional<ConfiguracionEmpresa> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    Optional<ConfiguracionEmpresa> findFirstByOrderByIdConfiguracionEmpresaAsc();
}
