package pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.CatalogoPacienteService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoPacienteServiceImpl implements CatalogoPacienteService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final TipoDocumentoRepository tiposDocumento;
    private final TipoMedidaRepository tiposMedida;
    private final TipoOjoRepository tiposOjo;

    @Override
    public List<CatalogoPacienteResponseDTO> tiposDocumento() {
        actorActual.lectura();
        return tiposDocumento.findAll(Sort.by("nombre")).stream()
                .map(t -> catalogo(t.getIdTipoDocumento(), t.getNombre())).toList();
    }

    @Override
    public List<CatalogoPacienteResponseDTO> tiposMedida() {
        actorActual.lectura();
        return tiposMedida.findAll(Sort.by("nombre")).stream()
                .map(t -> catalogo(t.getIdTipoMedida(), t.getNombre())).toList();
    }

    @Override
    public List<CatalogoPacienteResponseDTO> tiposOjo() {
        actorActual.lectura();
        return tiposOjo.findAll(Sort.by("nombre")).stream()
                .map(t -> catalogo(t.getIdTipoOjo(), t.getNombre())).toList();
    }

    private CatalogoPacienteResponseDTO catalogo(Long id, String nombre) {
        var dto = new CatalogoPacienteResponseDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        return dto;
    }
}

