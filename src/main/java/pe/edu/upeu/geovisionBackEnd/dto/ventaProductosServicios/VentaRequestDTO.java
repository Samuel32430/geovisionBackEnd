package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {

    private Long pacienteId;

    private Long compradorId;

    @NotNull(message = "El empleado responsable es obligatorio")
    private Long empleadoId;

    private Long recetaId;

    private String observaciones;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    private Long tipoComprobanteId;

    private String serieComprobante;

    private String numeroComprobante;

    @Valid
    private List<ItemVendidoRequestDTO> items = new ArrayList<>();

    @Valid
    private List<ServicioVendidoRequestDTO> servicios = new ArrayList<>();

    @Valid
    private LenteRequestDTO lente;

    @Valid
    private PagoInicialRequestDTO pagoInicial;
}
