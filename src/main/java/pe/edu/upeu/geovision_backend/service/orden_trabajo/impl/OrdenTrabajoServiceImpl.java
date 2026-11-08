package pe.edu.upeu.geovision_backend.service.orden_trabajo.impl;

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
import pe.edu.upeu.geovision_backend.repository.atencion.LenteMedidaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.OrdenTrabajoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.mapper.orden_trabajo.OrdenTrabajoMapper;
import pe.edu.upeu.geovision_backend.service.orden_trabajo.OrdenTrabajoService;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ActorOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ComparadorLensometria;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.ConstanciaOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.FlujoOrdenTrabajo;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.VentaOrdenTrabajo;

@Service @RequiredArgsConstructor @Transactional
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {
    private final OrdenTrabajoRepository ordenes;
    private final LenteRepository lentes;
    private final RecetaRepository recetas;
    private final VentaRepository ventas;
    private final PagoRepository pagos;
    private final LenteMedidaRepository lenteMedidas;
    private final EnvioLaboratorioRepository envios;
    private final LaboratorioRepository laboratorios;
    private final MedidaRepository medidas;
    private final ActorOrdenTrabajo actores;
    private final FlujoOrdenTrabajo flujo;
    private final ComparadorLensometria comparador;
    private final OrdenTrabajoMapper mapper;
    private final VentaOrdenTrabajo ventasOrden;

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
                String pattern="%"+escapar(t.toLowerCase(java.util.Locale.ROOT))+"%";
                filtros.add(cb.or(cb.like(cb.lower(persona.get("nombres")),pattern,'!'),cb.like(cb.lower(persona.get("apellidos")),pattern,'!'),
                    cb.like(persona.get("numeroDocumento"),pattern,'!')));
            }
            return cb.and(filtros.toArray(Predicate[]::new));
        };
        return ordenes.findAll(spec,pageable).map(mapper::orden);
    }
    @Override @Transactional(readOnly=true)
    public OrdenTrabajoResponseDTO obtener(Long id) {
        return mapper.orden(ordenes.findById(id).orElseThrow(()->new RecursosNoEncontradosException("Orden no encontrada")));
    }
    @Override
    public OrdenTrabajoResponseDTO generar(OrdenTrabajoRequestDTO request,String usuario) {
        flujo.validar(request); actores.obtener(usuario);
        var referencia=lentes.findById(request.getIdLente()).orElseThrow(()->new RecursosNoEncontradosException("Lente no encontrado"));
        recetas.findByIdForUpdate(referencia.getReceta().getIdReceta()).orElseThrow(()->new RecursosNoEncontradosException("Receta no encontrada"));
        var venta=ventasOrden.bloquear(referencia.getReceta().getIdReceta());
        var lente=lentes.findByIdForUpdate(request.getIdLente()).orElseThrow(()->new RecursosNoEncontradosException("Lente no encontrado"));
        validarVenta(venta,lente);
        if(ordenes.existsByLenteIdLente(lente.getIdLente())) throw new ReglaNegocioException("El lente ya tiene una orden de trabajo");
        var vinculos=lenteMedidas.findByLenteIdLente(lente.getIdLente());
        if(vinculos.isEmpty()) throw new ReglaNegocioException("El lente no tiene medidas asociadas");
        var tipos=new HashSet<TipoMedidaEnum>();
        for(var vinculo:vinculos) {
            var m=vinculo.getMedida(); comparador.validarOriginal(m);
            if(m.getEnvioLaboratorio()!=null || m.getReceta()==null || !m.getReceta().getIdReceta().equals(lente.getReceta().getIdReceta())
                || m.getTipoMedida()==null || !tipos.add(m.getTipoMedida()))
                throw new ReglaNegocioException("Las medidas deben pertenecer a la receta y tener tipos unicos");
        }
        var orden=OrdenTrabajo.builder().lente(lente).descripcion(ConstanciaOrdenTrabajo.agregar(request.getDescripcion(),"[Creacion] Usuario: "+usuario))
            .estado(EstadoOrdenTrabajoEnum.PENDIENTE).build();
        return mapper.orden(ordenes.saveAndFlush(orden));
    }
    private void validarVenta(Venta venta,Lente lente) {
        if(venta.getEstado()!=EstadoVentaEnum.PENDIENTE && venta.getEstado()!=EstadoVentaEnum.PAGADA)
            throw new ReglaNegocioException("La venta debe estar vigente");
        if(venta.getReceta()==null || !venta.getReceta().getIdReceta().equals(lente.getReceta().getIdReceta())
            || venta.getPaciente()==null || !venta.getPaciente().getIdPersona().equals(lente.getReceta().getPaciente().getIdPersona()))
            throw new ReglaNegocioException("La venta, el lente y el paciente deben corresponder a la misma receta");
        BigDecimal pagado=pagos.sumMontoPagadoByVentaId(venta.getIdVenta());
        if(venta.getTotal()==null || venta.getTotal().signum()<=0 || pagado==null || pagado.signum()<=0 || pagado.compareTo(venta.getTotal())>0)
            throw new ReglaNegocioException("La venta debe tener pago registrado e importes consistentes");
    }
    @Override
    public EnvioLaboratorioResponseDTO enviar(EnvioLaboratorioRequestDTO request,String usuario) {
        flujo.validar(request); actores.obtener(usuario); var ot=flujo.bloquear(request.getIdOrdenTrabajo());
        exigirVentaVigente(ot);
        if(ot.getEstado()!=EstadoOrdenTrabajoEnum.PENDIENTE && ot.getEstado()!=EstadoOrdenTrabajoEnum.CONTROL_CALIDAD_RECHAZADO)
            throw new ReglaNegocioException("Solo se envian ordenes pendientes o rechazadas");
        if(ot.getEstado()==EstadoOrdenTrabajoEnum.CONTROL_CALIDAD_RECHAZADO) {
            var previo=ultimo(ot);
            if(previo.getFechaRevision()==null || !Integer.valueOf(0).equals(previo.getAprobado()))
                throw new ReglaNegocioException("El reenvio requiere un rechazo previo registrado");
        } else if(envios.findFirstByOrdenTrabajoIdOrdenTrabajoOrderByFechaEnvioDescIdEnvioLaboratorioDesc(ot.getIdOrdenTrabajo()).isPresent()) {
            throw new ReglaNegocioException("La orden pendiente ya registra un envio incompatible");
        }
        var laboratorio=laboratorios.findById(request.getIdLaboratorio()).filter(l->Boolean.TRUE.equals(l.getActivo()))
            .orElseThrow(()->new ReglaNegocioException("Seleccione un laboratorio existente y activo"));
        var envio=EnvioLaboratorio.builder().ordenTrabajo(ot).laboratorio(laboratorio)
            .fechaRecepcionEstimada(request.getFechaRecepcionEstimada())
            .observaciones(ConstanciaOrdenTrabajo.agregar(request.getObservaciones(),"[Envio] Usuario: "+usuario)).build();
        envios.saveAndFlush(envio); ot.setEstado(EstadoOrdenTrabajoEnum.ENVIADO_LABORATORIO);
        ordenes.saveAndFlush(ot); return mapper.envio(envio);
    }
    @Override
    public EnvioLaboratorioResponseDTO recibir(Long idEnvio,RecepcionLaboratorioRequestDTO request,String usuario) {
        if(request!=null) flujo.validar(request);
        actores.obtener(usuario); var envio=flujo.bloquearEnvioActual(idEnvio); var ot=envio.getOrdenTrabajo();
        exigirVentaVigente(ot);
        if(envio.getFechaRecepcion()!=null || ot.getEstado()!=EstadoOrdenTrabajoEnum.ENVIADO_LABORATORIO)
            throw new ReglaNegocioException("El envio ya fue recibido o la orden no admite recepcion");
        envio.setFechaRecepcion(LocalDateTime.now().withNano(0));
        envio.setObservaciones(ConstanciaOrdenTrabajo.agregar(envio.getObservaciones(),"[Recepcion] Usuario: "+usuario
            +(request==null || request.observaciones()==null?"":". "+request.observaciones())));
        ot.setEstado(EstadoOrdenTrabajoEnum.RECIBIDO_LABORATORIO);
        envios.saveAndFlush(envio); ordenes.saveAndFlush(ot); return mapper.envio(envio);
    }
    @Override
    public OrdenTrabajoResponseDTO entregar(EntregaPedidoRequestDTO request,String usuario) {
        flujo.validar(request); var actor=actores.obtener(usuario); var ot=flujo.bloquear(request.getIdOrdenTrabajo());
        if(request.getIdEmpleadoEntrega()!=null && !request.getIdEmpleadoEntrega().equals(actor.getIdPersona()))
            throw new AccessDeniedException("El empleado de entrega debe corresponder al usuario autenticado");
        if(ot.getEstado()!=EstadoOrdenTrabajoEnum.LISTO_ENTREGA || ot.getFechaEntrega()!=null)
            throw new ReglaNegocioException("La orden debe estar lista para entrega y no entregada previamente");
        var envio=ultimo(ot);
        if(envio.getFechaRecepcion()==null || envio.getFechaRevision()==null || !Integer.valueOf(1).equals(envio.getAprobado())
            || medidas.findByEnvioLaboratorioIdEnvioLaboratorioOrderByIdMedidaAsc(envio.getIdEnvioLaboratorio()).isEmpty())
            throw new ReglaNegocioException("La entrega requiere control de calidad aprobado y lensometria registrada");
        // Sin FK nueva: receta unica y bloqueo de la venta compartido con los abonos.
        var venta=ventasOrden.bloquear(ot.getLente().getReceta().getIdReceta());
        validarVenta(venta,ot.getLente());
        BigDecimal pagado=pagos.sumMontoPagadoByVentaId(venta.getIdVenta());
        BigDecimal saldo=venta.getTotal().subtract(pagado);
        if(saldo.compareTo(BigDecimal.ZERO)!=0) throw new ReglaNegocioException("No se puede entregar: el saldo pendiente debe ser exactamente cero. Saldo: "+saldo);
        ot.setEstado(EstadoOrdenTrabajoEnum.ENTREGADO); ot.setFechaEntrega(LocalDateTime.now().withNano(0));
        ot.setDescripcion(ConstanciaOrdenTrabajo.agregar(ot.getDescripcion(),"[Entrega] Usuario: "+usuario
            +(request.getObservaciones()==null?"":". "+request.getObservaciones())));
        return mapper.orden(ordenes.saveAndFlush(ot));
    }
    private EnvioLaboratorio ultimo(OrdenTrabajo ot) {
        return envios.findFirstByOrdenTrabajoIdOrdenTrabajoOrderByFechaEnvioDescIdEnvioLaboratorioDesc(ot.getIdOrdenTrabajo())
            .orElseThrow(()->new ReglaNegocioException("La orden no tiene un envio registrado"));
    }
    private void exigirVentaVigente(OrdenTrabajo ot) {
        ventasOrden.bloquear(ot.getLente().getReceta().getIdReceta());
    }
    @Override @Transactional(readOnly=true)
    public List<LaboratorioDTO> laboratorios() {
        return laboratorios.findByActivo(true).stream().map(l->LaboratorioDTO.builder()
            .idLaboratorio(l.getIdLaboratorio()).nombre(l.getNombre()).ruc(l.getRuc()).telefono(l.getTelefono())
            .direccion(l.getDireccion()).correo(l.getCorreo()).estado((l.getActivo() == null ? null : Boolean.TRUE.equals(l.getActivo()) ? 1 : 0)).build()).toList();
    }
    private String escapar(String t) { return t==null?null:t.replace("!","!!").replace("%","!%").replace("_","!_"); }
    private String limpiar(String t) { return t==null || t.isBlank()?null:t.trim(); }
}
