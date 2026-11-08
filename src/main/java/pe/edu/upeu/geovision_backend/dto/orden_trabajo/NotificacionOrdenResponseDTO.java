package pe.edu.upeu.geovision_backend.dto.orden_trabajo;
import java.time.LocalDateTime;
/** Resultado de esta operacion; no representa un registro persistido ni un historial. */
public record NotificacionOrdenResponseDTO(Long id,String canal,String estado,String telefono,
        String mensaje,LocalDateTime fechaNotificacion,String referenciaProveedor,String detalle,String enlaceWhatsApp) {}
