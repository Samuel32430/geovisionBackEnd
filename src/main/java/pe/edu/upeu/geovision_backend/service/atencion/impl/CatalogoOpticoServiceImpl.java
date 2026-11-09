package pe.edu.upeu.geovision_backend.service.atencion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.atencion.CatalogoOpticoMapper;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MaterialCristalRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.TratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.service.atencion.CatalogoOpticoService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CatalogoOpticoServiceImpl implements CatalogoOpticoService {

    // >>>> DEPENDENCIES
    private final MaterialCristalRepository materialCristalRepository;
    private final TratamientoCristalRepository tratamientoCristalRepository;
    private final MonturaRepository monturaRepository;

    // >>>> QUERIES
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCristalDTO> listarMaterialesCristal() {
        return materialCristalRepository.findAll(Sort.by("nombre", "id")).stream()
                .map(CatalogoOpticoMapper::toMaterialCristalDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TratamientoCristalDTO> listarTratamientosCristal() {
        return tratamientoCristalRepository.findAll(Sort.by("nombre", "id")).stream()
                .map(CatalogoOpticoMapper::toTratamientoCristalDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MonturaDTO> listarMonturasDisponibles(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged() || pageable.getPageSize() > 100
                || pageable.getOffset() > Integer.MAX_VALUE
        ) {
            throw new ReglaNegocioException("La consulta debe estar paginada con un máximo de 100 monturas");
        }
        Sort orden = pageable.getSort();
        for (Sort.Order campo : orden) {
            if (!Set.of("id", "producto.nombre", "producto.precio").contains(campo.getProperty())) {
                throw new ReglaNegocioException("Campo de ordenamiento no permitido");
            }
        }
        if (orden.getOrderFor("id") == null) {
            orden = orden.and(Sort.by("id"));
        }
        return monturaRepository.findByProductoActivoAndProductoStockGreaterThan(true, 0,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), orden)
        )
                .map(CatalogoOpticoMapper::toMonturaDTO);
    }

}
