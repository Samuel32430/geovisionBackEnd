package pe.edu.upeu.geovision_backend.service.security.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pe.edu.upeu.geovision_backend.service.security.EmailService;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.from-name:Geo Vision - Sistema Óptico}")
    private String fromName;

    @Value("${app.mail.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public EmailServiceImpl(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarRecuperacionPassword(String destinatario, String token, String nombreUsuario) {
        String base = (frontendUrl != null && !frontendUrl.isBlank()) ? frontendUrl.replaceAll("/$", "") : "http://localhost:4200";
        String enlace = String.format("%s/reset-password?token=%s", base, token);

        boolean tieneCredenciales = mailUsername != null && !mailUsername.isBlank()
                && mailPassword != null && !mailPassword.isBlank();

        if (!mailEnabled || mailSender == null || !tieneCredenciales) {
            log.info("================================================================================");
            log.info("[SIMULADOR CORREO] Solicitud de restablecimiento de contraseña");
            log.info("Razón modo simulador: {}", !mailEnabled ? "app.mail.enabled=false" : (mailSender == null ? "JavaMailSender no disponible" : "Variables de entorno MAIL_USERNAME / MAIL_PASSWORD no configuradas"));
            log.info("Destinatario: {}", destinatario);
            log.info("Usuario: {}", nombreUsuario);
            log.info("Token de recuperación (vigencia 15 min): {}", token);
            log.info("Enlace directo: {}", enlace);
            log.info("================================================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            String remitente = (mailFrom != null && !mailFrom.isBlank()) ? mailFrom.trim() : mailUsername.trim();
            helper.setFrom(remitente, fromName);
            helper.setTo(destinatario.trim());
            helper.setSubject("Recuperación de Contraseña - Geo Vision");

            String html = construirPlantillaHtml(nombreUsuario != null ? nombreUsuario : "Usuario", enlace);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Correo de recuperación enviado exitosamente a {} vía Gmail SMTP", destinatario);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error al construir el correo de recuperación para {}: {}", destinatario, e.getMessage());
            log.info("[FALLBACK] Enlace de recuperación: {}", enlace);
        } catch (Exception e) {
            log.error("Error al enviar correo por SMTP a {}: {}", destinatario, e.getMessage());
            log.info("[FALLBACK] Enlace de recuperación disponible en consola: {}", enlace);
        }
    }

    private String construirPlantillaHtml(String nombreUsuario, String enlace) {
        String template = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Recuperación de Contraseña</title>
              <style>
                body { margin: 0; padding: 0; font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, Helvetica, Arial, sans-serif; background-color: #F1F5F9; color: #1E293B; }
                .email-container { max-width: 580px; margin: 30px auto; background-color: #FFFFFF; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06); border: 1px solid #E2E8F0; }
                .email-header { background: linear-gradient(135deg, #0F766E 0%, #115E59 100%); padding: 32px 24px; text-align: center; color: #FFFFFF; }
                .brand-title { margin: 0; font-size: 26px; font-weight: 800; letter-spacing: -0.5px; }
                .brand-subtitle { margin: 4px 0 0 0; font-size: 11px; letter-spacing: 1.5px; text-transform: uppercase; opacity: 0.85; font-weight: 600; }
                .email-body { padding: 36px 32px; }
                .greeting { font-size: 18px; font-weight: 700; color: #0F172A; margin-top: 0; margin-bottom: 16px; }
                .text { font-size: 15px; line-height: 1.6; color: #475569; margin-bottom: 24px; }
                .btn-container { text-align: center; margin: 32px 0; }
                .btn { display: inline-block; background-color: #0F766E; color: #FFFFFF !important; font-size: 15px; font-weight: 700; text-decoration: none; padding: 14px 32px; border-radius: 10px; box-shadow: 0 4px 12px rgba(15, 118, 110, 0.3); }
                .alert-box { background-color: #FFFBEB; border-left: 4px solid #F59E0B; padding: 14px 18px; border-radius: 8px; margin: 28px 0; font-size: 13px; line-height: 1.5; color: #92400E; }
                .link-backup { font-size: 12px; color: #64748B; word-break: break-all; margin-top: 24px; line-height: 1.5; }
                .link-backup a { color: #0F766E; text-decoration: underline; }
                .email-footer { background-color: #F8FAFC; padding: 24px 32px; text-align: center; border-top: 1px solid #E2E8F0; font-size: 12px; color: #94A3B8; line-height: 1.5; }
              </style>
            </head>
            <body>
              <div class="email-container">
                <div class="email-header">
                  <h1 class="brand-title">Geo Vision</h1>
                  <p class="brand-subtitle">Sistema Óptico &amp; Ventas</p>
                </div>
                <div class="email-body">
                  <h2 class="greeting">Hola, {{nombreUsuario}}</h2>
                  <p class="text">
                    Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en el sistema <strong>Geo Vision</strong>.
                  </p>
                  <p class="text">
                    Para crear una nueva contraseña y recuperar tu acceso, haz clic en el siguiente botón:
                  </p>
                  <div class="btn-container">
                    <a href="{{enlace}}" target="_blank" class="btn">Restablecer mi Contraseña</a>
                  </div>
                  <div class="alert-box">
                    <strong>⚠️ Información de seguridad:</strong> Este enlace es personal, de un solo uso y expirará en <strong>15 minutos</strong>. Si tú no solicitaste este cambio, ignora este mensaje; tu contraseña actual permanecerá segura.
                  </div>
                  <p class="link-backup">
                    Si el botón no funciona, copia y pega el siguiente enlace directamente en tu navegador:<br>
                    <a href="{{enlace}}" target="_blank">{{enlace}}</a>
                  </p>
                </div>
                <div class="email-footer">
                  <p style="margin: 0 0 6px 0;">&copy; 2026 Geo Vision. Todos los derechos reservados.</p>
                  <p style="margin: 0;">Este es un mensaje automático del sistema, por favor no respondas a este correo.</p>
                </div>
              </div>
            </body>
            </html>
            """;
        return template
                .replace("{{nombreUsuario}}", nombreUsuario)
                .replace("{{enlace}}", enlace);
    }
}
