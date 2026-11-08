package pe.edu.upeu.geovision_backend.repository.atencion;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.Medida;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedidaRepository extends JpaRepository<Medida, Long> {

    List<Medida> findByRecetaId(Long idReceta);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByRecetaIdOrderByIdAsc(Long idReceta);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByRecetaIdInOrderByIdAsc(Collection<Long> idsReceta);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByIdInAndRecetaId(Collection<Long> idsMedida, Long idReceta);

    List<Medida> findByRecetaIdAndTipoMedida(Long idReceta, TipoMedidaEnum tipoMedida);

    Optional<Medida> findByEnvioLaboratorioId(Long idEnvioLaboratorio);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByEnvioLaboratorioIdOrderByIdAsc(Long idEnvioLaboratorio);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    Optional<Medida> findOneWithOjosById(Long idMedida);
}
