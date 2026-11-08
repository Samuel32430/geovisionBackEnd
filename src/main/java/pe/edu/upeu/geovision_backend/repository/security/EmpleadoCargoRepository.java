package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.EmpleadoCargo;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoCargoRepository extends JpaRepository<EmpleadoCargo, Long> {

    List<EmpleadoCargo> findByEmpleadoId(Long idPersona);

    List<EmpleadoCargo> findByCargoId(Long idCargo);

    Optional<EmpleadoCargo> findByEmpleadoIdAndCargoId(Long idPersona, Long idCargo);

    boolean existsByEmpleadoIdAndCargoId(Long idPersona, Long idCargo);

    void deleteByEmpleadoId(Long idPersona);

    @Query("SELECT ec.cargo.nombre FROM EmpleadoCargo ec WHERE ec.empleado.id = :idPersona")
    List<String> findNombresCargosByEmpleadoId(@Param("idPersona") Long idPersona);
}
