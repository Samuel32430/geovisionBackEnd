package pe.edu.upeu.geovision_backend.dto.orden_trabajo;
import java.time.LocalDateTime;

public record NotificacionOrdenResponseDTO(Long id,String canal,String estado,String telefono,
        String mensaje,LocalDateTime fechaNotificacion,String referenciaProveedor,String detalle,String enlaceWhatsApp
) {}
