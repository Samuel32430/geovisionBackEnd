package pe.edu.upeu.geovision_backend.service.ventas;

import pe.edu.upeu.geovision_backend.dto.ventas.PagoPendienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;

import java.util.List;

public interface PagoPendienteService {

    // >>>> QUERIES
    List<PagoPendienteResponseDTO> listarVentasConPagosPendientes(String termino);

    PagoPendienteResponseDTO obtenerSaldoPendientePorVenta(Long idVenta);

    // >>>> CREATION
    PagoResponseDTO registrarPagoPendiente(PagoRegistroRequestDTO request);

}
