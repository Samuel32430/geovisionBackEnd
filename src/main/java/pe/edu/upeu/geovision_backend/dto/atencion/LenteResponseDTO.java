package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;


import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record LenteResponseDTO(
    Long idLente,
    Long idReceta,
    BigDecimal subTotal,
    Long idMaterialCristal,
    String nombreMaterialCristal,
    Long idProductoMontura,
    String codigoMontura,
    String nombreMontura,
    List<TratamientoCristalDTO> tratamientos,
    List<MedidaResponseDTO> medidas
) {
}
