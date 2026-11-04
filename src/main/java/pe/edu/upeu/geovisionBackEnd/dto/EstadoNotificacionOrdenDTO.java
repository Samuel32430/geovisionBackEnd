package pe.edu.upeu.geovisionBackEnd.dto;
import java.time.LocalDateTime;
/** Solo refleja el campo existente ORDEN_TRABAJO.fecha_notificacion. */
public record EstadoNotificacionOrdenDTO(Long idOrdenTrabajo,LocalDateTime fechaNotificacion) {}
