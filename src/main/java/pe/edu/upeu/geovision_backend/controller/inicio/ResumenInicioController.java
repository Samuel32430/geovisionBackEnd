package pe.edu.upeu.geovision_backend.controller.inicio;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.inicio.ResumenInicioDTO;
import pe.edu.upeu.geovision_backend.service.inicio.ResumenInicioService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inicio")
public class ResumenInicioController {
    private final ResumenInicioService resumen;

    @GetMapping("/ventas")
    @PreAuthorize("hasAuthority('VENTAS_LEER')")
    public ResumenInicioDTO.Ventas ventas() {
        return resumen.ventasDelDia();
    }

    @GetMapping("/ordenes")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public ResumenInicioDTO.Ordenes ordenes() {
        return resumen.ordenesActivas();
    }

    @GetMapping("/atenciones")
    @PreAuthorize("hasAuthority('PACIENTES_LEER')")
    public ResumenInicioDTO.Atenciones atenciones() {
        return resumen.atencionesDelDia();
    }
}
