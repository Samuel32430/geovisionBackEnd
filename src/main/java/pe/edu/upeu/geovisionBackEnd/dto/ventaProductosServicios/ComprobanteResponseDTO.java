package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteResponseDTO {

    private Long idComprobante;
    private String serie;
    private String numero;
    private LocalDate fechaEmision;
    private BigDecimal totalEmitido;
    private String estadoDocumento;
    private String estadoCobro;
    private LocalDate fechaCancelacion;
    private Long tipoComprobanteId;
    private String tipoComprobanteNombre;
    private Long estadoComprobanteId;
    private String estadoComprobanteNombre;
    private List<PagoResponseDTO> pagos = new ArrayList<>();
}
