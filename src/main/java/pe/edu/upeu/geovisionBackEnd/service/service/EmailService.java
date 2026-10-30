package pe.edu.upeu.geovisionBackEnd.service.service;

public interface EmailService {

    void enviarRecuperacionPassword(String destinatario, String token, String nombreUsuario);
}
