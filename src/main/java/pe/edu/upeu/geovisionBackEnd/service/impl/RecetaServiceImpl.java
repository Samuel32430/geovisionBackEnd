package pe.edu.upeu.geovisionBackEnd.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pe.edu.upeu.geovisionBackEnd.dto.MedidaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Medida;
import pe.edu.upeu.geovisionBackEnd.entity.MedidaOjo;
import pe.edu.upeu.geovisionBackEnd.entity.Paciente;
import pe.edu.upeu.geovisionBackEnd.entity.Receta;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.RecetaMapper;
import pe.edu.upeu.geovisionBackEnd.repository.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.MedidaOjoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.MedidaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.PacienteRepository;
import pe.edu.upeu.geovisionBackEnd.repository.PersonaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RecetaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.RecetaService;
import pe.edu.upeu.geovisionBackEnd.util.OpticaValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@Transactional
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final MedidaRepository medidaRepository;
    private final MedidaOjoRepository medidaOjoRepository;
    private final PacienteRepository pacienteRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RecetaMapper recetaMapper;
    private final OpticaValidator opticaValidator;

    public RecetaServiceImpl(RecetaRepository recetaRepository, MedidaRepository medidaRepository,
                             MedidaOjoRepository medidaOjoRepository, PacienteRepository pacienteRepository,
                             PersonaRepository personaRepository, UsuarioRepository usuarioRepository,
                             EmpleadoRepository empleadoRepository, RecetaMapper recetaMapper,
                             OpticaValidator opticaValidator) {
        this.recetaRepository = recetaRepository;
        this.medidaRepository = medidaRepository;
        this.medidaOjoRepository = medidaOjoRepository;
        this.pacienteRepository = pacienteRepository;
        this.personaRepository = personaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.recetaMapper = recetaMapper;
        this.opticaValidator = opticaValidator;
    }

    @Override
    public RecetaResponseDTO registrarExamenVisual(RecetaRequestDTO request, String usernameAuditoria) {
        if (request.getEsExterno() == 1) {
            throw new ReglaNegocioException("Utilice el endpoint de receta externa para una prescripción externa");
        }
        return registrar(request, usernameAuditoria, 0);
    }

    @Override
    public RecetaResponseDTO registrarRecetaExterna(RecetaRequestDTO request, String usernameAuditoria) {
        return registrar(request, usernameAuditoria, 1);
    }

    private RecetaResponseDTO registrar(RecetaRequestDTO request, String username, int esExterno) {
        Empleado empleado = obtenerEmpleadoActual(username, request.getIdEmpleado());
        validarDatosNuevos(request.getMedidas());
        opticaValidator.validarMedidas(request.getMedidas());

        // Comparte el bloqueo con la gestión del paciente para serializar su desactivación.
        personaRepository.findByIdForUpdate(request.getIdPaciente())
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
        Paciente paciente = pacienteRepository.findByIdWithDetails(request.getIdPaciente())
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
        if (paciente.getEstado() != EstadoGeneralEnum.ACTIVO) {
            throw new ReglaNegocioException("El paciente debe estar activo para registrar una receta");
        }

        Receta receta = Receta.builder().paciente(paciente).empleado(empleado).esExterno(esExterno)
                .fechaRegistro(request.getFechaRegistro() == null ? null : request.getFechaRegistro().withNano(0))
                .build();
        recetaRepository.saveAndFlush(receta);
        List<Medida> medidas = new ArrayList<>();
        for (MedidaRequestDTO medida : request.getMedidas()) {
            MedidaOjo derecha = medidaOjoRepository.saveAndFlush(recetaMapper.toMedidaOjo(medida.getOjoDerecho()));
            MedidaOjo izquierda = medidaOjoRepository.saveAndFlush(recetaMapper.toMedidaOjo(medida.getOjoIzquierdo()));
            medidas.add(medidaRepository.saveAndFlush(recetaMapper.toMedida(medida, receta, derecha, izquierda)));
        }
        return recetaMapper.toRecetaResponse(receta, medidas);
    }

    private Empleado obtenerEmpleadoActual(String username, Long idSolicitado) {
        if (username == null || username.isBlank()) {
            throw new AccessDeniedException("Se requiere un usuario autenticado");
        }
        Usuario usuario = usuarioRepository.findByNombre(username)
                .filter(actual -> actual.getEstado() == EstadoGeneralEnum.ACTIVO)
                .orElseThrow(() -> new AccessDeniedException("Usuario no habilitado"));
        Empleado empleado = empleadoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .filter(actual -> actual.getEstado() == EstadoGeneralEnum.ACTIVO)
                .orElseThrow(() -> new AccessDeniedException("Se requiere un empleado activo vinculado al usuario"));
        if (!empleado.getIdPersona().equals(idSolicitado)) {
            throw new AccessDeniedException("El empleado debe corresponder al usuario autenticado");
        }
        return empleado;
    }

    private void validarDatosNuevos(List<MedidaRequestDTO> medidas) {
        for (MedidaRequestDTO medida : medidas) {
            if (medida.getIdReceta() != null || medida.getIdEnvioLaboratorio() != null
                    || medida.getOjoDerecho().getIdMedidaOjo() != null
                    || medida.getOjoIzquierdo().getIdMedidaOjo() != null) {
                throw new ReglaNegocioException("Las medidas nuevas no deben incluir IDs de receta, ojos o envío");
            }
        }
    }
}
