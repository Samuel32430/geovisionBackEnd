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
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.geovisionBackEnd.dto.CambioPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LogoutResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ResetPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.UsuarioRol;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoLogEnum;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PermisoMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.RolMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.UsuarioMapper;
import pe.edu.upeu.geovisionBackEnd.repository.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RolPermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRolRepository;
import pe.edu.upeu.geovisionBackEnd.security.JwtService;
import pe.edu.upeu.geovisionBackEnd.service.impl.AuthServiceImpl;
import pe.edu.upeu.geovisionBackEnd.service.service.EmailService;
import pe.edu.upeu.geovisionBackEnd.service.service.UsuarioLogService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private RolPermisoRepository rolPermisoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioLogService usuarioLogService;

    @Mock
    private EmailService emailService;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private RolMapper rolMapper;

    @Mock
    private PermisoMapper permisoMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario testUser;
    private Persona testPersona;
    private Rol testRol;

    @BeforeEach
    void setUp() {
        testPersona = Persona.builder()
                .idPersona(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .build();

        testUser = Usuario.builder()
                .idUsuario(1L)
                .nombre("juan.perez")
                .correo("juan@geovision.com")
                .password("$2a$10$encodedHash")
                .estado(EstadoGeneralEnum.ACTIVO)
                .persona(testPersona)
                .build();

        testRol = Rol.builder()
                .idRol(1L)
                .nombre("VENDEDOR")
                .build();
    }

    @Test
    @DisplayName("CUS15: Login exitoso retorna token y roles")
    void loginExitoso() {
        LoginRequestDTO request = new LoginRequestDTO("juan.perez", "password123");

        UserDetails principal = User.withUsername("juan.perez")
                .password("hash")
                .authorities(Collections.emptyList())
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(usuarioRepository.findByNombreOrCorreo("juan.perez", "juan.perez")).thenReturn(Optional.of(testUser));
        when(jwtService.generarToken(principal)).thenReturn("fake.jwt.token");

        UsuarioRol usuarioRol = UsuarioRol.builder().usuario(testUser).rol(testRol).build();
        when(usuarioRolRepository.findByUsuarioIdUsuario(1L)).thenReturn(List.of(usuarioRol));
        when(rolPermisoRepository.findNombresPermisosByRolId(1L)).thenReturn(List.of("VENTAS_LEER", "VENTAS_REGISTRAR"));

        LoginResponseDTO response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake.jwt.token");
        assertThat(response.getUsername()).isEqualTo("juan.perez");
        assertThat(response.getRoles()).containsExactly("VENDEDOR");
        assertThat(response.getPermisos()).containsExactlyInAnyOrder("VENTAS_LEER", "VENTAS_REGISTRAR");
        verify(usuarioLogService).registrar(testUser, TipoLogEnum.LOGIN);
    }

    @Test
    @DisplayName("CUS15: Login fallido por credenciales inválidas registra intento fallido")
    void loginCredencialesInvalidas() {
        LoginRequestDTO request = new LoginRequestDTO("juan.perez", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(usuarioRepository.findByNombreOrCorreo("juan.perez", "juan.perez")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(usuarioLogService).registrar(testUser, TipoLogEnum.INTENTO_FALLIDO);
    }

    @Test
    @DisplayName("CUS16: Logout exitoso registra auditoría de LOGOUT")
    void logoutExitoso() {
        when(usuarioRepository.findByNombreOrCorreo("juan.perez", "juan.perez")).thenReturn(Optional.of(testUser));

        LogoutResponseDTO response = authService.logout("juan.perez");

        assertThat(response.getExitoso()).isTrue();
        assertThat(response.getMensaje()).contains("exitosamente");
        verify(usuarioLogService).registrar(testUser, TipoLogEnum.LOGOUT);
    }

    @Test
    @DisplayName("Cambio de contraseña exitoso")
    void cambiarPasswordExitoso() {
        CambioPasswordRequestDTO request = new CambioPasswordRequestDTO("oldPass", "newPass123", "newPass123");

        when(usuarioRepository.findByNombreOrCorreo("juan.perez", "juan.perez")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPass123", testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPass123")).thenReturn("$2a$10$newHash");

        authService.cambiarPassword("juan.perez", request);

        verify(usuarioRepository).save(testUser);
        verify(usuarioLogService).registrar(testUser, TipoLogEnum.CAMBIO_PASSWORD);
    }

    @Test
    @DisplayName("CUS19: Flujo de recuperación y restablecimiento de contraseña")
    void recuperacionYRestablecimientoPassword() {
        // 1. Solicitud de recuperación
        RecuperarPasswordRequestDTO solRequest = new RecuperarPasswordRequestDTO("juan@geovision.com");
        when(usuarioRepository.findByCorreo("juan@geovision.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generarTokenRecuperacion("juan@geovision.com", 15)).thenReturn("recovery.jwt.token");

        authService.solicitarRecuperacionPassword(solRequest);
        verify(emailService).enviarRecuperacionPassword("juan@geovision.com", "recovery.jwt.token", "juan.perez");

        // 2. Validación de token
        when(jwtService.validarTokenRecuperacion("recovery.jwt.token")).thenReturn(true);
        assertThat(authService.validarTokenRecuperacion("recovery.jwt.token")).isTrue();

        // 3. Reset de contraseña
        ResetPasswordRequestDTO resetRequest = new ResetPasswordRequestDTO("recovery.jwt.token", "brandNewPass1", "brandNewPass1");
        when(jwtService.extraerEmailRecuperacion("recovery.jwt.token")).thenReturn("juan@geovision.com");
        when(passwordEncoder.encode("brandNewPass1")).thenReturn("$2a$10$brandNewHash");

        authService.restablecerPassword(resetRequest);

        verify(usuarioRepository).save(testUser);
        verify(usuarioLogService).registrar(testUser, TipoLogEnum.CAMBIO_PASSWORD);
    }
}
