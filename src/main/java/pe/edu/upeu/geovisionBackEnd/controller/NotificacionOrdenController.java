package pe.edu.upeu.geovisionBackEnd.controller;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.service.service.NotificacionOrdenService;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/ordenes-trabajo/{idOrden}/notificaciones")
public class NotificacionOrdenController {
    private final NotificacionOrdenService servicio;
    @GetMapping @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public EstadoNotificacionOrdenDTO estado(@PathVariable @Positive Long idOrden) {
        return servicio.consultarEstado(idOrden);
    }
    @PostMapping("/whatsapp") @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public NotificacionOrdenResponseDTO whatsapp(@PathVariable @Positive Long idOrden,
            @Valid @RequestBody NotificacionOrdenRequestDTO request,Authentication auth) {
        return servicio.prepararWhatsApp(idOrden,request.telefono(),auth.getName());
    }
    @PostMapping("/whatsapp/confirmar") @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public NotificacionOrdenResponseDTO confirmar(@PathVariable @Positive Long idOrden,
            @Valid @RequestBody NotificacionOrdenRequestDTO request,Authentication auth) {
        return servicio.confirmarWhatsApp(idOrden,request.telefono(),auth.getName());
    }
    @PostMapping("/sms") @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public NotificacionOrdenResponseDTO sms(@PathVariable @Positive Long idOrden,
            @Valid @RequestBody NotificacionOrdenRequestDTO request,Authentication auth) {
        return servicio.enviarSms(idOrden,request.telefono(),auth.getName());
    }
}
