package pe.edu.upeu.geovisionBackEnd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LogoutResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Auditoria;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;
import pe.edu.upeu.geovisionBackEnd.enums.controlAccesos.AccionAuditoriaEnum;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.AuditoriaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.service.impl.controlAccesos.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AuthService (Logout CU-SEG16)")
class AuthServiceLogoutTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = Usuario.builder()
                .idUsuario(1L)
                .nombre("samuel")
                .correo("samuel@geovision.pe")
                .estado(1)
                .build();
    }

    @Test
    @DisplayName("Debe registrar auditoría de CERRAR_SESION y retornar confirmación exitosa")
    void logout_Exitoso() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("samuel");
        when(usuarioRepository.findByNombreOrCorreo("samuel", "samuel"))
                .thenReturn(Optional.of(usuarioPrueba));

        LogoutResponseDTO response = authService.logout(auth);

        assertNotNull(response);
        assertEquals("Sesión cerrada exitosamente", response.getMensaje());
        assertNotNull(response.getFecha());

        ArgumentCaptor<Auditoria> auditoriaCaptor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository, times(1)).save(auditoriaCaptor.capture());

        Auditoria auditoriaGuardada = auditoriaCaptor.getValue();
        assertEquals("USUARIO", auditoriaGuardada.getEntidad());
        assertEquals(1L, auditoriaGuardada.getIdEntidad());
        assertEquals(AccionAuditoriaEnum.CERRAR_SESION.name(), auditoriaGuardada.getAccion());
        assertEquals(usuarioPrueba, auditoriaGuardada.getUsuario());
    }

    @Test
    @DisplayName("Debe manejar logout sin autenticación sin lanzar excepción")
    void logout_SinAutenticacion_DebeRetornarConfirmacion() {
        LogoutResponseDTO response = authService.logout(null);

        assertNotNull(response);
        assertEquals("Sesión cerrada exitosamente", response.getMensaje());
        verify(auditoriaRepository, never()).save(any(Auditoria.class));
    }
}
