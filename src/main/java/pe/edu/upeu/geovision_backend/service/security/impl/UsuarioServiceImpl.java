package pe.edu.upeu.geovision_backend.service.security.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.PersonaUsuarioResumenDTO;
import pe.edu.upeu.geovision_backend.dto.security.RegistroPersonaUsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Rol;
import pe.edu.upeu.geovision_backend.entity.security.RolPermiso;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.security.UsuarioRol;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.security.PermisoMapper;
import pe.edu.upeu.geovision_backend.mapper.security.RolMapper;
import pe.edu.upeu.geovision_backend.mapper.security.UsuarioMapper;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRolRepository;
import pe.edu.upeu.geovision_backend.service.security.UsuarioService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
        return crearCuentaInterna(request);
    }

    // Ambos métodos públicos de alta abren la transacción antes de llamar a esta lógica común.
    private UsuarioResponseDTO crearCuentaInterna(UsuarioRequestDTO request) {
        String nombre = request.nombre().trim();
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        if (nombre.length() < 3 || nombre.length() > 100) {
            throw new ReglaNegocioException("El nombre de usuario debe tener entre 3 y 100 caracteres");
        }
        if (usuarioRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese nombre de usuario");
        }
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo electrónico");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new ReglaNegocioException("La contraseña es obligatoria para nuevos usuarios");
        }
        if (request.password().length() < 6 || request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña debe tener al menos 6 caracteres y no superar los 72 bytes");
        }

        // Serializar las altas para una misma persona, incluso cuando aún no existe su usuario.
        Persona persona = personaRepository.findByIdForUpdate(request.idPersona())
                .orElseThrow(() -> new RecursosNoEncontradosException("La persona seleccionada no existe"));
        if (usuarioRepository.findByPersonaId(persona.getId()).isPresent()) {
            throw new ReglaNegocioException("La persona seleccionada ya tiene una cuenta de usuario asignada");
        }
        Optional<Empleado> existente = empleadoRepository.findById(persona.getId());
        if (existente.isEmpty() || !Boolean.TRUE.equals(existente.get().getActivo())) {
            throw new ReglaNegocioException("Solo se pueden crear cuentas para empleados activos");
        }
        if (existente.get().getUsuario() != null) {
            throw new ReglaNegocioException("El empleado ya está vinculado a una cuenta; revise su asociación");
        }
        if (request.empleado() != null) {
            if (request.empleado().fechaContrato() == null) {
                throw new ReglaNegocioException("La fecha de contrato es obligatoria para registrar al empleado");
            }
            if (existente.isPresent() && !Boolean.TRUE.equals(existente.get().getActivo())) {
                throw new ReglaNegocioException("El empleado está inactivo; no se reactiva mediante el registro de usuarios");
            }
        }

        List<Rol> roles = Collections.emptyList();
        if (request.rolesIds() != null && !request.rolesIds().isEmpty()) {
            roles = rolRepository.findAllById(request.rolesIds());
            if (roles.size() != request.rolesIds().size()) {
                throw new RecursosNoEncontradosException("Uno o más roles seleccionados no existen");
            }
        }
        if (request.estado() != null && request.estado() != 0 && request.estado() != 1) {
            throw new ReglaNegocioException("El estado debe ser 0 o 1");
        }
        Boolean estado = request.estado() == null || request.estado() == 1;
        Usuario usuario = new Usuario(null, nombre, passwordEncoder.encode(request.password()), correo, estado, persona);
        try {
            Usuario guardado = usuarioRepository.saveAndFlush(usuario);
            Empleado empleado = existente.orElse(null);
            if (empleado != null) {
                empleado.setUsuario(guardado);
                empleadoRepository.saveAndFlush(empleado);
            }
            List<UsuarioRol> asociaciones = roles.stream().map(rol -> new UsuarioRol(null, guardado, rol)).toList();
            usuarioRolRepository.saveAllAndFlush(asociaciones);
            log.info("Cuenta creada: idUsuario={}", guardado.getId());
            return cargarUsuarioConRoles(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se pudo registrar la cuenta; compruebe el usuario, correo y asociaciones duplicadas");
        }
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request) {
        if (request.empleado() != null) {
            throw new ReglaNegocioException("El alta laboral solo se admite al registrar la cuenta");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con ID: " + id));

        String nombre = request.nombre().trim();
        String correo = request.correo().trim().toLowerCase();

        if (usuarioRepository.existsByNombreAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otro usuario con el nombre: " + nombre);
        }

        if (usuarioRepository.existsByCorreoAndIdNot(correo, id)) {
            throw new ReglaNegocioException("Ya existe otro usuario con el correo: " + correo);
        }

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);

        if (request.estado() != null) {
            if (request.estado() != 0 && request.estado() != 1) {
                throw new ReglaNegocioException("El estado debe ser 0 o 1");
            }
            usuario.setActivo(request.estado() == 1);
        }

        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new ReglaNegocioException("La contraseña no puede superar los 72 bytes");
            }
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        if (request.rolesIds() != null) {
            actualizarRolesUsuario(usuario, request.rolesIds(), null);
        }

        log.info("Usuario actualizado exitosamente: id={} usuario={}", actualizado.getId(), actualizado.getNombre());
        return cargarUsuarioConRoles(actualizado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO cambiarEstado(Long id, Integer nuevoEstado, String authUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado con ID: " + id));

        if (nuevoEstado == null || (nuevoEstado != 0 && nuevoEstado != 1)) {
            throw new ReglaNegocioException("El estado debe ser 0 o 1");
        }
        Boolean estado = nuevoEstado == 1;

        // Protección de autodesactivación
        if (usuario.getNombre().equalsIgnoreCase(authUsername) && Boolean.FALSE.equals(estado)) {
            throw new ReglaNegocioException("Un administrador no puede desactivar su propia cuenta");
        }

        usuario.setActivo(estado);
        Usuario actualizado = usuarioRepository.save(usuario);

        log.info("Estado de usuario actualizado: id={} usuario={} estado={}",
                actualizado.getId(), actualizado.getNombre(), Boolean.TRUE.equals(estado) ? "Activo" : "Inactivo");

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
        usuarioRepository.findByIdForUpdate(usuario.getId())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado"));
        Set<Long> ids = (roleIds != null) ? roleIds : Collections.emptySet();

        List<Rol> roles = rolRepository.findAllById(ids);
        if (roles.size() != ids.size()) {
            Set<Long> encontrados = roles.stream().map(Rol::getId).collect(Collectors.toSet());
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

        // Conservar asociaciones vigentes evita reinsertar pares todavía pendientes de borrar.
        List<UsuarioRol> actuales = usuarioRolRepository.findByUsuarioId(usuario.getId());
        Set<Long> existentes = actuales.stream().map(ur -> ur.getRol().getId()).collect(Collectors.toSet());
        usuarioRolRepository.deleteAll(actuales.stream().filter(ur -> !ids.contains(ur.getRol().getId())).toList());

        List<UsuarioRol> nuevos = roles.stream().filter(rol -> !existentes.contains(rol.getId()))
                .map(rol -> new UsuarioRol(null, usuario, rol))
                .toList();
        usuarioRolRepository.saveAllAndFlush(nuevos);

        log.info("Roles actualizados para usuario={}: total={}", usuario.getNombre(), roles.size());
    }

    private UsuarioResponseDTO cargarUsuarioConRoles(Usuario usuario) {
        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioId(usuario.getId());

        Set<RolDTO> rolesDTO = usuarioRoles.stream()
                .map(ur -> {
                    Rol rol = ur.getRol();
                    List<RolPermiso> rolPermisos = rolPermisoRepository.findByRolId(rol.getId());
                    Set<PermisoDTO> permisosDTO = rolPermisos.stream()
                            .map(rp -> permisoMapper.toDTO(rp.getPermiso()))
                            .collect(Collectors.toSet());
                    return rolMapper.toDTO(rol, permisosDTO);
                })
                .collect(Collectors.toSet());

        return usuarioMapper.toUsuarioResponse(usuario, rolesDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaUsuarioResumenDTO> buscarPersonasParaUsuario(String termino, boolean sinUsuario, Pageable pageable) {
        String texto = termino == null ? "" : termino.trim().toLowerCase(Locale.ROOT);
        String patron = "%" + texto.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
        return personaRepository.buscarParaUsuario(patron, sinUsuario, pageable);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrarConPersona(RegistroPersonaUsuarioRequestDTO request) {
        if (request.empleado() == null || request.empleado().fechaContrato() == null) {
            throw new ReglaNegocioException("El alta de empleado requiere una fecha de contrato");
        }
        PersonaRequestDTO datos = request.persona();
        String documento = datos.numeroDocumento().trim();
        if (personaRepository.existsByNumeroDocumento(documento)) {
            throw new ReglaNegocioException("El documento ya está registrado; seleccione la persona existente");
        }
        if (datos.fechaNacimiento() != null && datos.fechaNacimiento().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede estar en el futuro");
        }
        Persona persona = new Persona(null, datos.nombres().trim(), datos.apellidos().trim(), limpiar(datos.telefono()), datos.fechaNacimiento(), datos.sexo(), limpiar(datos.direccion()), documento, datos.tipoDocumento());
        try {
            personaRepository.saveAndFlush(persona);
            empleadoRepository.saveAndFlush(new Empleado(null, persona, true, request.empleado().fechaContrato(), null));
            var cuenta = request.usuario();
            return crearCuentaInterna(new UsuarioRequestDTO(cuenta.nombre(), cuenta.password(), cuenta.correo(), cuenta.estado(), persona.getId(), cuenta.rolesIds(), null));
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se pudo registrar la persona y su cuenta; compruebe los datos duplicados");
        }
    }

    private String limpiar(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
}
