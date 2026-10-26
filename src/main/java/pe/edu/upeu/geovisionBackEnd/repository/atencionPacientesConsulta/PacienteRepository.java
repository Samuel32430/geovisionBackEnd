package pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    @EntityGraph(attributePaths = "persona")
    @Query("""
        select p from Paciente p join p.persona per
        where (:estado is null or p.estado = :estado) and
         (:termino is null or lower(concat(concat(per.nombres, ' '), per.apellidos)) like :termino escape '!'
          or lower(per.telefono) like :termino escape '!'
          or exists (select d.idDocumento from Documento d where d.persona = per
                     and lower(d.numero) like :termino escape '!'))
        """)
    Page<Paciente> buscar(@Param("termino") String termino, @Param("estado") Integer estado, Pageable pageable);
}

