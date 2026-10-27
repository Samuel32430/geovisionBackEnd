package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Laboratorio;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    Optional<Laboratorio> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    boolean existsByRucAndIdLaboratorioNot(String ruc, Long idLaboratorio);

    List<Laboratorio> findByEstado(EstadoGeneralEnum estado);

    Page<Laboratorio> findByEstado(EstadoGeneralEnum estado, Pageable pageable);

    Page<Laboratorio> findByNombreContainingIgnoreCaseOrRucContaining(String nombre, String ruc, Pageable pageable);
}
