package pe.edu.upeu.geovision_backend.repository.atencion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.LenteMedida;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface LenteMedidaRepository extends JpaRepository<LenteMedida, Long> {

    @EntityGraph(attributePaths = {"medida.medidaOjoDerecha", "medida.medidaOjoIzquierda"})
    List<LenteMedida> findByLenteIdLente(Long idLente);

    @EntityGraph(attributePaths = {"medida.medidaOjoDerecha", "medida.medidaOjoIzquierda"})
    List<LenteMedida> findByLenteIdLenteInOrderByMedidaIdMedidaAsc(Collection<Long> idsLente);

    List<LenteMedida> findByMedidaIdMedida(Long idMedida);

    Optional<LenteMedida> findByLenteIdLenteAndMedidaIdMedida(Long idLente, Long idMedida);

    boolean existsByLenteIdLenteAndMedidaIdMedida(Long idLente, Long idMedida);

    void deleteByLenteIdLente(Long idLente);
}
