package pe.edu.upeu.geovision_backend.service.atencion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;

import java.util.List;

public interface CatalogoOpticoService {

    // >>>> QUERIES
    List<MaterialCristalDTO> listarMaterialesCristal();

    List<TratamientoCristalDTO> listarTratamientosCristal();

    Page<MonturaDTO> listarMonturasDisponibles(Pageable pageable);

}
