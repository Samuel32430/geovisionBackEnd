package pe.edu.upeu.geovision_backend.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.security.UsuarioRol;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRolRepository;

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
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final EmpleadoRepository empleadoRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String termino = usernameOrEmail.trim();

        Usuario usuario = usuarioRepository.findByNombreOrCorreo(termino, termino)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con: " + termino));

        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioId(usuario.getId());
        boolean empleadoActivo = empleadoOpt.map(e -> Boolean.TRUE.equals(e.getActivo())).orElse(true);
        boolean cuentaHabilitada = (Boolean.TRUE.equals(usuario.getActivo())) && empleadoActivo;

        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuarioId(usuario.getId());
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UsuarioRol ur : usuarioRoles) {
            if (ur.getRol() != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre()));
                List<String> permisos = rolPermisoRepository.findNombresPermisosByRolId(ur.getRol().getId());
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
