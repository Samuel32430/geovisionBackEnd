package pe.edu.upeu.geovision_backend.service.orden_trabajo.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.mapper.atencion.RecetaMapper;
import pe.edu.upeu.geovision_backend.mapper.orden_trabajo.OrdenTrabajoMapper;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.VentaOrdenTrabajo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdenTrabajoDetalleService {
    private final EnvioLaboratorioRepository envios;
    private final MedidaRepository medidas;
    private final PagoRepository pagos;
    private final VentaOrdenTrabajo ventasOrden;

    public OrdenTrabajoResponseDTO orden(OrdenTrabajo orden) {
        var venta = ventasOrden.consultar(orden.getLente().getReceta().getId()).orElse(null);
        var saldo = venta == null ? null : venta.getTotal().subtract(pagos.sumMontoPagadoByVentaId(venta.getId()));
        var detalleEnvios = envios.findByOrdenTrabajoIdOrderByFechaEnvioDesc(orden.getId())
                .stream().map(this::envio).toList();
        return OrdenTrabajoMapper.orden(orden, venta, saldo, detalleEnvios);
    }

    public EnvioLaboratorioResponseDTO envio(EnvioLaboratorio envio) {
        var detalleMedidas = medidas.findByEnvioLaboratorioIdOrderByIdAsc(envio.getId())
                .stream().map(RecetaMapper::toMedidaResponse).toList();
        return OrdenTrabajoMapper.envio(envio, detalleMedidas);
    }
}
