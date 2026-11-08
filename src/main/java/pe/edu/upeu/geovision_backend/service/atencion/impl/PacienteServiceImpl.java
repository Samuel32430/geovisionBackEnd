package pe.edu.upeu.geovision_backend.service.atencion.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteResponseDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.PacienteLog;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.atencion.PacienteMapper;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteLogRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.service.atencion.PacienteService;

import java.util.Set;

@Service
@Validated
@Transactional(readOnly = true)
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PersonaRepository personaRepository;
    private final PacienteLogRepository pacienteLogRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacienteMapper pacienteMapper;

    public PacienteServiceImpl(PacienteRepository pacienteRepository, PersonaRepository personaRepository,
                               PacienteLogRepository pacienteLogRepository, UsuarioRepository usuarioRepository,
                               PacienteMapper pacienteMapper) {
        this.pacienteRepository = pacienteRepository;
        this.personaRepository = personaRepository;
        this.pacienteLogRepository = pacienteLogRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacienteMapper = pacienteMapper;
    }

    @Override
    public Page<PacienteResponseDTO> listarPacientes(String termino, Boolean estado, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged() || pageable.getPageSize() > 100
                || pageable.getOffset() > Integer.MAX_VALUE) {
            throw new ReglaNegocioException("La consulta debe estar paginada con un máximo de 100 pacientes");
        }
        Sort orden = pageable.getSort();
        for (Sort.Order campo : orden) {
            if (!Set.of("id", "fechaRegistro", "persona.nombres", "persona.apellidos")
                    .contains(campo.getProperty())) {
                throw new ReglaNegocioException("Campo de ordenamiento no permitido");
            }
        }
        if (orden.getOrderFor("id") == null) {
            orden = orden.and(Sort.by("id"));
        }
        String busqueda = termino == null || termino.isBlank() ? null : termino.trim();
        if (busqueda != null) {
            busqueda = busqueda.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        }
        return pacienteRepository.buscarPacientes(busqueda, estado,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), orden))
                .map(pacienteMapper::toPacienteResponse);
    }

    @Override
    public PacienteResponseDTO obtenerPorId(Long idPaciente) {
        return pacienteMapper.toPacienteResponse(buscarPaciente(idPaciente));
    }

    @Override
    public PacienteResponseDTO obtenerPorNumeroDocumento(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new ReglaNegocioException("El número de documento es obligatorio");
        }
        return pacienteRepository.findByPersonaNumeroDocumento(numeroDocumento.trim())
                .map(pacienteMapper::toPacienteResponse)
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
    }

    @Override
    @Transactional
    public PacienteResponseDTO registrarPaciente(PacienteRequestDTO request, String usernameAuditoria) {
        Usuario actor = obtenerActor(usernameAuditoria, request.idUsuario());
        if ((request.id() == null) == (request.persona() == null)) {
            throw new ReglaNegocioException("Envíe id o los datos de persona, exclusivamente uno de ellos");
        }
        Persona persona;
        if (request.id() != null) {
            persona = bloquearPersona(request.id());
            if (pacienteRepository.existsById(persona.getId())) {
                throw new ReglaNegocioException("La persona ya está registrada como paciente");
            }
        } else {
            String documento = request.persona().numeroDocumento().trim();
            if (personaRepository.existsByNumeroDocumento(documento)) {
                throw new ReglaNegocioException("El documento ya está registrado; seleccione la persona existente");
            }
            persona = pacienteMapper.toPersona(request.persona());
            guardarPersona(persona);
        }
        Paciente paciente = new Paciente(null, persona, estado(request.estado() == null ? 1 : request.estado()), null, actor);
        pacienteRepository.saveAndFlush(paciente);
        auditar(paciente, actor);
        return pacienteMapper.toPacienteResponse(paciente);
    }

    @Override
    @Transactional
    public PacienteResponseDTO actualizarPaciente(Long idPaciente, PacienteRequestDTO request,
                                                  String usernameAuditoria) {
        Usuario actor = obtenerActor(usernameAuditoria, request.idUsuario());
        if (request.persona() == null) {
            throw new ReglaNegocioException("Los datos de persona son obligatorios para actualizar");
        }
        if (request.id() != null && !request.id().equals(idPaciente)) {
            throw new ReglaNegocioException("No se puede cambiar la identidad del paciente");
        }
        Persona persona = bloquearPersona(idPaciente);
        Paciente paciente = buscarPaciente(idPaciente);
        String documento = request.persona().numeroDocumento().trim();
        if (personaRepository.existsByNumeroDocumentoAndIdNot(documento, idPaciente)) {
            throw new ReglaNegocioException("El documento ya pertenece a otra persona");
        }
        pacienteMapper.actualizarPersona(persona, request.persona());
        guardarPersona(persona);
        // El estado se gestiona exclusivamente con cambiarEstadoPaciente.
        auditar(paciente, actor);
        return pacienteMapper.toPacienteResponse(paciente);
    }

    @Override
    @Transactional
    public PacienteResponseDTO cambiarEstadoPaciente(Long idPaciente, Integer nuevoEstado, String usernameAuditoria) {
        Boolean estado = estado(nuevoEstado);
        Usuario actor = obtenerActor(usernameAuditoria, null);
        bloquearPersona(idPaciente);
        Paciente paciente = buscarPaciente(idPaciente);
        if (!java.util.Objects.equals(paciente.getActivo(), estado)) {
            paciente.setActivo(estado);
            pacienteRepository.saveAndFlush(paciente);
            auditar(paciente, actor);
        }
        return pacienteMapper.toPacienteResponse(paciente);
    }

    private Paciente buscarPaciente(Long id) {
        return pacienteRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
    }

    private Persona bloquearPersona(Long id) {
        return personaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Persona no encontrada"));
    }

    private Usuario obtenerActor(String username, Long idSolicitado) {
        if (username == null || username.isBlank()) {
            throw new AccessDeniedException("Se requiere un usuario autenticado");
        }
        Usuario actor = usuarioRepository.findByNombre(username)
                .filter(usuario -> Boolean.TRUE.equals(usuario.getActivo()))
                .orElseThrow(() -> new AccessDeniedException("Usuario no habilitado"));
        if (idSolicitado != null && !idSolicitado.equals(actor.getId())) {
            throw new AccessDeniedException("El usuario de auditoría debe ser el usuario autenticado");
        }
        return actor;
    }

    private Boolean estado(Integer codigo) {
        if (codigo == null || (codigo != 0 && codigo != 1)) {
            throw new ReglaNegocioException("El estado debe ser 0 o 1");
        }
        return codigo == 1;
    }

    private void guardarPersona(Persona persona) {
        try {
            personaRepository.saveAndFlush(persona);
        } catch (DataIntegrityViolationException ex) {
            // La restricción UNIQUE también protege contra altas concurrentes.
            throw new ReglaNegocioException("No se pudo guardar la persona; verifique la unicidad del documento");
        }
    }

    private void auditar(Paciente paciente, Usuario actor) {
        pacienteLogRepository.saveAndFlush(new PacienteLog(null, null, actor, paciente));
    }
}
