package pe.edu.upeu.geovision_backend.service.orden_trabajo.impl;

import java.util.Locale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.security.access.AccessDeniedException;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EntregaPedidoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.LaboratorioDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PendienteOrdenTrabajoDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.RecepcionLaboratorioRequestDTO;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.LaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.OrdenTrabajoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;

import pe.edu.upeu.geovision_backend.service.orden_trabajo.OrdenTrabajoService;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ActorOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ComparadorLensometria;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ConstanciaOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.FlujoOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.VentaOrdenTrabajo;

@Service @RequiredArgsConstructor
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    // >>>> DEPENDENCIES
    private final OrdenTrabajoDetalleService detalles;
    private final OrdenTrabajoRepository ordenes;
    private final LenteRepository lentes;
    private final RecetaRepository recetas;
    private final VentaRepository ventas;
    private final PagoRepository pagos;
    private final EnvioLaboratorioRepository envios;
    private final LaboratorioRepository laboratorios;
    private final MedidaRepository medidas;
    private final ActorOrdenTrabajo actores;
    private final FlujoOrdenTrabajo flujo;

    private final VentaOrdenTrabajo ventasOrden;

    // >>>> QUERIES
    @Override @Transactional(readOnly=true)
    public Page<PendienteOrdenTrabajoDTO> pendientes(String termino,Pageable pageable) {
        return ventas.buscarPendientesOrdenTrabajo(escapar(limpiar(termino)),pageable);
    }

    @Override @Transactional(readOnly=true)
    public Page<OrdenTrabajoResponseDTO> listar(EstadoOrdenTrabajoEnum estado,String termino,Pageable pageable) {
        String t=limpiar(termino);
        Specification<OrdenTrabajo> spec=(root,query,cb)->{
            List<Predicate> filtros=new ArrayList<>();
            if(estado!=null) filtros.add(cb.equal(root.get("estado"),estado));
            if(t!=null) {
                var persona=root.join("lente").join("receta").join("paciente").join("persona");
                String pattern="%"+escapar(t.toLowerCase(Locale.ROOT))+"%";
                filtros.add(cb.or(cb.like(cb.lower(persona.get("nombres")),pattern,'!'),cb.like(cb.lower(persona.get("apellidos")),pattern,'!'),
                    cb.like(persona.get("numeroDocumento"),pattern,'!')
                    )
                );
            }
            return cb.and(filtros.toArray(Predicate[]::new));
        };
        return ordenes.findAll(spec,pageable).map(detalles::orden);
    }

    @Override @Transactional(readOnly=true)
    public OrdenTrabajoResponseDTO obtener(Long id) {
        return detalles.orden(ordenes.findById(id).orElseThrow(()->new RecursosNoEncontradosException("Orden no encontrada")));
    }

    @Override @Transactional(readOnly=true)
    public List<LaboratorioDTO> laboratorios() {
        return laboratorios.findByActivo(true).stream().map(l->new LaboratorioDTO(l.getId(), l.getNombre(), l.getRuc(), l.getTelefono(), l.getDireccion(), l.getCorreo(), (l.getActivo() == null ? null : Boolean.TRUE.equals(l.getActivo()) ? 1 : 0))).toList();
    }

    // >>>> CREATION
    @Override
    @Transactional
    public OrdenTrabajoResponseDTO generar(OrdenTrabajoRequestDTO request,String usuario) {
        flujo.validar(request); actores.obtener(usuario);
        var referencia=lentes.findById(request.idLente()).orElseThrow(()->new RecursosNoEncontradosException("Lente no encontrado"));
        recetas.findByIdForUpdate(referencia.getReceta().getId()).orElseThrow(()->new RecursosNoEncontradosException("Receta no encontrada"));
        var venta=ventasOrden.bloquear(referencia.getReceta().getId());
        var lente=lentes.findByIdForUpdate(request.idLente()).orElseThrow(()->new RecursosNoEncontradosException("Lente no encontrado"));
        validarVenta(venta,lente);
        if(ordenes.existsByLenteId(lente.getId())) throw new ReglaNegocioException("El lente ya tiene una orden de trabajo");
        var medidasLente=lente.getMedidas();
        if(medidasLente.isEmpty()) throw new ReglaNegocioException("El lente no tiene medidas asociadas");
        var tipos=new HashSet<TipoMedidaEnum>();
        for(var m:medidasLente) {
            ComparadorLensometria.validarOriginal(m);
            if(m.getEnvioLaboratorio()!=null || m.getReceta()==null || !m.getReceta().getId().equals(lente.getReceta().getId())
                || m.getTipoMedida()==null || !tipos.add(m.getTipoMedida())
            )
                throw new ReglaNegocioException("Las medidas deben pertenecer a la receta y tener tipos unicos");
        }
        var orden=new OrdenTrabajo(null, null, null, null, EstadoOrdenTrabajoEnum.PENDIENTE, ConstanciaOrdenTrabajo.agregar(request.descripcion(),"[Creacion] Usuario: "+usuario), lente);
        return detalles.orden(ordenes.saveAndFlush(orden));
    }

    // >>>> LABORATORY OPERATIONS
    @Override
    @Transactional
    public EnvioLaboratorioResponseDTO enviar(EnvioLaboratorioRequestDTO request,String usuario) {
        flujo.validar(request); actores.obtener(usuario); var ot=flujo.bloquear(request.idOrdenTrabajo());
        exigirVentaVigente(ot);
        if(ot.getEstado()!=EstadoOrdenTrabajoEnum.PENDIENTE && ot.getEstado()!=EstadoOrdenTrabajoEnum.CONTROL_CALIDAD_RECHAZADO)
            throw new ReglaNegocioException("Solo se envian ordenes pendientes o rechazadas");
        if(ot.getEstado()==EstadoOrdenTrabajoEnum.CONTROL_CALIDAD_RECHAZADO) {
            var previo=ultimo(ot);
            if(previo.getFechaRevision()==null || !Integer.valueOf(0).equals(previo.getAprobado()))
                throw new ReglaNegocioException("El reenvio requiere un rechazo previo registrado");
        } else if(envios.findFirstByOrdenTrabajoIdOrderByFechaEnvioDescIdDesc(ot.getId()).isPresent()) {
            throw new ReglaNegocioException("La orden pendiente ya registra un envio incompatible");
        }
        var laboratorio=laboratorios.findById(request.idLaboratorio()).filter(l->Boolean.TRUE.equals(l.getActivo()))
            .orElseThrow(()->new ReglaNegocioException("Seleccione un laboratorio existente y activo"));
        var envio=new EnvioLaboratorio(null, null, request.fechaRecepcionEstimada(), null, null, null, null, ConstanciaOrdenTrabajo.agregar(request.observaciones(),"[Envio] Usuario: "+usuario), ot, laboratorio);
        envios.saveAndFlush(envio); ot.setEstado(EstadoOrdenTrabajoEnum.ENVIADO_LABORATORIO);
        ordenes.saveAndFlush(ot); return detalles.envio(envio);
    }

    @Override
    @Transactional
    public EnvioLaboratorioResponseDTO recibir(Long idEnvio,RecepcionLaboratorioRequestDTO request,String usuario) {
        if(request!=null) flujo.validar(request);
        actores.obtener(usuario); var envio=flujo.bloquearEnvioActual(idEnvio); var ot=envio.getOrdenTrabajo();
        exigirVentaVigente(ot);
        if(envio.getFechaRecepcion()!=null || ot.getEstado()!=EstadoOrdenTrabajoEnum.ENVIADO_LABORATORIO)
            throw new ReglaNegocioException("El envio ya fue recibido o la orden no admite recepcion");
        envio.setFechaRecepcion(LocalDateTime.now().withNano(0));
        envio.setObservaciones(ConstanciaOrdenTrabajo.agregar(envio.getObservaciones(),"[Recepcion] Usuario: "+usuario
            +(request==null || request.observaciones()==null?"":". "+request.observaciones())
            )
        );
        ot.setEstado(EstadoOrdenTrabajoEnum.RECIBIDO_LABORATORIO);
        envios.saveAndFlush(envio); ordenes.saveAndFlush(ot); return detalles.envio(envio);
    }

    // >>>> DELIVERY
    @Override
    @Transactional
    public OrdenTrabajoResponseDTO entregar(EntregaPedidoRequestDTO request,String usuario) {
        flujo.validar(request); var actor=actores.obtener(usuario); var ot=flujo.bloquear(request.idOrdenTrabajo());
        if(request.idEmpleadoEntrega()!=null && !request.idEmpleadoEntrega().equals(actor.getId()))
            throw new AccessDeniedException("El empleado de entrega debe corresponder al usuario autenticado");
        if(ot.getEstado()!=EstadoOrdenTrabajoEnum.LISTO_ENTREGA || ot.getFechaEntrega()!=null)
            throw new ReglaNegocioException("La orden debe estar lista para entrega y no entregada previamente");
        var envio=ultimo(ot);
        if(envio.getFechaRecepcion()==null || envio.getFechaRevision()==null || !Integer.valueOf(1).equals(envio.getAprobado())
            || medidas.findByEnvioLaboratorioIdOrderByIdAsc(envio.getId()).isEmpty()
        )
            throw new ReglaNegocioException("La entrega requiere control de calidad aprobado y lensometria registrada");

        var venta=ventasOrden.bloquear(ot.getLente().getReceta().getId());
        validarVenta(venta,ot.getLente());
        BigDecimal pagado=pagos.sumMontoPagadoByVentaId(venta.getId());
        BigDecimal saldo=venta.getTotal().subtract(pagado);
        if(saldo.compareTo(BigDecimal.ZERO)!=0) throw new ReglaNegocioException("No se puede entregar: el saldo pendiente debe ser exactamente cero. Saldo: "+saldo);
        ot.setEstado(EstadoOrdenTrabajoEnum.ENTREGADO); ot.setFechaEntrega(LocalDateTime.now().withNano(0));
        ot.setDescripcion(ConstanciaOrdenTrabajo.agregar(ot.getDescripcion(),"[Entrega] Usuario: "+usuario
            +(request.observaciones()==null?"":". "+request.observaciones())
            )
        );
        return detalles.orden(ordenes.saveAndFlush(ot));
    }

    // >>>> HELPERS
    private void validarVenta(Venta venta,Lente lente) {
        if(venta.getEstado()!=EstadoVentaEnum.PENDIENTE && venta.getEstado()!=EstadoVentaEnum.PAGADA)
            throw new ReglaNegocioException("La venta debe estar vigente");
        if(venta.getReceta()==null || !venta.getReceta().getId().equals(lente.getReceta().getId())
            || venta.getPaciente()==null || !venta.getPaciente().getId().equals(lente.getReceta().getPaciente().getId())
        )
            throw new ReglaNegocioException("La venta, el lente y el paciente deben corresponder a la misma receta");
        BigDecimal pagado=pagos.sumMontoPagadoByVentaId(venta.getId());
        if(venta.getTotal()==null || venta.getTotal().signum()<=0 || pagado==null || pagado.signum()<=0 || pagado.compareTo(venta.getTotal())>0)
            throw new ReglaNegocioException("La venta debe tener pago registrado e importes consistentes");
    }

    private EnvioLaboratorio ultimo(OrdenTrabajo ot) {
        return envios.findFirstByOrdenTrabajoIdOrderByFechaEnvioDescIdDesc(ot.getId())
            .orElseThrow(()->new ReglaNegocioException("La orden no tiene un envio registrado"));
    }

    private void exigirVentaVigente(OrdenTrabajo ot) {
        ventasOrden.bloquear(ot.getLente().getReceta().getId());
    }

    private String escapar(String t) { return t==null?null:t.replace("!","!!").replace("%","!%").replace("_","!_"); }

    private String limpiar(String t) { return t==null || t.isBlank()?null:t.trim(); }

}
