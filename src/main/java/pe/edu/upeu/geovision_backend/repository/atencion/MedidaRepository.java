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

    List<Medida> findByRecetaIdReceta(Long idReceta);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByRecetaIdRecetaOrderByIdMedidaAsc(Long idReceta);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByRecetaIdRecetaInOrderByIdMedidaAsc(Collection<Long> idsReceta);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByIdMedidaInAndRecetaIdReceta(Collection<Long> idsMedida, Long idReceta);

    List<Medida> findByRecetaIdRecetaAndTipoMedida(Long idReceta, TipoMedidaEnum tipoMedida);

    Optional<Medida> findByEnvioLaboratorioIdEnvioLaboratorio(Long idEnvioLaboratorio);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    List<Medida> findByEnvioLaboratorioIdEnvioLaboratorioOrderByIdMedidaAsc(Long idEnvioLaboratorio);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    Optional<Medida> findOneWithOjosByIdMedida(Long idMedida);
}
