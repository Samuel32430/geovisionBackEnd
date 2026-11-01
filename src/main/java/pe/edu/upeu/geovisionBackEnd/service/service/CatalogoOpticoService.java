package pe.edu.upeu.geovisionBackEnd.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.MaterialCristalDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MonturaDTO;
import pe.edu.upeu.geovisionBackEnd.dto.TratamientoCristalDTO;

import java.util.List;

public interface CatalogoOpticoService {

    List<MaterialCristalDTO> listarMaterialesCristal();

    List<TratamientoCristalDTO> listarTratamientosCristal();

    Page<MonturaDTO> listarMonturasDisponibles(Pageable pageable);
}
