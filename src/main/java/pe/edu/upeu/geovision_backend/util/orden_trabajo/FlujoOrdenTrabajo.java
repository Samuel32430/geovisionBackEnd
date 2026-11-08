package pe.edu.upeu.geovision_backend.util.orden_trabajo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.OrdenTrabajoRepository;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import jakarta.validation.Validator;

@Component @RequiredArgsConstructor
public class FlujoOrdenTrabajo {
    private final OrdenTrabajoRepository ordenes;
    private final EnvioLaboratorioRepository envios;
    private final Validator validator;
    public void validar(Object request) {
        if(request==null || !validator.validate(request).isEmpty()) throw new ReglaNegocioException("Complete correctamente los datos requeridos");
    }
    public OrdenTrabajo bloquear(Long id) {
        return ordenes.findByIdForUpdate(id).orElseThrow(()->new RecursosNoEncontradosException("Orden de trabajo no encontrada"));
    }
    public EnvioLaboratorio bloquearEnvioActual(Long id) {
        // Orden de bloqueo comun: OT -> envio. Evita ciclos con calidad y entrega.
        var idOrden=envios.findIdOrdenByIdEnvio(id).orElseThrow(()->new RecursosNoEncontradosException("Envio no encontrado"));
        var orden=bloquear(idOrden);
        var envio=envios.findByIdForUpdate(id).orElseThrow(()->new RecursosNoEncontradosException("Envio no encontrado"));
        var ultimo=envios.findFirstByOrdenTrabajoIdOrderByFechaEnvioDescIdDesc(orden.getId())
            .orElseThrow(()->new ReglaNegocioException("No hay envio vigente"));
        if(!ultimo.getId().equals(id)) throw new ReglaNegocioException("Solo se puede operar el ultimo envio de la orden");
        return envio;
    }
}
