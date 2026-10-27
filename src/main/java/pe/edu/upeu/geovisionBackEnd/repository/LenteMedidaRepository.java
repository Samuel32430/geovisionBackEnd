package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.LenteMedida;

import java.util.List;
import java.util.Optional;

@Repository
public interface LenteMedidaRepository extends JpaRepository<LenteMedida, Long> {

    List<LenteMedida> findByLenteIdLente(Long idLente);

    List<LenteMedida> findByMedidaIdMedida(Long idMedida);

    Optional<LenteMedida> findByLenteIdLenteAndMedidaIdMedida(Long idLente, Long idMedida);

    boolean existsByLenteIdLenteAndMedidaIdMedida(Long idLente, Long idMedida);

    void deleteByLenteIdLente(Long idLente);
}
