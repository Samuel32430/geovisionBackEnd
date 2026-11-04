package pe.edu.upeu.geovisionBackEnd.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.entity.*;
import pe.edu.upeu.geovisionBackEnd.repository.*;
import pe.edu.upeu.geovisionBackEnd.exception.*;
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
        var ultimo=envios.findFirstByOrdenTrabajoIdOrdenTrabajoOrderByFechaEnvioDescIdEnvioLaboratorioDesc(orden.getIdOrdenTrabajo())
            .orElseThrow(()->new ReglaNegocioException("No hay envio vigente"));
        if(!ultimo.getIdEnvioLaboratorio().equals(id)) throw new ReglaNegocioException("Solo se puede operar el ultimo envio de la orden");
        return envio;
    }
}
