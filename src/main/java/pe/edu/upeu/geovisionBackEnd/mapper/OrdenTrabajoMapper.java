package pe.edu.upeu.geovisionBackEnd.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.entity.*;
import pe.edu.upeu.geovisionBackEnd.repository.*;

@Component @RequiredArgsConstructor
public class OrdenTrabajoMapper {
    private final RecetaMapper recetas;
    private final LenteMedidaRepository lenteMedidas;
    private final LenteTratamientoCristalRepository tratamientos;
    private final EnvioLaboratorioRepository envios;
    private final MedidaRepository medidas;
    private final PagoRepository pagos;
    private final pe.edu.upeu.geovisionBackEnd.util.VentaOrdenTrabajo ventasOrden;
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
