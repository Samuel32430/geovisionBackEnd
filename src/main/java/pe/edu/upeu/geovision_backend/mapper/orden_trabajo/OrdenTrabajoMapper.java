package pe.edu.upeu.geovision_backend.mapper.orden_trabajo;

import pe.edu.upeu.geovision_backend.mapper.atencion.RecetaMapper;

import java.math.BigDecimal;
import java.util.List;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;

public final class OrdenTrabajoMapper {
    private OrdenTrabajoMapper() {}

    public static OrdenTrabajoResponseDTO orden(OrdenTrabajo o, Venta venta, BigDecimal saldo,
                                                List<EnvioLaboratorioResponseDTO> envios) {
        var lente=o.getLente(); var paciente=lente.getReceta().getPaciente(); var persona=paciente.getPersona();
        return new OrdenTrabajoResponseDTO(o.getId(), o.getFechaRegistro(), o.getFechaEntrega(),
                venta==null?null:venta.getId(),
                saldo,
                o.getFechaNotificacion(), o.getEstado(), o.getEstado().getDescripcion(), o.getDescripcion(),
                lente.getId(), RecetaMapper.toLenteResponse(lente, new java.util.ArrayList<>(lente.getMedidas()),
                    new java.util.ArrayList<>(lente.getTratamientosCristal())),
                paciente.getId(), persona.getNombres()+" "+persona.getApellidos(), persona.getTelefono(),
                envios);
    }
    public static EnvioLaboratorioResponseDTO envio(EnvioLaboratorio e, List<MedidaResponseDTO> medidas) {
        return new EnvioLaboratorioResponseDTO(e.getId(), e.getOrdenTrabajo().getId(), e.getLaboratorio().getId(), e.getLaboratorio().getNombre(), e.getFechaEnvio(), e.getFechaRecepcionEstimada(), e.getFechaRecepcion(), e.getFechaRevision(), e.getTolerancia(), e.getAprobado()==null?null:e.getAprobado()==1, e.getObservaciones(), medidas);
    }
}
