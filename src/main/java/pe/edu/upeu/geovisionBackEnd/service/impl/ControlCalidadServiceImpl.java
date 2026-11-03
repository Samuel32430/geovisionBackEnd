package pe.edu.upeu.geovisionBackEnd.service.impl;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.enums.*;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.repository.*;
import pe.edu.upeu.geovisionBackEnd.entity.*;
import pe.edu.upeu.geovisionBackEnd.mapper.*;
import pe.edu.upeu.geovisionBackEnd.util.*;
import pe.edu.upeu.geovisionBackEnd.service.service.ControlCalidadService;

@Service
@RequiredArgsConstructor
@Transactional
public class ControlCalidadServiceImpl implements ControlCalidadService {
    private final OrdenTrabajoRepository ordenes;
    private final EnvioLaboratorioRepository envios;
    private final LenteMedidaRepository lenteMedidas;
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
        var envio = flujo.bloquearEnvioActual(request.getIdEnvioLaboratorio());
        var ot = envio.getOrdenTrabajo();
        if (ot.getEstado() != EstadoOrdenTrabajoEnum.RECIBIDO_LABORATORIO || envio.getFechaRecepcion() == null)
            throw new ReglaNegocioException("Debe recibir el ultimo envio antes de registrar calidad");
        if (envio.getFechaRevision() != null || envio.getAprobado() != null || !medidas.findByEnvioLaboratorioIdEnvioLaboratorioOrderByIdMedidaAsc(envio.getIdEnvioLaboratorio()).isEmpty())
            throw new ReglaNegocioException("Este envio ya tiene un control; para recontrolar registre un nuevo envio y recepcion");
        ventasOrden.bloquear(ot.getLente().getReceta().getIdReceta());
        var referencia = lenteMedidas.findByLenteIdLente(ot.getLente().getIdLente()).stream().map(LenteMedida::getMedida).toList();
        boolean aprobado = comparador.comparar(referencia, request.getMedidasLensometria(), p);
        for (var m : request.getMedidasLensometria()) {
            var derecha = ojos.save(recetas.toMedidaOjo(m.getOjoDerecho()));
            var izquierda = ojos.save(recetas.toMedidaOjo(m.getOjoIzquierdo()));
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
            +(request.getObservaciones()==null?"":". "+request.getObservaciones());
        envio.setObservaciones(ConstanciaOrdenTrabajo.agregar(envio.getObservaciones(),constancia));
        ot.setEstado(aprobado ? EstadoOrdenTrabajoEnum.LISTO_ENTREGA : EstadoOrdenTrabajoEnum.CONTROL_CALIDAD_RECHAZADO);
        medidas.flush();
        envios.saveAndFlush(envio);
        ordenes.saveAndFlush(ot);
        return mapper.envio(envio);
    }
}
