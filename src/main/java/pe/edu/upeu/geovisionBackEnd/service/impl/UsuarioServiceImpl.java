package pe.edu.upeu.geovisionBackEnd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.PersonaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PersonaUsuarioResumenDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RegistroPersonaUsuarioRequestDTO;
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
        String nombre = request.getNombre().trim();
        String correo = request.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (nombre.length() < 3 || nombre.length() > 100) {
            throw new ReglaNegocioException("El nombre de usuario debe tener entre 3 y 100 caracteres");
        }
        if (usuarioRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese nombre de usuario");
        }
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo electrónico");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ReglaNegocioException("La contraseña es obligatoria para nuevos usuarios");
        }
        if (request.getPassword().length() < 6 || request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña debe tener al menos 6 caracteres y no superar los 72 bytes");
        }

        // Serializar las altas para una misma persona, incluso cuando aún no existe su usuario.
        Persona persona = personaRepository.findByIdForUpdate(request.getIdPersona())
                .orElseThrow(() -> new RecursosNoEncontradosException("La persona seleccionada no existe"));
        if (usuarioRepository.findByPersonaIdPersona(persona.getIdPersona()).isPresent()) {
            throw new ReglaNegocioException("La persona seleccionada ya tiene una cuenta de usuario asignada");
        }
        Optional<Empleado> existente = empleadoRepository.findById(persona.getIdPersona());
        if (existente.isEmpty() || existente.get().getEstado() != EstadoGeneralEnum.ACTIVO) {
            throw new ReglaNegocioException("Solo se pueden crear cuentas para empleados activos");
        }
        if (existente.get().getUsuario() != null) {
            throw new ReglaNegocioException("El empleado ya está vinculado a una cuenta; revise su asociación");
        }
        if (request.getEmpleado() != null) {
            if (request.getEmpleado().getFechaContrato() == null) {
                throw new ReglaNegocioException("La fecha de contrato es obligatoria para registrar al empleado");
            }
            if (existente.isPresent() && existente.get().getEstado() != EstadoGeneralEnum.ACTIVO) {
                throw new ReglaNegocioException("El empleado está inactivo; no se reactiva mediante el registro de usuarios");
            }
        }

        List<Rol> roles = Collections.emptyList();
        if (request.getRolesIds() != null && !request.getRolesIds().isEmpty()) {
            roles = rolRepository.findAllById(request.getRolesIds());
            if (roles.size() != request.getRolesIds().size()) {
                throw new RecursosNoEncontradosException("Uno o más roles seleccionados no existen");
            }
        }
        EstadoGeneralEnum estado = request.getEstado() == null ? EstadoGeneralEnum.ACTIVO : EstadoGeneralEnum.fromCodigo(request.getEstado());
        Usuario usuario = Usuario.builder().nombre(nombre).correo(correo)
                .password(passwordEncoder.encode(request.getPassword())).estado(estado).persona(persona).build();
        try {
            Usuario guardado = usuarioRepository.saveAndFlush(usuario);
            Empleado empleado = existente.orElse(null);
            if (empleado != null) {
                empleado.setUsuario(guardado);
                empleadoRepository.saveAndFlush(empleado);
            }
            List<UsuarioRol> asociaciones = roles.stream().map(rol -> UsuarioRol.builder()
                    .usuario(guardado).rol(rol).build()).toList();
            usuarioRolRepository.saveAllAndFlush(asociaciones);
            log.info("Cuenta creada: idUsuario={}", guardado.getIdUsuario());
            return cargarUsuarioConRoles(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se pudo registrar la cuenta; compruebe el usuario, correo y asociaciones duplicadas");
        }
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request) {
        if (request.getEmpleado() != null) {
            throw new ReglaNegocioException("El alta laboral solo se admite al registrar la cuenta");
        }
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
        usuarioRepository.findByIdForUpdate(usuario.getIdUsuario())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado"));
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

        // Conservar asociaciones vigentes evita reinsertar pares todavía pendientes de borrar.
        List<UsuarioRol> actuales = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        Set<Long> existentes = actuales.stream().map(ur -> ur.getRol().getIdRol()).collect(Collectors.toSet());
        usuarioRolRepository.deleteAll(actuales.stream().filter(ur -> !ids.contains(ur.getRol().getIdRol())).toList());

        List<UsuarioRol> nuevos = roles.stream().filter(rol -> !existentes.contains(rol.getIdRol()))
                .map(rol -> UsuarioRol.builder()
                        .usuario(usuario)
                        .rol(rol)
                        .build())
                .toList();
        usuarioRolRepository.saveAllAndFlush(nuevos);

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
        if (request.getEmpleado() == null || request.getEmpleado().getFechaContrato() == null) {
            throw new ReglaNegocioException("El alta de empleado requiere una fecha de contrato");
        }
        PersonaRequestDTO datos = request.getPersona();
        String documento = datos.getNumeroDocumento().trim();
        if (personaRepository.existsByNumeroDocumento(documento)) {
            throw new ReglaNegocioException("El documento ya está registrado; seleccione la persona existente");
        }
        if (datos.getFechaNacimiento() != null && datos.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede estar en el futuro");
        }
        Persona persona = Persona.builder().nombres(datos.getNombres().trim())
                .apellidos(datos.getApellidos().trim()).numeroDocumento(documento)
                .tipoDocumento(datos.getTipoDocumento()).sexo(datos.getSexo())
                .fechaNacimiento(datos.getFechaNacimiento()).telefono(limpiar(datos.getTelefono()))
                .direccion(limpiar(datos.getDireccion())).build();
        try {
            personaRepository.saveAndFlush(persona);
            empleadoRepository.saveAndFlush(Empleado.builder().persona(persona).estado(EstadoGeneralEnum.ACTIVO)
                    .fechaContrato(request.getEmpleado().getFechaContrato()).build());
            var cuenta = request.getUsuario();
            return crearCuentaInterna(UsuarioRequestDTO.builder()
                    .nombre(cuenta.getNombre()).correo(cuenta.getCorreo()).password(cuenta.getPassword())
                    .estado(cuenta.getEstado()).rolesIds(cuenta.getRolesIds()).idPersona(persona.getIdPersona())
                    .build());
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se pudo registrar la persona y su cuenta; compruebe los datos duplicados");
        }
    }

    private String limpiar(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
}
