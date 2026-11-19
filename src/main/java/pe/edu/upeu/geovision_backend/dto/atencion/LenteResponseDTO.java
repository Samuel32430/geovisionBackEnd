package pe.edu.upeu.geovision_backend.dto.atencion;

import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;

import java.math.BigDecimal;
import java.util.List;

public record LenteResponseDTO(
    Long id,
    Long idReceta,
    BigDecimal subTotal,
    Long idMaterialCristal,
    String nombreMaterialCristal,
    Long idProductoMontura,
    String codigoMontura,
    String nombreMontura,
    BigDecimal precioMontura,
    List<TratamientoCristalDTO> tratamientos,
    List<MedidaResponseDTO> medidas
) {
    public LenteResponseDTO(
        Long id,
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
        this(id, idReceta, subTotal, idMaterialCristal, nombreMaterialCristal, idProductoMontura, codigoMontura, nombreMontura, null, tratamientos, medidas);
    }
}
