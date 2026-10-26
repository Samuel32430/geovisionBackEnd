package pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Medida;
import java.util.List;
import java.util.Optional;

public interface MedidaRepository extends JpaRepository<Medida, Long> {
    List<Medida> findByExamenIdExamenOrderByIdMedidaAsc(Long examen);
    boolean existsByExamenIdExamenAndTipoMedidaIdTipoMedidaAndIdMedidaNot(Long examen, Long tipo, Long id);
}

