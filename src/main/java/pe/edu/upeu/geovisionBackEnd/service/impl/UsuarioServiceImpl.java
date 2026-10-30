package pe.edu.upeu.geovisionBackEnd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.PermisoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RolDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.RolPermiso;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.UsuarioRol;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PermisoMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.RolMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.UsuarioMapper;
import pe.edu.upeu.geovisionBackEnd.repository.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.PersonaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RolPermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RolRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRolRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.UsuarioService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;
    private final PermisoMapper permisoMapper;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            PersonaRepository personaRepository,
            EmpleadoRepository empleadoRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            RolPermisoRepository rolPermisoRepository,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper,
            RolMapper rolMapper,
            PermisoMapper permisoMapper) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.rolMapper = rolMapper;
        this.permisoMapper = permisoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll(Sort.by("nombre"))
                .stream()
                .map(this::cargarUsuarioConRoles)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con ID: " + id));
        return cargarUsuarioConRoles(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        String nombre = request.getNombre().trim();
        String correo = request.getCorreo().trim().toLowerCase();

        log.info("Registrando usuario={} correo={} idPersona={}", nombre, correo, request.getIdPersona());

        if (usuarioRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe un usuario con el nombre de usuario: " + nombre);
        }

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe un usuario registrado con el correo: " + correo);
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ReglaNegocioException("La contraseña es obligatoria para nuevos usuarios");
        }

        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar los 72 bytes");
        }

        Persona persona = personaRepository.findById(request.getIdPersona())
                .orElseThrow(() -> new RecursosNoEncontradosException(
                        "Persona no encontrada con ID: " + request.getIdPersona()));

        Optional<Usuario> usuarioAsociado = usuarioRepository.findByPersonaIdPersona(persona.getIdPersona());
        if (usuarioAsociado.isPresent()) {
            throw new ReglaNegocioException("La persona seleccionada ya tiene una cuenta de usuario asignada");
        }

        List<Rol> roles = Collections.emptyList();
        if (request.getRolesIds() != null && !request.getRolesIds().isEmpty()) {
            roles = rolRepository.findAllById(request.getRolesIds());
            if (roles.size() != request.getRolesIds().size()) {
                Set<Long> encontrados = roles.stream().map(Rol::getIdRol).collect(Collectors.toSet());
                Set<Long> faltantes = new HashSet<>(request.getRolesIds());
                faltantes.removeAll(encontrados);
                throw new RecursosNoEncontradosException("Roles no encontrados con IDs: " + faltantes);
            }
        }

        EstadoGeneralEnum estado = (request.getEstado() != null)
                ? EstadoGeneralEnum.fromCodigo(request.getEstado())
                : EstadoGeneralEnum.ACTIVO;

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .correo(correo)
                .password(passwordEncoder.encode(request.getPassword()))
                .estado(estado)
                .persona(persona)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        // Si la persona es un empleado, vincular el usuario al empleado
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(persona.getIdPersona());
        empleadoOpt.ifPresent(empleado -> {
            empleado.setUsuario(guardado);
            empleadoRepository.save(empleado);
        });

        // Guardar roles en USUARIO_ROL
        if (!roles.isEmpty()) {
            List<UsuarioRol> usuarioRoles = roles.stream()
                    .map(rol -> UsuarioRol.builder()
                            .usuario(guardado)
                            .rol(rol)
                            .build())
                    .toList();
            usuarioRolRepository.saveAll(usuarioRoles);
        }

        log.info("Usuario creado exitosamente: id={} usuario={}", guardado.getIdUsuario(), guardado.getNombre());
        return cargarUsuarioConRoles(guardado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con ID: " + id));

        String nombre = request.getNombre().trim();
        String correo = request.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByNombreAndIdUsuarioNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otro usuario con el nombre: " + nombre);
        }

        if (usuarioRepository.existsByCorreoAndIdUsuarioNot(correo, id)) {
            throw new ReglaNegocioException("Ya existe otro usuario con el correo: " + correo);
        }

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);

        if (request.getEstado() != null) {
            usuario.setEstado(EstadoGeneralEnum.fromCodigo(request.getEstado()));
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new ReglaNegocioException("La contraseña no puede superar los 72 bytes");
            }
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        if (request.getRolesIds() != null) {
            actualizarRolesUsuario(usuario, request.getRolesIds(), null);
        }

        log.info("Usuario actualizado exitosamente: id={} usuario={}", actualizado.getIdUsuario(), actualizado.getNombre());
        return cargarUsuarioConRoles(actualizado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO cambiarEstado(Long id, Integer nuevoEstado, String authUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con ID: " + id));

        EstadoGeneralEnum estado = EstadoGeneralEnum.fromCodigo(nuevoEstado);

        // Protección de autodesactivación
        if (usuario.getNombre().equalsIgnoreCase(authUsername) && estado == EstadoGeneralEnum.INACTIVO) {
            throw new ReglaNegocioException("Un administrador no puede desactivar su propia cuenta");
        }

        usuario.setEstado(estado);
        Usuario actualizado = usuarioRepository.save(usuario);

        log.info("Estado de usuario actualizado: id={} usuario={} estado={}",
                actualizado.getIdUsuario(), actualizado.getNombre(), estado.getDescripcion());

        return cargarUsuarioConRoles(actualizado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO asignarRoles(Long id, Set<Long> roleIds, String authUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con ID: " + id));

        actualizarRolesUsuario(usuario, roleIds, authUsername);
        return cargarUsuarioConRoles(usuario);
    }

    private void actualizarRolesUsuario(Usuario usuario, Set<Long> roleIds, String authUsername) {
        Set<Long> ids = (roleIds != null) ? roleIds : Collections.emptySet();

        List<Rol> roles = rolRepository.findAllById(ids);
        if (roles.size() != ids.size()) {
            Set<Long> encontrados = roles.stream().map(Rol::getIdRol).collect(Collectors.toSet());
            Set<Long> faltantes = new HashSet<>(ids);
            faltantes.removeAll(encontrados);
            throw new RecursosNoEncontradosException("Roles no encontrados con IDs: " + faltantes);
        }

        // Protección de auto-despojo del rol ADMIN
        if (authUsername != null && usuario.getNombre().equalsIgnoreCase(authUsername)) {
            boolean conservaAdmin = roles.stream()
                    .anyMatch(r -> r.getNombre().equalsIgnoreCase("ADMINISTRADOR")
                            || r.getNombre().equalsIgnoreCase("ADMIN"));
            if (!conservaAdmin) {
                throw new ReglaNegocioException("Un administrador no puede retirarse a sí mismo el rol de administrador");
            }
        }

        usuarioRolRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());

        List<UsuarioRol> nuevos = roles.stream()
                .map(rol -> UsuarioRol.builder()
                        .usuario(usuario)
                        .rol(rol)
                        .build())
                .toList();
        usuarioRolRepository.saveAll(nuevos);

        log.info("Roles actualizados para usuario={}: total={}", usuario.getNombre(), roles.size());
    }

    private UsuarioResponseDTO cargarUsuarioConRoles(Usuario usuario) {
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
}
