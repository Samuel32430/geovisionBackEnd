package pe.edu.upeu.geovision_backend.service.ventas;

import pe.edu.upeu.geovision_backend.dto.ventas.PagoPendienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;

import java.util.List;

public interface PagoPendienteService {

    /**
     * CUS07: Lista las ventas que registran un saldo pendiente de pago.
     * Permite filtrar opcionalmente por término de búsqueda (comprobante, DNI o cliente).
     *
     * @param termino criterio de búsqueda opcional
     * @return lista de ventas con balance adeudado
     */
    List<PagoPendienteResponseDTO> listarVentasConPagosPendientes(String termino);

    /**
     * CUS07: Obtiene el desglose financiero de deuda y saldo pendiente de una venta específica.
     *
     * @param idVenta identificador de la venta
     * @return detalle del balance pendiente e historial de pagos
     */
    PagoPendienteResponseDTO obtenerSaldoPendientePorVenta(Long idVenta);

    /**
     * CUS07: Registra un abono o liquidación total del saldo pendiente de una venta.
     * Utiliza bloqueo pesimista para garantizar consistencia transaccional y evitar sobrepagos concurrentes.
     *
     * @param request datos del pago a registrar
     * @return detalle del pago confirmado
     */
    PagoResponseDTO registrarPagoPendiente(PagoRegistroRequestDTO request);
}
