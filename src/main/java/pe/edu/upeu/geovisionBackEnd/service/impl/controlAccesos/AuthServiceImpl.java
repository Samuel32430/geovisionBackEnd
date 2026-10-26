package pe.edu.upeu.geovisionBackEnd.service.impl.controlAccesos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LogoutResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Auditoria;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.UsuarioRol;
import pe.edu.upeu.geovisionBackEnd.enums.controlAccesos.AccionAuditoriaEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos.UsuarioMapper;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.AuditoriaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.RolPermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.UsuarioRolRepository;
import pe.edu.upeu.geovisionBackEnd.security.JwtService;
import pe.edu.upeu.geovisionBackEnd.service.service.controlAccesos.AuthService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementación de la lógica de autenticación y sesiones (CU-SEG15 Login y CU-SEG16 Logout).
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioMapper usuarioMapper;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository,
            RolPermisoRepository rolPermisoRepository,
            EmpleadoRepository empleadoRepository,
            AuditoriaRepository auditoriaRepository,
            UsuarioMapper usuarioMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.empleadoRepository = empleadoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        String termino = request.getUsername().trim();
        log.info("Intento de inicio de sesión para el identificador: {}", termino);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(termino, request.getPassword()));

        UserDetails principal = (UserDetails) auth.getPrincipal();

        Usuario usuario = usuarioRepository.findByNombreOrCorreo(principal.getUsername(), principal.getUsername())
                .or(() -> usuarioRepository.findByNombreOrCorreo(termino, termino))
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con: " + termino));

        String token = jwtService.generarToken(principal);

        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        List<String> roles = usuarioRoles.stream()
                .filter(ur -> ur.getRol() != null)
                .map(ur -> ur.getRol().getNombre())
                .toList();

        Set<String> permisosSet = new HashSet<>();
        for (UsuarioRol ur : usuarioRoles) {
            if (ur.getRol() != null) {
                permisosSet.addAll(rolPermisoRepository.findNombresPermisosByRolId(ur.getRol().getIdRol()));
            }
        }
        List<String> permisos = new ArrayList<>(permisosSet);

        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        Long idEmpleado = empleadoOpt.map(Empleado::getIdPersona).orElse(null);
        String nombreCompleto = empleadoOpt
                .map(e -> e.getPersona() != null
                        ? (e.getPersona().getNombres() + " " + e.getPersona().getApellidos()).trim()
                        : null)
                .orElse(null);

        registrarAuditoriaLogin(usuario);

        log.info("Inicio de sesión exitoso: idUsuario={}, nombre={}, roles={}",
                usuario.getIdUsuario(), usuario.getNombre(), roles);

        return LoginResponseDTO.builder()
                .token(token)
                .tipoToken("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .roles(roles)
                .permisos(permisos)
                .idEmpleado(idEmpleado)
                .nombreCompleto(nombreCompleto)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO usuarioActual(String usernameOrEmail) {
        String termino = usernameOrEmail.trim();
        Usuario usuario = usuarioRepository.findByNombreOrCorreo(termino, termino)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado: " + termino));

        List<String> roles = usuarioRolRepository.findNombresRolesByUsuarioId(usuario.getIdUsuario());
        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        Long idPersona = empleadoOpt.map(Empleado::getIdPersona).orElse(null);
        String nombreCompleto = empleadoOpt
                .map(e -> e.getPersona() != null
                        ? (e.getPersona().getNombres() + " " + e.getPersona().getApellidos()).trim()
                        : null)
                .orElse(null);

        return usuarioMapper.toResponseDTO(usuario, roles, idPersona, nombreCompleto);
    }

    @Override
    @Transactional
    public LogoutResponseDTO logout(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();
            log.info("Cerrando sesión para el usuario: {}", username);

            Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreOrCorreo(username, username);
            usuarioOpt.ifPresent(this::registrarAuditoriaLogout);
        }

        SecurityContextHolder.clearContext();

        return LogoutResponseDTO.builder()
                .mensaje("Sesión cerrada exitosamente")
                .fecha(LocalDateTime.now())
                .build();
    }

    private void registrarAuditoriaLogin(Usuario usuario) {
        try {
            Auditoria auditoria = Auditoria.builder()
                    .fechaLog(LocalDateTime.now())
                    .entidad("USUARIO")
                    .idEntidad(usuario.getIdUsuario())
                    .accion(AccionAuditoriaEnum.INICIAR_SESION.name())
                    .datosNuevos("Inicio de sesión exitoso. Usuario: " + usuario.getNombre() + " (" + usuario.getCorreo() + ")")
                    .usuario(usuario)
                    .build();
            auditoriaRepository.save(auditoria);
        } catch (Exception ex) {
            log.error("Error al registrar auditoría de login para usuario {}: {}",
                    usuario.getNombre(), ex.getMessage());
        }
    }

    private void registrarAuditoriaLogout(Usuario usuario) {
        try {
            Auditoria auditoria = Auditoria.builder()
                    .fechaLog(LocalDateTime.now())
                    .entidad("USUARIO")
                    .idEntidad(usuario.getIdUsuario())
                    .accion(AccionAuditoriaEnum.CERRAR_SESION.name())
                    .datosNuevos("Cierre de sesión seguro. Usuario: " + usuario.getNombre())
                    .usuario(usuario)
                    .build();
            auditoriaRepository.save(auditoria);
        } catch (Exception ex) {
            log.error("Error al registrar auditoría de logout para usuario {}: {}",
                    usuario.getNombre(), ex.getMessage());
        }
    }
}
