package pe.edu.upeu.geovisionBackEnd.service.service;
import pe.edu.upeu.geovisionBackEnd.dto.*;
public interface NotificacionOrdenService {
    NotificacionOrdenResponseDTO prepararWhatsApp(Long orden,String telefono,String usuario);
    NotificacionOrdenResponseDTO confirmarWhatsApp(Long orden,String telefono,String usuario);
    NotificacionOrdenResponseDTO enviarSms(Long orden,String telefono,String usuario);
    EstadoNotificacionOrdenDTO consultarEstado(Long orden);
}
