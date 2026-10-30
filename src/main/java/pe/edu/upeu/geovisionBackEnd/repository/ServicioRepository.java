package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Servicio;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    Optional<Servicio> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Servicio> findByEstado(EstadoGeneralEnum estado);

    Page<Servicio> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
