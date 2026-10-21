package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.LenteMedida;

import java.util.List;

@Repository
public interface LenteMedidaRepository extends JpaRepository<LenteMedida, Long> {

    List<LenteMedida> findByLente_IdLente(Long idLente);
}
