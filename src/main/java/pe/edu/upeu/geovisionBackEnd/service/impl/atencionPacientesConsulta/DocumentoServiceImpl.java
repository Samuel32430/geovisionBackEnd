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
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.DocumentoService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoServiceImpl implements DocumentoService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final DocumentoRepository documentos;

    @Override
    @Transactional
    public DocumentoResponseDTO create(DocumentoRequestDTO request) {
        var actor = actorActual.escritura();
        var persona = soporte.persona(request.getIdPersona());
        var tipo = soporte.tipoDocumento(request.getIdTipoDocumento());
        var numero = request.getNumero().trim();
        if (documentos.existsByTipoDocumentoIdTipoDocumentoAndNumero(tipo.getIdTipoDocumento(), numero)) {
            throw new ReglaNegocioException("El documento ya esta registrado");
        }
        var documento = new Documento();
        DocumentoMapper.updateEntity(request, documento);
        documento.setPersona(persona);
        documento.setTipoDocumento(tipo);
        documento = documentos.saveAndFlush(documento);
        soporte.registrarLogPersona(persona.getIdPersona(), actor);
        return DocumentoMapper.convertToResponse(documento);
    }

    @Override
    @Transactional
    public DocumentoResponseDTO update(Long id, DocumentoRequestDTO request) {
        var actor = actorActual.escritura();
        var documento = documentos.findById(id).orElseThrow(() -> ausente("Documento"));
        if (!documento.getPersona().getIdPersona().equals(request.getIdPersona())) {
            throw new ReglaNegocioException("No se puede transferir un documento a otra persona");
        }
        var tipo = soporte.tipoDocumento(request.getIdTipoDocumento());
        if (documentos.existsByTipoDocumentoIdTipoDocumentoAndNumeroAndIdDocumentoNot(
                tipo.getIdTipoDocumento(), request.getNumero().trim(), id)) {
            throw new ReglaNegocioException("El documento ya esta registrado");
        }
        DocumentoMapper.updateEntity(request, documento);
        documento.setTipoDocumento(tipo);
        documentos.flush();
        soporte.registrarLogPersona(request.getIdPersona(), actor);
        return DocumentoMapper.convertToResponse(documento);
    }

    @Override
    public DocumentoResponseDTO read(Long id) {
        actorActual.lectura();
        return DocumentoMapper.convertToResponse(documentos.findById(id).orElseThrow(() -> ausente("Documento")));
    }

    @Override
    public List<DocumentoResponseDTO> listarPorPersona(Long idPersona) {
        actorActual.lectura();
        soporte.persona(idPersona);
        return documentos.findByPersonaIdPersonaOrderByIdDocumentoAsc(idPersona).stream()
                .map(DocumentoMapper::convertToResponse).toList();
    }
}

