package pe.edu.upeu.geovision_backend.mapper.orden_trabajo;

import pe.edu.upeu.geovision_backend.mapper.atencion.RecetaMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;

@Component @RequiredArgsConstructor
public class OrdenTrabajoMapper {
    private final RecetaMapper recetas;
    private final EnvioLaboratorioRepository envios;
    private final MedidaRepository medidas;
    private final PagoRepository pagos;
    private final pe.edu.upeu.geovision_backend.util.orden_trabajo.VentaOrdenTrabajo ventasOrden;
    public OrdenTrabajoResponseDTO orden(OrdenTrabajo o) {
        var lente=o.getLente(); var paciente=lente.getReceta().getPaciente(); var persona=paciente.getPersona();
        var venta=ventasOrden.consultar(lente.getReceta().getId()).orElse(null);
        return new OrdenTrabajoResponseDTO(o.getId(), o.getFechaRegistro(), o.getFechaEntrega(),
                venta==null?null:venta.getId(),
                venta==null?null:venta.getTotal().subtract(pagos.sumMontoPagadoByVentaId(venta.getId())),
                o.getFechaNotificacion(), o.getEstado(), o.getEstado().getDescripcion(), o.getDescripcion(),
                lente.getId(), recetas.toLenteResponse(lente, new java.util.ArrayList<>(lente.getMedidas()),
                    new java.util.ArrayList<>(lente.getTratamientosCristal())),
                paciente.getId(), persona.getNombres()+" "+persona.getApellidos(), persona.getTelefono(),
                envios.findByOrdenTrabajoIdOrderByFechaEnvioDesc(o.getId()).stream().map(this::envio).toList());
    }
    public EnvioLaboratorioResponseDTO envio(EnvioLaboratorio e) {
        return new EnvioLaboratorioResponseDTO(e.getId(), e.getOrdenTrabajo().getId(), e.getLaboratorio().getId(), e.getLaboratorio().getNombre(), e.getFechaEnvio(), e.getFechaRecepcionEstimada(), e.getFechaRecepcion(), e.getFechaRevision(), e.getTolerancia(), e.getAprobado()==null?null:e.getAprobado()==1, e.getObservaciones(), medidas.findByEnvioLaboratorioIdOrderByIdAsc(e.getId()).stream().map(recetas::toMedidaResponse).toList());
    }
}
