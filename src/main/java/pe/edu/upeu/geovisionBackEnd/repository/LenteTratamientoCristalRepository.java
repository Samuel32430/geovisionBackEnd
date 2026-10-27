package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.LenteTratamientoCristal;

import java.util.List;
import java.util.Optional;

@Repository
public interface LenteTratamientoCristalRepository extends JpaRepository<LenteTratamientoCristal, Long> {

    List<LenteTratamientoCristal> findByLenteIdLente(Long idLente);

    List<LenteTratamientoCristal> findByTratamientoCristalIdTratamientoCristal(Long idTratamientoCristal);

    Optional<LenteTratamientoCristal> findByLenteIdLenteAndTratamientoCristalIdTratamientoCristal(Long idLente, Long idTratamientoCristal);

    boolean existsByLenteIdLenteAndTratamientoCristalIdTratamientoCristal(Long idLente, Long idTratamientoCristal);

    void deleteByLenteIdLente(Long idLente);
}
