package pe.edu.upeu.geovision_backend.mapper.orden_trabajo;

import pe.edu.upeu.geovision_backend.mapper.atencion.RecetaMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.atencion.LenteMedida;
import pe.edu.upeu.geovision_backend.entity.atencion.LenteTratamientoCristal;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteMedidaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteTratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;

@Component @RequiredArgsConstructor
public class OrdenTrabajoMapper {
    private final RecetaMapper recetas;
    private final LenteMedidaRepository lenteMedidas;
    private final LenteTratamientoCristalRepository tratamientos;
    private final EnvioLaboratorioRepository envios;
    private final MedidaRepository medidas;
    private final PagoRepository pagos;
    private final pe.edu.upeu.geovision_backend.util.orden_trabajo.VentaOrdenTrabajo ventasOrden;
    public OrdenTrabajoResponseDTO orden(OrdenTrabajo o) {
        var lente=o.getLente(); var paciente=lente.getReceta().getPaciente(); var persona=paciente.getPersona();
        var venta=ventasOrden.consultar(lente.getReceta().getIdReceta()).orElse(null);
        return OrdenTrabajoResponseDTO.builder().idOrdenTrabajo(o.getIdOrdenTrabajo())
            .fechaRegistro(o.getFechaRegistro()).fechaEntrega(o.getFechaEntrega())
            .fechaNotificacion(o.getFechaNotificacion()).estado(o.getEstado()).estadoDescripcion(o.getEstado().getDescripcion())
            .descripcion(o.getDescripcion()).idLente(lente.getIdLente()).idPaciente(paciente.getIdPersona())
            .nombrePaciente(persona.getNombres()+" "+persona.getApellidos()).telefonoPaciente(persona.getTelefono())
            .idVenta(venta==null?null:venta.getIdVenta())
            .saldoPendiente(venta==null?null:venta.getTotal().subtract(pagos.sumMontoPagadoByVentaId(venta.getIdVenta())))
            .lente(recetas.toLenteResponse(lente,lenteMedidas.findByLenteIdLente(lente.getIdLente()).stream().map(LenteMedida::getMedida).toList(),
                tratamientos.findByLenteIdLente(lente.getIdLente()).stream().map(LenteTratamientoCristal::getTratamientoCristal).toList()))
            .envios(envios.findByOrdenTrabajoIdOrdenTrabajoOrderByFechaEnvioDesc(o.getIdOrdenTrabajo()).stream().map(this::envio).toList()).build();
    }
    public EnvioLaboratorioResponseDTO envio(EnvioLaboratorio e) {
        return EnvioLaboratorioResponseDTO.builder().idEnvioLaboratorio(e.getIdEnvioLaboratorio())
            .idOrdenTrabajo(e.getOrdenTrabajo().getIdOrdenTrabajo()).idLaboratorio(e.getLaboratorio().getIdLaboratorio())
            .nombreLaboratorio(e.getLaboratorio().getNombre()).fechaEnvio(e.getFechaEnvio())
            .fechaRecepcionEstimada(e.getFechaRecepcionEstimada()).fechaRecepcion(e.getFechaRecepcion()).fechaRevision(e.getFechaRevision())
            .tolerancia(e.getTolerancia())
            .aprobado(e.getAprobado()==null?null:e.getAprobado()==1).observaciones(e.getObservaciones())
            .medidasControl(medidas.findByEnvioLaboratorioIdEnvioLaboratorioOrderByIdMedidaAsc(e.getIdEnvioLaboratorio()).stream().map(recetas::toMedidaResponse).toList()).build();
    }
}
