package pe.edu.upeu.geovisionBackEnd.service.service.ventaProductosServicios;

import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.generic.PaginaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.FiltroVentaDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaDetalleResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaResumenDTO;

public interface VentaHistorialService {

    PaginaResponseDTO<VentaResumenDTO> listarHistorialVentas(FiltroVentaDTO filtro, Pageable pageable);

    VentaDetalleResponseDTO obtenerDetalleVenta(Long idVenta);
}
