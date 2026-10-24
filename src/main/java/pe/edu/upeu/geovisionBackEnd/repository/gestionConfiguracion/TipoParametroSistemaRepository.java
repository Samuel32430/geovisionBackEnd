package pe.edu.upeu.geovisionBackEnd.repository.gestionConfiguracion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.gestionConfiguracion.TipoParametroSistema;

@Repository
public interface TipoParametroSistemaRepository extends JpaRepository<TipoParametroSistema, Long> {
}
