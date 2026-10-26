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
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.PacienteService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PacienteServiceImpl implements PacienteService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final PacienteRepository pacientes;
    private final PersonaRepository personas;
    private final DocumentoRepository documentos;
    private final PacienteLogRepository logs;

    @Override
    @Transactional
    public PacienteDetalleResponseDTO registrar(PacienteRegistroRequestDTO request) {
        var actor = actorActual.escritura();
        var persona = new Persona();
        PersonaMapper.updateEntity(request.getPersona(), persona);
        persona = personas.save(persona);
        var paciente = new Paciente();
        paciente.setPersona(persona);
        paciente = pacientes.save(paciente);
        var pares = new java.util.HashSet<String>();
        for (var item : request.getDocumentos()) {
            var tipo = soporte.tipoDocumento(item.getIdTipoDocumento());
            String numero = item.getNumero().trim();
            if (!pares.add(tipo.getIdTipoDocumento() + ":" + numero)
                    || documentos.existsByTipoDocumentoIdTipoDocumentoAndNumero(tipo.getIdTipoDocumento(), numero)) {
                throw new ReglaNegocioException("El documento ya esta registrado");
            }
            var documento = new Documento();
            documento.setPersona(persona);
            documento.setTipoDocumento(tipo);
            documento.setNumero(numero);
            documentos.save(documento);
        }
        documentos.flush();
        soporte.registrarLog(paciente, actor);
        return detalle(paciente);
    }

    @Override
    @Transactional
    public PacienteResponseDTO create(PacienteRequestDTO request) {
        var actor = actorActual.escritura();
        var persona = personas.bloquear(request.getIdPersona()).orElseThrow(() -> ausente("Persona"));
        if (pacientes.existsById(request.getIdPersona())) {
            throw new ReglaNegocioException("La persona ya esta registrada como paciente");
        }
        var paciente = new Paciente();
        paciente.setPersona(persona);
        paciente = pacientes.saveAndFlush(paciente);
        soporte.registrarLog(paciente, actor);
        return PacienteMapper.convertToResponse(paciente);
    }

    @Override
    @Transactional
    public PacienteDetalleResponseDTO update(Long id, PersonaRequestDTO request) {
        var actor = actorActual.escritura();
        var persona = personas.bloquear(id).orElseThrow(() -> ausente("Persona"));
        var paciente = soporte.paciente(id);
        PersonaMapper.updateEntity(request, persona);
        soporte.registrarLog(paciente, actor);
        return detalle(paciente);
    }

    @Override
    public PacienteDetalleResponseDTO read(Long id) {
        actorActual.lectura();
        return detalle(soporte.paciente(id));
    }

    @Override
    public PaginaResponse<PacienteResponseDTO> listar(String termino, Integer estado, int pagina, int tamanio,
                                                      String ordenarPor, String direccion) {
        actorActual.lectura();
        if (estado != null && estado != 0 && estado != 1) {
            throw new ReglaNegocioException("Estado debe ser 0 o 1");
        }
        var pageable = PaginacionPaciente.crear(pagina, tamanio, ordenarPor, direccion,
                "persona.apellidos", "idPersona",
                Set.of("idPersona", "persona.nombres", "persona.apellidos", "fechaRegistro"));
        return PaginaResponse.de(pacientes.buscar(PaginacionPaciente.termino(termino), estado, pageable)
                .map(PacienteMapper::convertToResponse));
    }

    @Override
    public PaginaResponse<PacienteLogResponseDTO> listarLogs(Long id, int pagina, int tamanio) {
        actorActual.lectura();
        soporte.paciente(id);
        var pageable = PaginacionPaciente.crear(pagina, tamanio, "fechaLog", "desc",
                "fechaLog", "idPacienteLog", Set.of("fechaLog"));
        return PaginaResponse.de(logs.findByPacienteIdPersona(id, pageable).map(PacienteLogMapper::convertToResponse));
    }

    private PacienteDetalleResponseDTO detalle(Paciente paciente) {
        var dto = new PacienteDetalleResponseDTO();
        dto.setPaciente(PacienteMapper.convertToResponse(paciente));
        dto.setPersona(PersonaMapper.convertToResponse(paciente.getPersona()));
        dto.setDocumentos(documentos.findByPersonaIdPersonaOrderByIdDocumentoAsc(paciente.getIdPersona())
                .stream().map(DocumentoMapper::convertToResponse).toList());
        return dto;
    }
}

