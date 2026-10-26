package pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Persona p where p.idPersona = :id")
    Optional<Persona> bloquear(@Param("id") Long id);
}

