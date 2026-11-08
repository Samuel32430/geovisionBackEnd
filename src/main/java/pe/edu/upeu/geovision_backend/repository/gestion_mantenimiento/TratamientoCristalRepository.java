package pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;

import java.util.Optional;

@Repository
public interface TratamientoCristalRepository extends JpaRepository<TratamientoCristal, Long>, JpaSpecificationExecutor<TratamientoCristal> {

    Optional<TratamientoCristal> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long idTratamientoCristal);
}
