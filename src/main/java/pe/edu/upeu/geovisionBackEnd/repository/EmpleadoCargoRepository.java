package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.EmpleadoCargo;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoCargoRepository extends JpaRepository<EmpleadoCargo, Long> {

    List<EmpleadoCargo> findByEmpleadoIdPersona(Long idPersona);

    List<EmpleadoCargo> findByCargoIdCargo(Long idCargo);

    Optional<EmpleadoCargo> findByEmpleadoIdPersonaAndCargoIdCargo(Long idPersona, Long idCargo);

    boolean existsByEmpleadoIdPersonaAndCargoIdCargo(Long idPersona, Long idCargo);

    void deleteByEmpleadoIdPersona(Long idPersona);

    @Query("SELECT ec.cargo.nombre FROM EmpleadoCargo ec WHERE ec.empleado.idPersona = :idPersona")
    List<String> findNombresCargosByEmpleadoId(@Param("idPersona") Long idPersona);
}
