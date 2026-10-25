package pe.edu.upeu.geovisionBackEnd.repository.controlInventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Cristal;

import java.util.List;

@Repository
public interface CristalRepository extends JpaRepository<Cristal, Long> {

    List<Cristal> findByIdMaterialCristal(Long idMaterialCristal);
}
