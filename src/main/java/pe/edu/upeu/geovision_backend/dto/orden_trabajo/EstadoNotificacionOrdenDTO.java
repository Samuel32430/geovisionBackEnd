package pe.edu.upeu.geovision_backend.dto.orden_trabajo;
import java.time.LocalDateTime;
/** Solo refleja el campo existente ORDEN_TRABAJO.fecha_notificacion. */
public record EstadoNotificacionOrdenDTO(Long id,LocalDateTime fechaNotificacion) {}
