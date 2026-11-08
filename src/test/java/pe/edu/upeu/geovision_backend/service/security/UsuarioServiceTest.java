package pe.edu.upeu.geovision_backend.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Rol;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.security.PermisoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRolRepository;
import pe.edu.upeu.geovision_backend.service.security.impl.UsuarioServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private RolPermisoRepository rolPermisoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario adminUser;
    private Usuario vendedorUser;
    private Persona personaAdmin;
    private Persona personaVendedor;
    private Rol rolAdmin;
    private Rol rolVendedor;

    @BeforeEach
    void setUp() {
        personaAdmin = new Persona(1L, "Admin", "General", null, null, null, null, null, null);
        personaVendedor = new Persona(2L, "Carlos", "Ventas", null, null, null, null, null, null);

        rolAdmin = new Rol(1L, "ADMINISTRADOR");
        rolVendedor = new Rol(2L, "VENDEDOR");

        adminUser = new Usuario(1L, "admin", "$2a$10$hash", "admin@geovision.com", true, personaAdmin);

        vendedorUser = new Usuario(2L, "carlos.vendedor", "$2a$10$hash", "carlos@geovision.com", true, personaVendedor);
    }

    @Test
    @DisplayName("CUS17: Registro de nuevo usuario exitoso (RF-86)")
    void crearUsuarioExitoso() {
        UsuarioRequestDTO request = new UsuarioRequestDTO("nuevo.usuario", "password123", "nuevo@geovision.com", 1, 2L, Set.of(2L), null);

        when(usuarioRepository.existsByNombre("nuevo.usuario")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("nuevo@geovision.com")).thenReturn(false);
        when(personaRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(personaVendedor));
        when(usuarioRepository.findByPersonaId(2L)).thenReturn(Optional.empty());
        when(rolRepository.findAllById(Set.of(2L))).thenReturn(List.of(rolVendedor));
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(vendedorUser);
        Empleado empleadoVendedor = new Empleado(2L, personaVendedor, true, null, null);
        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleadoVendedor));
        when(usuarioRolRepository.findByUsuarioId(2L)).thenReturn(Collections.emptyList());

        UsuarioResponseDTO response = usuarioService.crear(request);

        assertThat(response).isNotNull();
        verify(usuarioRepository).saveAndFlush(any(Usuario.class));
        verify(usuarioRolRepository).saveAllAndFlush(any());
    }

    @Test
    @DisplayName("CUS17: Desactivar usuario impide que un admin se desactive a sí mismo (RF-88)")
    void desactivarAdminASiMismoRechazado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> usuarioService.cambiarEstado(1L, 0, "admin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no puede desactivar su propia cuenta");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("CUS18: Asignar roles impide que un admin se quite el rol ADMINISTRADOR (RF-89)")
    void asignarRolesAdminSeQuitaAdminRechazado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(usuarioRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(adminUser));
        when(rolRepository.findAllById(Set.of(2L))).thenReturn(List.of(rolVendedor));

        assertThatThrownBy(() -> usuarioService.asignarRoles(1L, Set.of(2L), "admin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no puede retirarse a sí mismo el rol de administrador");
    }
}
