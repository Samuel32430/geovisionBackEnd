package pe.edu.upeu.geovisionBackEnd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.UsuarioRol;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
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
import pe.edu.upeu.geovisionBackEnd.service.impl.UsuarioServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private RolMapper rolMapper;

    @Mock
    private PermisoMapper permisoMapper;

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
        personaAdmin = Persona.builder().idPersona(1L).nombres("Admin").apellidos("General").build();
        personaVendedor = Persona.builder().idPersona(2L).nombres("Carlos").apellidos("Ventas").build();

        rolAdmin = Rol.builder().idRol(1L).nombre("ADMINISTRADOR").build();
        rolVendedor = Rol.builder().idRol(2L).nombre("VENDEDOR").build();

        adminUser = Usuario.builder()
                .idUsuario(1L)
                .nombre("admin")
                .correo("admin@geovision.com")
                .password("$2a$10$hash")
                .estado(EstadoGeneralEnum.ACTIVO)
                .persona(personaAdmin)
                .build();

        vendedorUser = Usuario.builder()
                .idUsuario(2L)
                .nombre("carlos.vendedor")
                .correo("carlos@geovision.com")
                .password("$2a$10$hash")
                .estado(EstadoGeneralEnum.ACTIVO)
                .persona(personaVendedor)
                .build();
    }

    @Test
    @DisplayName("CUS17: Registro de nuevo usuario exitoso (RF-86)")
    void crearUsuarioExitoso() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nombre("nuevo.usuario")
                .correo("nuevo@geovision.com")
                .password("password123")
                .estado(1)
                .idPersona(2L)
                .rolesIds(Set.of(2L))
                .build();

        when(usuarioRepository.existsByNombre("nuevo.usuario")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("nuevo@geovision.com")).thenReturn(false);
        when(personaRepository.findById(2L)).thenReturn(Optional.of(personaVendedor));
        when(usuarioRepository.findByPersonaIdPersona(2L)).thenReturn(Optional.empty());
        when(rolRepository.findAllById(Set.of(2L))).thenReturn(List.of(rolVendedor));
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(vendedorUser);
        when(empleadoRepository.findById(2L)).thenReturn(Optional.empty());
        when(usuarioRolRepository.findByUsuarioIdUsuario(2L)).thenReturn(Collections.emptyList());
        when(usuarioMapper.toUsuarioResponse(eq(vendedorUser), any())).thenReturn(new UsuarioResponseDTO());

        UsuarioResponseDTO response = usuarioService.crear(request);

        assertThat(response).isNotNull();
        verify(usuarioRepository).save(any(Usuario.class));
        verify(usuarioRolRepository).saveAll(any());
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
        when(rolRepository.findAllById(Set.of(2L))).thenReturn(List.of(rolVendedor));

        assertThatThrownBy(() -> usuarioService.asignarRoles(1L, Set.of(2L), "admin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no puede retirarse a sí mismo el rol de administrador");
    }
}
