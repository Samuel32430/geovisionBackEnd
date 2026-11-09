package pe.edu.upeu.geovision_backend.util.orden_trabajo;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

@Component @RequiredArgsConstructor
public class VentaOrdenTrabajo {

    // >>>> DEPENDENCIES
    private final VentaRepository ventas;
    private final RecetaRepository recetas;

    // >>>> OPERATIONS
    public Optional<Venta> consultar(Long idReceta) {
        var candidatas=ventas.findByRecetaId(idReceta);
        return candidatas.size()==1 ? Optional.of(candidatas.getFirst()) : Optional.empty();
    }
    public Venta bloquear(Long idReceta) {
        recetas.findByIdForUpdate(idReceta).orElseThrow(()->new ReglaNegocioException("No se pudo verificar la receta"));
        var candidatas=ventas.findByRecetaId(idReceta);
        if(candidatas.size()!=1)
            throw new ReglaNegocioException("La receta debe tener una unica venta para verificar el pedido y su saldo sin ambiguedad");
        var venta=ventas.findByIdForUpdate(candidatas.getFirst().getId())
            .orElseThrow(()->new ReglaNegocioException("No se pudo verificar la venta"));
        if(venta.getEstado()==EstadoVentaEnum.ANULADA) throw new ReglaNegocioException("La venta del pedido esta anulada");
        return venta;
    }
}
