package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LenteResponseDTO {

    private Long idLente;
    private Long recetaId;
    private Long disenoLenteId;
    private String disenoLenteNombre;
    private Long cristalId;
    private Long monturaId;
    private String descripcionMonturaPropia;
    private String observaciones;
    private BigDecimal subTotal;
}
