package pe.edu.upeu.geovision_backend.service.orden_trabajo;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EstadoNotificacionOrdenDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.NotificacionOrdenResponseDTO;
public interface NotificacionOrdenService {
    NotificacionOrdenResponseDTO prepararWhatsApp(Long orden,String telefono,String usuario);
    NotificacionOrdenResponseDTO confirmarWhatsApp(Long orden,String telefono,String usuario);
    NotificacionOrdenResponseDTO enviarSms(Long orden,String telefono,String usuario);
    EstadoNotificacionOrdenDTO consultarEstado(Long orden);
}
