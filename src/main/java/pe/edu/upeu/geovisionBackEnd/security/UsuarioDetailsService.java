package pe.edu.upeu.geovisionBackEnd.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.UsuarioRol;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.repository.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RolPermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRolRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio de carga de detalles del usuario para Spring Security.
 * Soporta autenticación tanto por nombre de usuario como por correo electrónico.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final EmpleadoRepository empleadoRepository;

    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository,
            RolPermisoRepository rolPermisoRepository,
            EmpleadoRepository empleadoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String termino = usernameOrEmail.trim();

        Usuario usuario = usuarioRepository.findByNombreOrCorreo(termino, termino)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con: " + termino));

        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        boolean empleadoActivo = empleadoOpt.map(e -> e.getEstado() == EstadoGeneralEnum.ACTIVO).orElse(true);
        boolean cuentaHabilitada = (usuario.getEstado() == EstadoGeneralEnum.ACTIVO) && empleadoActivo;

        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UsuarioRol ur : usuarioRoles) {
            if (ur.getRol() != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre()));
                List<String> permisos = rolPermisoRepository.findNombresPermisosByRolId(ur.getRol().getIdRol());
                for (String permiso : permisos) {
                    authorities.add(new SimpleGrantedAuthority(permiso));
                }
            }
        }

        return User.withUsername(usuario.getNombre())
                .password(usuario.getPassword())
                .authorities(new ArrayList<>(authorities))
                .disabled(!cuentaHabilitada)
                .build();
    }
}
