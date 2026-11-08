package pe.edu.upeu.geovision_backend.service.ventas;

import pe.edu.upeu.geovision_backend.dto.ventas.VentaRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;

public interface VentaService {

    /**
     * Registra una venta unificada (productos, servicios o lentes con receta).
     * Aplica validaciones clínicas, verificación y descuento de stock, cálculo de totales
     * y registro del pago inicial o total.
     *
     * @param request datos de la venta y pagos iniciales
     * @return detalle de la venta registrada
     */
    VentaResponseDTO registrarVenta(VentaRegistroRequestDTO request);

    /**
     * Obtiene el detalle de una venta por su ID.
     *
     * @param idVenta ID de la venta
     * @return detalle de la venta
     */
    VentaResponseDTO obtenerVentaPorId(Long idVenta);
}
