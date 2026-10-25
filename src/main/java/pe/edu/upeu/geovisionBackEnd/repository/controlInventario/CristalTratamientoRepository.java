package pe.edu.upeu.geovisionBackEnd.repository.controlInventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.CristalTratamiento;

import java.util.List;

@Repository
public interface CristalTratamientoRepository extends JpaRepository<CristalTratamiento, Long> {

    List<CristalTratamiento> findByIdCristal(Long idCristal);

    boolean existsByIdCristalAndIdTratamiento(Long idCristal, Long idTratamiento);

    void deleteByIdCristal(Long idCristal);
}
