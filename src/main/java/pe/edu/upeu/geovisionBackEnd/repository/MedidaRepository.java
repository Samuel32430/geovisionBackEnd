package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Medida;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedidaRepository extends JpaRepository<Medida, Long> {

    List<Medida> findByRecetaIdReceta(Long idReceta);

    Optional<Medida> findByEnvioLaboratorioIdEnvioLaboratorio(Long idEnvioLaboratorio);

    @EntityGraph(attributePaths = {"medidaOjoDerecha", "medidaOjoIzquierda"})
    Optional<Medida> findOneWithOjosByIdMedida(Long idMedida);
}
