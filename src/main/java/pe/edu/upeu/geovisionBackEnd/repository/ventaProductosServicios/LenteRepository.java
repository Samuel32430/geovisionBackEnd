package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Lente;

import java.util.List;

@Repository
public interface LenteRepository extends JpaRepository<Lente, Long> {

    List<Lente> findByReceta_IdReceta(Long idReceta);
}
