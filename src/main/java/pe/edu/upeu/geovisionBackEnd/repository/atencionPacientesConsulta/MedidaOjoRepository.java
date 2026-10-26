package pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.MedidaOjo;
import java.util.List;
import java.util.Optional;

public interface MedidaOjoRepository extends JpaRepository<MedidaOjo, Long> {
    List<MedidaOjo> findByMedidaIdMedidaOrderByIdMedidaOjoAsc(Long medida);
    boolean existsByMedidaIdMedidaAndTipoOjoIdTipoOjoAndIdMedidaOjoNot(Long medida, Long tipo, Long id);
}

