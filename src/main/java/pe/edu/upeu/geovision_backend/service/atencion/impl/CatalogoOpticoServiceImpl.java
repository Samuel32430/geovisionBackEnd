package pe.edu.upeu.geovision_backend.service.atencion.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.atencion.CatalogoOpticoMapper;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MaterialCristalRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.TratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.service.atencion.CatalogoOpticoService;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CatalogoOpticoServiceImpl implements CatalogoOpticoService {

    private final MaterialCristalRepository materialCristalRepository;
    private final TratamientoCristalRepository tratamientoCristalRepository;
    private final MonturaRepository monturaRepository;
    private final CatalogoOpticoMapper catalogoOpticoMapper;

    public CatalogoOpticoServiceImpl(MaterialCristalRepository materialCristalRepository,
                                     TratamientoCristalRepository tratamientoCristalRepository,
                                     MonturaRepository monturaRepository, CatalogoOpticoMapper catalogoOpticoMapper) {
        this.materialCristalRepository = materialCristalRepository;
        this.tratamientoCristalRepository = tratamientoCristalRepository;
        this.monturaRepository = monturaRepository;
        this.catalogoOpticoMapper = catalogoOpticoMapper;
    }

    @Override
    public List<MaterialCristalDTO> listarMaterialesCristal() {
        return materialCristalRepository.findAll(Sort.by("nombre", "idMaterialCristal")).stream()
                .map(catalogoOpticoMapper::toMaterialCristalDTO).toList();
    }

    @Override
    public List<TratamientoCristalDTO> listarTratamientosCristal() {
        return tratamientoCristalRepository.findAll(Sort.by("nombre", "idTratamientoCristal")).stream()
                .map(catalogoOpticoMapper::toTratamientoCristalDTO).toList();
    }

    @Override
    public Page<MonturaDTO> listarMonturasDisponibles(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged() || pageable.getPageSize() > 100
                || pageable.getOffset() > Integer.MAX_VALUE) {
            throw new ReglaNegocioException("La consulta debe estar paginada con un máximo de 100 monturas");
        }
        Sort orden = pageable.getSort();
        for (Sort.Order campo : orden) {
            if (!Set.of("idProducto", "producto.nombre", "producto.precio").contains(campo.getProperty())) {
                throw new ReglaNegocioException("Campo de ordenamiento no permitido");
            }
        }
        if (orden.getOrderFor("idProducto") == null) {
            orden = orden.and(Sort.by("idProducto"));
        }
        return monturaRepository.findByProductoEstadoAndProductoStockGreaterThan(EstadoGeneralEnum.ACTIVO, 0,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), orden))
                .map(catalogoOpticoMapper::toMonturaDTO);
    }
}
