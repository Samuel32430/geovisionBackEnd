package pe.edu.upeu.geovisionBackEnd.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdPersonaNot(String numeroDocumento, Long idPersona);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Persona p WHERE p.idPersona = :id")
    Optional<Persona> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT p FROM Persona p WHERE LOWER(p.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR p.numeroDocumento LIKE CONCAT('%', :termino, '%')")
    Page<Persona> buscarPorNombreODocumento(@Param("termino") String termino, Pageable pageable);
}
