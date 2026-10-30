package pe.edu.upeu.geovisionBackEnd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.CambioPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LogoutResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PermisoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ResetPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RolDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.RolPermiso;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.UsuarioRol;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoLogEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PermisoMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.RolMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.UsuarioMapper;
import pe.edu.upeu.geovisionBackEnd.repository.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RolPermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRolRepository;
import pe.edu.upeu.geovisionBackEnd.security.JwtService;
import pe.edu.upeu.geovisionBackEnd.service.service.AuthService;
import pe.edu.upeu.geovisionBackEnd.service.service.EmailService;
import pe.edu.upeu.geovisionBackEnd.service.service.UsuarioLogService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioLogService usuarioLogService;
    private final EmailService emailService;
    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;
    private final PermisoMapper permisoMapper;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            EmpleadoRepository empleadoRepository,
            UsuarioRolRepository usuarioRolRepository,
            RolPermisoRepository rolPermisoRepository,
            PasswordEncoder passwordEncoder,
            UsuarioLogService usuarioLogService,
            EmailService emailService,
            UsuarioMapper usuarioMapper,
            RolMapper rolMapper,
            PermisoMapper permisoMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioLogService = usuarioLogService;
        this.emailService = emailService;
        this.usuarioMapper = usuarioMapper;
        this.rolMapper = rolMapper;
        this.permisoMapper = permisoMapper;
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        String identificador = request.getUsername().trim();
        log.info("Intento de autenticación: usuario/correo={}", identificador);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identificador, request.getPassword()));
        } catch (AuthenticationException ex) {
            log.warn("Fallo de autenticación para {}: {}", identificador, ex.getMessage());
            usuarioRepository.findByNombreOrCorreo(identificador, identificador)
                    .ifPresent(u -> usuarioLogService.registrar(u, TipoLogEnum.INTENTO_FALLIDO));
            throw new BadCredentialsException("Nombre de usuario o contraseña incorrectos");
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByNombreOrCorreo(principal.getUsername(), principal.getUsername())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado"));

        if (usuario.getEstado() != EstadoGeneralEnum.ACTIVO) {
            throw new ReglaNegocioException("La cuenta de usuario está inactiva");
        }

        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        if (empleadoOpt.isPresent() && empleadoOpt.get().getEstado() != EstadoGeneralEnum.ACTIVO) {
            throw new ReglaNegocioException("El empleado asociado a esta cuenta se encuentra inactivo");
        }

        String token = jwtService.generarToken(principal);
        usuarioLogService.registrar(usuario, TipoLogEnum.LOGIN);

        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        List<String> roles = new ArrayList<>();
        Set<String> permisos = new TreeSet<>();

        for (UsuarioRol ur : usuarioRoles) {
            if (ur.getRol() != null) {
                roles.add(ur.getRol().getNombre());
                List<String> perms = rolPermisoRepository.findNombresPermisosByRolId(ur.getRol().getIdRol());
                permisos.addAll(perms);
            }
        }

        Persona persona = usuario.getPersona();
        Long idPersona = persona != null ? persona.getIdPersona() : null;
        String nombreCompleto = (persona != null)
                ? (persona.getNombres() + " " + persona.getApellidos()).trim()
                : usuario.getNombre();

        log.info("Autenticación exitosa: usuario={} roles={}", usuario.getNombre(), roles);

        return LoginResponseDTO.builder()
                .token(token)
                .tipoToken("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getNombre())
                .correo(usuario.getCorreo())
                .idPersona(idPersona)
                .nombreCompleto(nombreCompleto)
                .roles(roles)
                .permisos(new ArrayList<>(permisos))
                .build();
    }

    @Override
    @Transactional
    public LogoutResponseDTO logout(String username) {
        log.info("Cierre de sesión solicitado por: {}", username);
        usuarioRepository.findByNombreOrCorreo(username, username)
                .ifPresent(u -> usuarioLogService.registrar(u, TipoLogEnum.LOGOUT));

        return LogoutResponseDTO.builder()
                .mensaje("Sesión cerrada exitosamente")
                .exitoso(true)
                .fecha(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO usuarioActual(String username) {
        Usuario usuario = usuarioRepository.findByNombreOrCorreo(username, username)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con: " + username));

        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        Set<RolDTO> rolesDTO = usuarioRoles.stream()
                .map(ur -> {
                    Rol rol = ur.getRol();
                    List<RolPermiso> rolPermisos = rolPermisoRepository.findByRolIdRol(rol.getIdRol());
                    Set<PermisoDTO> permisosDTO = rolPermisos.stream()
                            .map(rp -> permisoMapper.toDTO(rp.getPermiso()))
                            .collect(Collectors.toSet());
                    return rolMapper.toDTO(rol, permisosDTO);
                })
                .collect(Collectors.toSet());

        return usuarioMapper.toUsuarioResponse(usuario, rolesDTO);
    }

    @Override
    @Transactional
    public void cambiarPassword(String username, CambioPasswordRequestDTO request) {
        Usuario usuario = usuarioRepository.findByNombreOrCorreo(username, username)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado: " + username));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta");
        }

        if (!request.getNuevoPassword().equals(request.getConfirmarPassword())) {
            throw new ReglaNegocioException("La nueva contraseña y su confirmación no coinciden");
        }

        if (request.getNuevoPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar los 72 bytes");
        }

        if (passwordEncoder.matches(request.getNuevoPassword(), usuario.getPassword())) {
            throw new ReglaNegocioException("La nueva contraseña no puede ser igual a la anterior");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNuevoPassword()));
        usuarioRepository.save(usuario);
        usuarioLogService.registrar(usuario, TipoLogEnum.CAMBIO_PASSWORD);

        log.info("Contraseña actualizada exitosamente para usuario: {}", usuario.getNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public void solicitarRecuperacionPassword(RecuperarPasswordRequestDTO request) {
        String correo = request.getCorreo().trim().toLowerCase();
        log.info("Solicitud de recuperación de contraseña para correo: {}", correo);

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursosNoEncontradosException(
                        "No se encontró ninguna cuenta registrada con el correo: " + correo));

        if (usuario.getEstado() != EstadoGeneralEnum.ACTIVO) {
            throw new ReglaNegocioException("La cuenta de usuario asociada a este correo se encuentra inactiva");
        }

        String token = jwtService.generarTokenRecuperacion(usuario.getCorreo(), 15);
        emailService.enviarRecuperacionPassword(usuario.getCorreo(), token, usuario.getNombre());
    }

    @Override
    public boolean validarTokenRecuperacion(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return jwtService.validarTokenRecuperacion(token.trim());
    }

    @Override
    @Transactional
    public void restablecerPassword(ResetPasswordRequestDTO request) {
        String token = request.getToken().trim();

        if (!jwtService.validarTokenRecuperacion(token)) {
            throw new ReglaNegocioException("El token de recuperación no es válido o ha expirado");
        }

        if (!request.getNuevoPassword().equals(request.getConfirmarPassword())) {
            throw new ReglaNegocioException("La nueva contraseña y su confirmación no coinciden");
        }

        if (request.getNuevoPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar los 72 bytes");
        }

        String email = jwtService.extraerEmailRecuperacion(token);
        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new RecursosNoEncontradosException("No se encontró el usuario asociado al token"));

        if (usuario.getEstado() != EstadoGeneralEnum.ACTIVO) {
            throw new ReglaNegocioException("La cuenta de usuario se encuentra inactiva");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNuevoPassword()));
        usuarioRepository.save(usuario);
        usuarioLogService.registrar(usuario, TipoLogEnum.CAMBIO_PASSWORD);

        log.info("Contraseña restablecida exitosamente para usuario={}", usuario.getNombre());
    }
}
