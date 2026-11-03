package pe.edu.upeu.geovisionBackEnd.util;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RecetaRepository;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;

/** El esquema original relaciona la OT con la venta exclusivamente a traves de su receta. */
@Component @RequiredArgsConstructor
public class VentaOrdenTrabajo {
    private final VentaRepository ventas;
    private final RecetaRepository recetas;
    public Optional<Venta> consultar(Long idReceta) {
        var candidatas=ventas.findByRecetaIdReceta(idReceta);
        return candidatas.size()==1 ? Optional.of(candidatas.getFirst()) : Optional.empty();
    }
    public Venta bloquear(Long idReceta) {
        recetas.findByIdForUpdate(idReceta).orElseThrow(()->new ReglaNegocioException("No se pudo verificar la receta"));
        var candidatas=ventas.findByRecetaIdReceta(idReceta);
        if(candidatas.size()!=1)
            throw new ReglaNegocioException("La receta debe tener una unica venta para verificar el pedido y su saldo sin ambiguedad");
        var venta=ventas.findByIdForUpdate(candidatas.getFirst().getIdVenta())
            .orElseThrow(()->new ReglaNegocioException("No se pudo verificar la venta"));
        if(venta.getEstado()==EstadoVentaEnum.ANULADA) throw new ReglaNegocioException("La venta del pedido esta anulada");
        return venta;
    }
}
