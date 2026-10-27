package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.TratamientoCristal;

import java.util.Optional;

@Repository
public interface TratamientoCristalRepository extends JpaRepository<TratamientoCristal, Long> {

    Optional<TratamientoCristal> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdTratamientoCristalNot(String nombre, Long idTratamientoCristal);
}
