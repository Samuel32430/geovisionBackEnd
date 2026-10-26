package pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Documento;
import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByPersonaIdPersonaOrderByIdDocumentoAsc(Long persona);
    boolean existsByTipoDocumentoIdTipoDocumentoAndNumero(Long tipo, String numero);
    boolean existsByTipoDocumentoIdTipoDocumentoAndNumeroAndIdDocumentoNot(Long tipo, String numero, Long id);
}

