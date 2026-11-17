package pe.edu.upeu.geovision_backend.service.ventas;

import pe.edu.upeu.geovision_backend.dto.ventas.VentaRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;

public interface VentaService {

    // >>>> QUERIES
    VentaResponseDTO obtenerVentaPorId(Long idVenta);

    // >>>> CREATION
    VentaResponseDTO registrarVenta(VentaRegistroRequestDTO request);

}
