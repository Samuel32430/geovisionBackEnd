package pe.edu.upeu.geovision_backend.service.orden_trabajo.impl;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.ControlCalidadRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PoliticaCalidadDTO;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaOjoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.OrdenTrabajoRepository;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.mapper.orden_trabajo.OrdenTrabajoMapper;
import pe.edu.upeu.geovision_backend.mapper.atencion.RecetaMapper;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ActorOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ComparadorLensometria;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ConstanciaOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.FlujoOrdenTrabajo;
import pe.edu.upeu.geovision_backend.config.gestion_mantenimiento.PoliticaCalidad;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.VentaOrdenTrabajo;
import pe.edu.upeu.geovision_backend.service.orden_trabajo.ControlCalidadService;

@Service
@RequiredArgsConstructor
@Transactional
public class ControlCalidadServiceImpl implements ControlCalidadService {
    private final OrdenTrabajoRepository ordenes;
    private final EnvioLaboratorioRepository envios;
    private final MedidaRepository medidas;
    private final MedidaOjoRepository ojos;
    private final ActorOrdenTrabajo actores;
    private final FlujoOrdenTrabajo flujo;
    private final PoliticaCalidad politica;
    private final ComparadorLensometria comparador;
    private final RecetaMapper recetas;
    private final OrdenTrabajoMapper mapper;
    private final VentaOrdenTrabajo ventasOrden;

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenTrabajoResponseDTO> pendientes(Pageable pageable) {
        return ordenes.findByEstado(EstadoOrdenTrabajoEnum.RECIBIDO_LABORATORIO, pageable).map(mapper::orden);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticaCalidadDTO politica() {
        return politica.consultar();
    }

    @Override
    public EnvioLaboratorioResponseDTO registrar(ControlCalidadRequestDTO request, String usuario) {
        flujo.validar(request);
        actores.obtener(usuario);
        var p = politica.exigir();
        var envio = flujo.bloquearEnvioActual(request.idEnvioLaboratorio());
        var ot = envio.getOrdenTrabajo();
        if (ot.getEstado() != EstadoOrdenTrabajoEnum.RECIBIDO_LABORATORIO || envio.getFechaRecepcion() == null)
            throw new ReglaNegocioException("Debe recibir el ultimo envio antes de registrar calidad");
        if (envio.getFechaRevision() != null || envio.getAprobado() != null || !medidas.findByEnvioLaboratorioIdOrderByIdAsc(envio.getId()).isEmpty())
            throw new ReglaNegocioException("Este envio ya tiene un control; para recontrolar registre un nuevo envio y recepcion");
        ventasOrden.bloquear(ot.getLente().getReceta().getId());
        var referencia = new java.util.ArrayList<>(ot.getLente().getMedidas());
        boolean aprobado = comparador.comparar(referencia, request.medidasLensometria(), p);
        for (var m : request.medidasLensometria()) {
            var derecha = ojos.save(recetas.toMedidaOjo(m.ojoDerecho()));
            var izquierda = ojos.save(recetas.toMedidaOjo(m.ojoIzquierdo()));
            // Las medidas de taller se vinculan al envio, nunca a la receta clinica ni a LENTE_MEDIDA.
            var medida = recetas.toMedida(m, null, derecha, izquierda);
            medida.setEnvioLaboratorio(envio);
            medidas.save(medida);
        }
        envio.setTolerancia(p.dioptrias());
        envio.setFechaRevision(LocalDateTime.now().withNano(0));
        envio.setAprobado(aprobado ? 1 : 0);
        String constancia="[Calidad] Usuario: "+usuario+". "+(aprobado?"Aprobado":"Rechazado")
            +". Margenes: eje="+p.ejeGrados()+", DP="+p.distanciaPupilarMm()+", prisma="+p.prisma()
            +(request.observaciones()==null?"":". "+request.observaciones());
        envio.setObservaciones(ConstanciaOrdenTrabajo.agregar(envio.getObservaciones(),constancia));
        ot.setEstado(aprobado ? EstadoOrdenTrabajoEnum.LISTO_ENTREGA : EstadoOrdenTrabajoEnum.CONTROL_CALIDAD_RECHAZADO);
        medidas.flush();
        envios.saveAndFlush(envio);
        ordenes.saveAndFlush(ot);
        return mapper.envio(envio);
    }
}
