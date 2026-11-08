package pe.edu.upeu.geovision_backend.service.orden_trabajo.impl;

import lombok.RequiredArgsConstructor;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EstadoNotificacionOrdenDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.NotificacionOrdenResponseDTO;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.OrdenTrabajoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.service.orden_trabajo.NotificacionOrdenService;
import pe.edu.upeu.geovision_backend.util.orden_trabajo.VentaOrdenTrabajo;

@Service
@RequiredArgsConstructor
public class NotificacionOrdenServiceImpl implements NotificacionOrdenService {
    static final String MENSAJE="Geo Vision: tu pedido esta listo para recoger. Comunicate con nosotros para coordinar la entrega.";
    private final OrdenTrabajoRepository ordenes;
    private final UsuarioRepository usuarios;
    private final EmpleadoRepository empleados;
    private final TextbeeClient textbee;
    private final EnvioLaboratorioRepository envios;
    private final MedidaRepository medidas;
    private final VentaOrdenTrabajo ventasOrden;
    private final PlatformTransactionManager manager;

    private TransactionTemplate transaccion() {
        var tx = new TransactionTemplate(manager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return tx;
    }

    @Override public NotificacionOrdenResponseDTO prepararWhatsApp(Long id,String telefono,String usuario) {
        return Objects.requireNonNull(transaccion().execute(s->{
            validarActor(usuario); var ot=listo(id); validarTelefono(ot,telefono);
            if(ot.getFechaNotificacion()!=null) return respuesta(ot,"WHATSAPP","CONFIRMADO_MANUAL",telefono,null,
                "Ya existe una constancia manual; no se modifica su fecha.",null);
            String enlace="https://wa.me/"+telefono.substring(1)+"?text="+URLEncoder.encode(MENSAJE,StandardCharsets.UTF_8).replace("+","%20");
            return respuesta(ot,"WHATSAPP","PREPARADO",telefono,null,"Enlace preparado; no se ha registrado un envio.",enlace);
        }));
    }
    @Override public NotificacionOrdenResponseDTO confirmarWhatsApp(Long id,String telefono,String usuario) {
        return Objects.requireNonNull(transaccion().execute(s->{
            validarActor(usuario); var ot=listo(id); validarTelefono(ot,telefono);
            // La confirmacion explicita del operador es la unica constancia persistida.
            if(ot.getFechaNotificacion()==null) {
                ot.setFechaNotificacion(LocalDateTime.now().withNano(0)); ordenes.saveAndFlush(ot);
            }
            return respuesta(ot,"WHATSAPP","CONFIRMADO_MANUAL",telefono,null,
                "El operador declaro el envio manual; entrega y lectura no verificadas.",null);
        }));
    }
    @Override public NotificacionOrdenResponseDTO enviarSms(Long id,String telefono,String usuario) {
        textbee.validarConfiguracion();
        var preparado=Objects.requireNonNull(transaccion().execute(s->{
            validarActor(usuario); var ot=listo(id); validarTelefono(ot,telefono);
            if(ot.getFechaNotificacion()!=null) throw new ReglaNegocioException("La orden ya tiene una constancia manual de notificacion");
            return respuesta(ot,"SMS","PREPARADO",telefono,null,null,null);
        }));
        // Sin historial ni reserva persistida. Cada POST explicito es una solicitud nueva.
        // No hay reintentos automaticos ni se equipara aceptacion en cola con entrega.
        var resultado=textbee.enviar(telefono,MENSAJE);
        return new NotificacionOrdenResponseDTO(id,"SMS",resultado.estado(),telefono,MENSAJE,
            preparado.fechaNotificacion(),resultado.referencia(),resultado.detalle(),null);
    }
    @Override public EstadoNotificacionOrdenDTO consultarEstado(Long id) {
        return Objects.requireNonNull(transaccion().execute(s->{
            var ot=ordenes.findById(id).orElseThrow(()->new RecursosNoEncontradosException("Orden no encontrada"));
            return new EstadoNotificacionOrdenDTO(id,ot.getFechaNotificacion());
        }));
    }
    private OrdenTrabajo listo(Long id) {
        var ot=ordenes.findByIdForUpdate(id).orElseThrow(()->new RecursosNoEncontradosException("Orden no encontrada"));
        if(ot.getEstado()!=EstadoOrdenTrabajoEnum.LISTO_ENTREGA)
            throw new ReglaNegocioException("Solo se notifican ordenes listas para entrega");
        var envio=envios.findFirstByOrdenTrabajoIdOrderByFechaEnvioDescIdDesc(id)
            .orElseThrow(()->new ReglaNegocioException("La orden no tiene control de calidad aprobado"));
        if(envio.getFechaRecepcion()==null || envio.getFechaRevision()==null || !Integer.valueOf(1).equals(envio.getAprobado())
            || medidas.findByEnvioLaboratorioIdOrderByIdAsc(envio.getId()).isEmpty())
            throw new ReglaNegocioException("La orden requiere lensometria y control de calidad aprobado");
        ventasOrden.bloquear(ot.getLente().getReceta().getId()); return ot;
    }
    private void validarTelefono(OrdenTrabajo ot,String telefono) {
        if(telefono==null || !telefono.matches("\\+[1-9][0-9]{7,14}")) throw new ReglaNegocioException("Use telefono internacional E.164");
        String registrado=ot.getLente().getReceta().getPaciente().getPersona().getTelefono();
        if(registrado==null || !telefono.equals(registrado.replaceAll("[\\s().-]","")))
            throw new ReglaNegocioException("Actualice el telefono internacional del paciente antes de notificar");
    }
    private void validarActor(String nombre) {
        var u=usuarios.findByNombre(nombre).orElseThrow(()->new ReglaNegocioException("Usuario no disponible"));
        var e=empleados.findByUsuarioId(u.getId()).orElseThrow(()->new ReglaNegocioException("La cuenta requiere empleado"));
        if(!Boolean.TRUE.equals(u.getActivo()) || !Boolean.TRUE.equals(e.getActivo()))
            throw new ReglaNegocioException("Se requiere usuario y empleado activos");
    }
    private NotificacionOrdenResponseDTO respuesta(OrdenTrabajo ot,String canal,String estado,String telefono,String referencia,String detalle,String enlace) {
        return new NotificacionOrdenResponseDTO(ot.getId(),canal,estado,telefono,MENSAJE,
            ot.getFechaNotificacion(),referencia,detalle,enlace);
    }
}
