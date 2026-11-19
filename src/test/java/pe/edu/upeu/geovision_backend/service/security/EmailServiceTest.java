package pe.edu.upeu.geovision_backend.service.security;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import pe.edu.upeu.geovision_backend.service.security.impl.EmailServiceImpl;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    @DisplayName("Modo Simulador: Si no hay credenciales configuradas, no intenta enviar por SMTP y no falla")
    void enviarRecuperacion_sinCredenciales_modoSimulador() {
        EmailServiceImpl service = new EmailServiceImpl(mailSender);
        ReflectionTestUtils.setField(service, "mailEnabled", true);
        ReflectionTestUtils.setField(service, "mailUsername", "");
        ReflectionTestUtils.setField(service, "mailPassword", "");

        assertDoesNotThrow(() ->
                service.enviarRecuperacionPassword("test@gmail.com", "fake-token-123", "Juan Perez")
        );

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Modo SMTP: Con credenciales presentes, construye y envia MimeMessage exitosamente")
    void enviarRecuperacion_conCredenciales_enviaMimeMessage() {
        EmailServiceImpl service = new EmailServiceImpl(mailSender);
        ReflectionTestUtils.setField(service, "mailEnabled", true);
        ReflectionTestUtils.setField(service, "mailUsername", "notificaciones@geovision.pe");
        ReflectionTestUtils.setField(service, "mailPassword", "clave-app-16-caracteres");
        ReflectionTestUtils.setField(service, "mailFrom", "notificaciones@geovision.pe");
        ReflectionTestUtils.setField(service, "fromName", "Geo Vision");
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:4200");

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() ->
                service.enviarRecuperacionPassword("paciente@gmail.com", "jwt-token-valido-xyz", "María Gómez")
        );

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
