package pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Laboratorio;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long>, JpaSpecificationExecutor<Laboratorio> {

    Optional<Laboratorio> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    boolean existsByRucAndIdNot(String ruc, Long idLaboratorio);

    List<Laboratorio> findByActivo(Boolean estado);

    Page<Laboratorio> findByActivo(Boolean estado, Pageable pageable);

    Page<Laboratorio> findByNombreContainingIgnoreCaseOrRucContaining(String nombre, String ruc, Pageable pageable);
}
