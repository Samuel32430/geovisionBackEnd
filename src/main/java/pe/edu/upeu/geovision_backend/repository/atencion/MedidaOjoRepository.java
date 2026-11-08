package pe.edu.upeu.geovision_backend.repository.atencion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;

import java.util.List;

@Repository
public interface MedidaOjoRepository extends JpaRepository<MedidaOjo, Long> {

    List<MedidaOjo> findByAgudezaVisual(String agudezaVisual);
}
