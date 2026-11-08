package pe.edu.upeu.geovision_backend.service.security;

public interface EmailService {

    void enviarRecuperacionPassword(String destinatario, String token, String nombreUsuario);
}
