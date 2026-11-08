package pe.edu.upeu.geovision_backend.service.security.impl;

import lombok.RequiredArgsConstructor;
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
import pe.edu.upeu.geovision_backend.dto.security.CambioPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.LogoutResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.dto.security.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.ResetPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Rol;
import pe.edu.upeu.geovision_backend.entity.security.RolPermiso;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.security.UsuarioRol;
import pe.edu.upeu.geovision_backend.enums.TipoLogEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.security.PermisoMapper;
import pe.edu.upeu.geovision_backend.mapper.security.RolMapper;
import pe.edu.upeu.geovision_backend.mapper.security.UsuarioMapper;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRolRepository;
import pe.edu.upeu.geovision_backend.security.service.JwtService;
import pe.edu.upeu.geovision_backend.service.security.AuthService;
import pe.edu.upeu.geovision_backend.service.security.EmailService;
import pe.edu.upeu.geovision_backend.service.security.UsuarioLogService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        String identificador = request.username().trim();
        log.info("Intento de autenticación: usuario/correo={}", identificador);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identificador, request.password()));
        } catch (AuthenticationException ex) {
            log.warn("Fallo de autenticación para {}: {}", identificador, ex.getMessage());
            usuarioRepository.findByNombreOrCorreo(identificador, identificador)
                    .ifPresent(u -> usuarioLogService.registrar(u, TipoLogEnum.INTENTO_FALLIDO));
            throw new BadCredentialsException("Nombre de usuario o contraseña incorrectos");
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByNombreOrCorreo(principal.getUsername(), principal.getUsername())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ReglaNegocioException("La cuenta de usuario está inactiva");
        }

        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioId(usuario.getId());
        if (empleadoOpt.isPresent() && !Boolean.TRUE.equals(empleadoOpt.get().getActivo())) {
            throw new ReglaNegocioException("El empleado asociado a esta cuenta se encuentra inactivo");
        }

        String token = jwtService.generarToken(principal);
        usuarioLogService.registrar(usuario, TipoLogEnum.LOGIN);

        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioId(usuario.getId());
        List<String> roles = new ArrayList<>();
        Set<String> permisos = new TreeSet<>();

        for (UsuarioRol ur : usuarioRoles) {
            if (ur.getRol() != null) {
                roles.add(ur.getRol().getNombre());
                List<String> perms = rolPermisoRepository.findNombresPermisosByRolId(ur.getRol().getId());
                permisos.addAll(perms);
            }
        }

        Persona persona = usuario.getPersona();
        Long idPersona = persona != null ? persona.getId() : null;
        String nombreCompleto = (persona != null)
                ? (persona.getNombres() + " " + persona.getApellidos()).trim()
                : usuario.getNombre();

        log.info("Autenticación exitosa: usuario={} roles={}", usuario.getNombre(), roles);

        return new LoginResponseDTO(token, "Bearer", usuario.getId(), usuario.getNombre(), usuario.getCorreo(), idPersona, nombreCompleto, roles, new ArrayList<>(permisos));
    }

    @Override
    @Transactional
    public LogoutResponseDTO logout(String username) {
        log.info("Cierre de sesión solicitado por: {}", username);
        usuarioRepository.findByNombreOrCorreo(username, username)
                .ifPresent(u -> usuarioLogService.registrar(u, TipoLogEnum.LOGOUT));

        return new LogoutResponseDTO("Sesión cerrada exitosamente", true, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO usuarioActual(String username) {
        Usuario usuario = usuarioRepository.findByNombreOrCorreo(username, username)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con: " + username));

        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioId(usuario.getId());
        Set<RolDTO> rolesDTO = usuarioRoles.stream()
                .map(ur -> {
                    Rol rol = ur.getRol();
                    List<RolPermiso> rolPermisos = rolPermisoRepository.findByRolId(rol.getId());
                    Set<PermisoDTO> permisosDTO = rolPermisos.stream()
                            .map(rp -> PermisoMapper.toDTO(rp.getPermiso()))
                            .collect(Collectors.toSet());
                    return RolMapper.toDTO(rol, permisosDTO);
                })
                .collect(Collectors.toSet());

        return UsuarioMapper.toUsuarioResponse(usuario, rolesDTO);
    }

    @Override
    @Transactional
    public void cambiarPassword(String username, CambioPasswordRequestDTO request) {
        Usuario usuario = usuarioRepository.findByNombreOrCorreo(username, username)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado: " + username));

        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPassword())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta");
        }

        if (!request.nuevoPassword().equals(request.confirmarPassword())) {
            throw new ReglaNegocioException("La nueva contraseña y su confirmación no coinciden");
        }

        if (request.nuevoPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar los 72 bytes");
        }

        if (passwordEncoder.matches(request.nuevoPassword(), usuario.getPassword())) {
            throw new ReglaNegocioException("La nueva contraseña no puede ser igual a la anterior");
        }

        usuario.setPassword(passwordEncoder.encode(request.nuevoPassword()));
        usuarioRepository.save(usuario);
        usuarioLogService.registrar(usuario, TipoLogEnum.CAMBIO_PASSWORD);

        log.info("Contraseña actualizada exitosamente para usuario: {}", usuario.getNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public void solicitarRecuperacionPassword(RecuperarPasswordRequestDTO request) {
        String correo = request.correo().trim().toLowerCase();
        log.info("Solicitud de recuperación de contraseña para correo: {}", correo);

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursosNoEncontradosException(
                        "No se encontró ninguna cuenta registrada con el correo: " + correo));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
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
        String token = request.token().trim();

        if (!jwtService.validarTokenRecuperacion(token)) {
            throw new ReglaNegocioException("El token de recuperación no es válido o ha expirado");
        }

        if (!request.nuevoPassword().equals(request.confirmarPassword())) {
            throw new ReglaNegocioException("La nueva contraseña y su confirmación no coinciden");
        }

        if (request.nuevoPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar los 72 bytes");
        }

        String email = jwtService.extraerEmailRecuperacion(token);
        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new RecursosNoEncontradosException("No se encontró el usuario asociado al token"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ReglaNegocioException("La cuenta de usuario se encuentra inactiva");
        }

        usuario.setPassword(passwordEncoder.encode(request.nuevoPassword()));
        usuarioRepository.save(usuario);
        usuarioLogService.registrar(usuario, TipoLogEnum.CAMBIO_PASSWORD);

        log.info("Contraseña restablecida exitosamente para usuario={}", usuario.getNombre());
    }
}
