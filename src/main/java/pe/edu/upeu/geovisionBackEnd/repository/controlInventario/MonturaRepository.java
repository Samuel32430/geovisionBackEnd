package pe.edu.upeu.geovisionBackEnd.repository.controlInventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Montura;

import java.util.List;

@Repository
public interface MonturaRepository extends JpaRepository<Montura, Long> {

    List<Montura> findByIdModeloMontura(Long idModeloMontura);

    List<Montura> findByIdMaterialMontura(Long idMaterialMontura);
}
