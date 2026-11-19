package pe.edu.upeu.geovision_backend.dto.ventas;

import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponseDTO(
    Long id,
    LocalDateTime fechaRegistro,
    BigDecimal total,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente,
    EstadoVentaEnum estado,
    String estadoDescripcion,
    TipoComprobanteEnum tipoComprobante,
    String tipoComprobanteDescripcion,
    String numeroComprobante,
    Long idEmpleado,
    String nombreCompletoEmpleado,
    Long idPaciente,
    String nombreCompletoPaciente,
    Long idReceta,
    List<ProductoVendidoDTO> productos,
    List<ServicioVendidoDTO> servicios,
    List<PagoResponseDTO> pagos,
    ConfiguracionEmpresaDTO datosEmpresa
) {
    public VentaResponseDTO(
        Long id,
        LocalDateTime fechaRegistro,
        BigDecimal total,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente,
        EstadoVentaEnum estado,
        String estadoDescripcion,
        TipoComprobanteEnum tipoComprobante,
        String tipoComprobanteDescripcion,
        String numeroComprobante,
        Long idEmpleado,
        String nombreCompletoEmpleado,
        Long idPaciente,
        String nombreCompletoPaciente,
        Long idReceta,
        List<ProductoVendidoDTO> productos,
        List<ServicioVendidoDTO> servicios,
        List<PagoResponseDTO> pagos
    ) {
        this(id, fechaRegistro, total, totalPagado, saldoPendiente, estado, estadoDescripcion,
             tipoComprobante, tipoComprobanteDescripcion, numeroComprobante, idEmpleado,
             nombreCompletoEmpleado, idPaciente, nombreCompletoPaciente, idReceta,
             productos, servicios, pagos, null);
    }
}
