package pe.edu.upeu.geovisionBackEnd.service.service.ventaProductosServicios;

import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoPendienteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.SaldoPendienteDTO;

import java.util.List;

public interface PagoPendienteService {

    List<SaldoPendienteDTO> listarVentasConSaldoPendiente(String termino);

    SaldoPendienteDTO obtenerSaldoPendientePorComprobante(Long idComprobante);

    PagoResponseDTO registrarPagoPendiente(PagoPendienteRequestDTO request);
}
