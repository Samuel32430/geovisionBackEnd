package pe.edu.upeu.geovisionBackEnd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.upeu.geovisionBackEnd.service.service.EmailService;

/**
 * Simulador de servicio de correo para entorno de desarrollo.
 * Registra en consola el enlace y token de recuperación sin requerir servidor SMTP activo.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void enviarRecuperacionPassword(String destinatario, String token, String nombreUsuario) {
        log.info("================================================================================");
        log.info("[SIMULADOR CORREO] Solicitud de restablecimiento de contraseña");
        log.info("Destinatario: {}", destinatario);
        log.info("Usuario: {}", nombreUsuario);
        log.info("Token de recuperación (vigencia 15 min):");
        log.info("{}", token);
        log.info("Enlace simulado: http://localhost:4200/auth/reset-password?token={}", token);
        log.info("================================================================================");
    }
}
