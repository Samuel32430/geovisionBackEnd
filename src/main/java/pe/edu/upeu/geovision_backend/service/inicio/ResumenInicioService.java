package pe.edu.upeu.geovision_backend.service.inicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.inicio.ResumenInicioDTO;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.OrdenTrabajoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumenInicioService {
    private static final ZoneId ZONA = ZoneId.of("America/Lima");
    private static final List<EstadoOrdenTrabajoEnum> ESTADOS_FINALES =
            List.of(EstadoOrdenTrabajoEnum.ENTREGADO, EstadoOrdenTrabajoEnum.ANULADO);

    private final VentaRepository ventas;
    private final OrdenTrabajoRepository ordenes;
    private final RecetaRepository recetas;

    public ResumenInicioDTO.Ventas ventasDelDia() {
        var hoy = LocalDate.now(ZONA);
        var totales = ventas.resumirVentasDelDia(hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());
        return new ResumenInicioDTO.Ventas(
                totales.getTotal() == null ? BigDecimal.ZERO : totales.getTotal(), totales.getCantidad());
    }

    public ResumenInicioDTO.Ordenes ordenesActivas() {
        long enProceso = 0;
        long listas = 0;
        for (var grupo : ordenes.contarActivasPorEstado(ESTADOS_FINALES)) {
            if (grupo.getEstado() == EstadoOrdenTrabajoEnum.LISTO_ENTREGA) {
                listas = grupo.getCantidad();
            } else {
                enProceso += grupo.getCantidad();
            }
        }
        return new ResumenInicioDTO.Ordenes(enProceso, listas);
    }

    public ResumenInicioDTO.Atenciones atencionesDelDia() {
        var hoy = LocalDate.now(ZONA);
        var desde = hoy.atStartOfDay();
        var hasta = hoy.plusDays(1).atStartOfDay();
        var pagina = recetas.findByEsExternoAndFechaRegistroGreaterThanEqualAndFechaRegistroLessThan(
                0, desde, hasta, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "fechaRegistro", "id")));
        var recientes = pagina.map(receta -> {
            var paciente = receta.getPaciente();
            var persona = paciente.getPersona();
            return new ResumenInicioDTO.Atencion(receta.getId(), paciente.getId(),
                    persona.getNombres() + " " + persona.getApellidos(), receta.getFechaRegistro());
        });
        return new ResumenInicioDTO.Atenciones(recetas.contarPacientesAtendidos(desde, hasta),
                recetas.contarPacientesPorPrimeraVez(desde, hasta), pagina.getTotalElements(), recientes.getContent());
    }
}
