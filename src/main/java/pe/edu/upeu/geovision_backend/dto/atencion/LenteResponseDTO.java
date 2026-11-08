package pe.edu.upeu.geovision_backend.dto.atencion;

import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LenteResponseDTO {

    private Long idLente;
    private Long idReceta;
    private BigDecimal subTotal;
    private Long idMaterialCristal;
    private String nombreMaterialCristal;
    private Long idProductoMontura;
    private String codigoMontura;
    private String nombreMontura;
    private List<TratamientoCristalDTO> tratamientos;
    private List<MedidaResponseDTO> medidas;
}
