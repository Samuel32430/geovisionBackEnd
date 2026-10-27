package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.MedidaOjo;

import java.util.List;

@Repository
public interface MedidaOjoRepository extends JpaRepository<MedidaOjo, Long> {

    List<MedidaOjo> findByAgudezaVisual(String agudezaVisual);
}
