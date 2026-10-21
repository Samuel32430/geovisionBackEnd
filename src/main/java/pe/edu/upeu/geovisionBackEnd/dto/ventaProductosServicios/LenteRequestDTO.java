package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LenteRequestDTO {

    @NotNull(message = "El id de la receta es obligatorio para confeccionar lente")
    private Long recetaId;

    @NotNull(message = "El id del diseño de lente es obligatorio")
    private Long disenoLenteId;

    @NotNull(message = "El id del cristal es obligatorio")
    private Long cristalId;

    private Long monturaId;

    private String descripcionMonturaPropia;

    private String observaciones;

    @DecimalMin(value = "0.00", message = "El subtotal no puede ser negativo")
    private BigDecimal subTotal = BigDecimal.ZERO;
}
