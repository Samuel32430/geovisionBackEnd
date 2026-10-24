package pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.TratamientoCristal;

@Repository
public interface TratamientoCristalRepository extends JpaRepository<TratamientoCristal, Long> {
}
