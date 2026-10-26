package pe.edu.upeu.geovisionBackEnd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Auditoria;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.UsuarioRol;
import pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos.UsuarioMapper;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.AuditoriaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.RolPermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.UsuarioRolRepository;
import pe.edu.upeu.geovisionBackEnd.security.JwtService;
import pe.edu.upeu.geovisionBackEnd.service.impl.controlAccesos.AuthServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AuthService (Login CU-SEG15)")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private RolPermisoRepository rolPermisoRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioPrueba;
    private Empleado empleadoPrueba;
    private Persona personaPrueba;
    private Rol rolVendedor;

    @BeforeEach
    void setUp() {
        usuarioPrueba = Usuario.builder()
                .idUsuario(1L)
                .nombre("samuel")
                .correo("samuel@geovision.pe")
                .password("$2a$10$hashedpassword")
                .estado(1)
                .build();

        personaPrueba = new Persona();
        personaPrueba.setIdPersona(10L);
        personaPrueba.setNombres("Samuel");
        personaPrueba.setApellidos("Vilca");

        empleadoPrueba = new Empleado();
        empleadoPrueba.setIdPersona(10L);
        empleadoPrueba.setPersona(personaPrueba);
        empleadoPrueba.setEstado(1);
        empleadoPrueba.setUsuario(usuarioPrueba);

        rolVendedor = Rol.builder()
                .idRol(2L)
                .nombre("VENDEDOR")
                .build();
    }

    @Test
    @DisplayName("Debe autenticar exitosamente por nombre de usuario y retornar LoginResponseDTO con token")
    void login_ConNombreUsuarioExitoso() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("samuel")
                .password("Password123*")
                .build();

        UserDetails userDetails = User.withUsername("samuel")
                .password("encoded")
                .authorities(Collections.emptyList())
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(usuarioRepository.findByNombreOrCorreo("samuel", "samuel"))
                .thenReturn(Optional.of(usuarioPrueba));

        when(jwtService.generarToken(userDetails)).thenReturn("jwt.token.mock");

        UsuarioRol usuarioRol = UsuarioRol.builder()
                .idUsuarioRol(1L)
                .usuario(usuarioPrueba)
                .rol(rolVendedor)
                .build();
        when(usuarioRolRepository.findByUsuarioIdUsuario(1L)).thenReturn(List.of(usuarioRol));
        when(rolPermisoRepository.findNombresPermisosByRolId(2L)).thenReturn(List.of("VENTAS_REGISTRAR", "VENTAS_LEER"));
        when(empleadoRepository.findByUsuarioIdUsuario(1L)).thenReturn(Optional.of(empleadoPrueba));

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt.token.mock", response.getToken());
        assertEquals("Bearer", response.getTipoToken());
        assertEquals(1L, response.getIdUsuario());
        assertEquals("samuel", response.getNombre());
        assertEquals("samuel@geovision.pe", response.getCorreo());
        assertTrue(response.getRoles().contains("VENDEDOR"));
        assertTrue(response.getPermisos().contains("VENTAS_REGISTRAR"));
        assertEquals(10L, response.getIdEmpleado());
        assertEquals("Samuel Vilca", response.getNombreCompleto());

        verify(auditoriaRepository, times(1)).save(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe autenticar exitosamente cuando se ingresa el correo electrónico")
    void login_ConCorreoElectronicoExitoso() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("samuel@geovision.pe")
                .password("Password123*")
                .build();

        UserDetails userDetails = User.withUsername("samuel")
                .password("encoded")
                .authorities(Collections.emptyList())
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(usuarioRepository.findByNombreOrCorreo("samuel", "samuel"))
                .thenReturn(Optional.of(usuarioPrueba));
        when(jwtService.generarToken(userDetails)).thenReturn("jwt.token.mock");
        when(usuarioRolRepository.findByUsuarioIdUsuario(1L)).thenReturn(Collections.emptyList());
        when(empleadoRepository.findByUsuarioIdUsuario(1L)).thenReturn(Optional.of(empleadoPrueba));

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("samuel", response.getNombre());
        assertEquals("samuel@geovision.pe", response.getCorreo());
    }

    @Test
    @DisplayName("Debe propagar BadCredentialsException si la autenticación falla")
    void login_CredencialesInvalidas_DebeLanzarExcepcion() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("samuel")
                .password("ClaveErronea")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(auditoriaRepository, never()).save(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe consultar y retornar el perfil del usuario autenticado")
    void usuarioActual_Exitoso() {
        when(usuarioRepository.findByNombreOrCorreo("samuel", "samuel"))
                .thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRolRepository.findNombresRolesByUsuarioId(1L))
                .thenReturn(List.of("VENDEDOR"));
        when(empleadoRepository.findByUsuarioIdUsuario(1L))
                .thenReturn(Optional.of(empleadoPrueba));

        UsuarioResponseDTO expectedDTO = UsuarioResponseDTO.builder()
                .idUsuario(1L)
                .nombre("samuel")
                .correo("samuel@geovision.pe")
                .estado(1)
                .idPersona(10L)
                .nombreCompletoEmpleado("Samuel Vilca")
                .roles(List.of("VENDEDOR"))
                .build();

        when(usuarioMapper.toResponseDTO(usuarioPrueba, List.of("VENDEDOR"), 10L, "Samuel Vilca"))
                .thenReturn(expectedDTO);

        UsuarioResponseDTO result = authService.usuarioActual("samuel");

        assertNotNull(result);
        assertEquals("samuel", result.getNombre());
        assertEquals("Samuel Vilca", result.getNombreCompletoEmpleado());
    }
}
