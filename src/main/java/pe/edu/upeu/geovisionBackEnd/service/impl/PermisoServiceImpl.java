package pe.edu.upeu.geovisionBackEnd.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.PermisoDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Permiso;
import pe.edu.upeu.geovisionBackEnd.enums.ModuloEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.mapper.PermisoMapper;
import pe.edu.upeu.geovisionBackEnd.repository.PermisoRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.PermisoService;

import java.util.List;

@Service
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;
    private final PermisoMapper permisoMapper;

    public PermisoServiceImpl(PermisoRepository permisoRepository, PermisoMapper permisoMapper) {
        this.permisoRepository = permisoRepository;
        this.permisoMapper = permisoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoDTO> listar() {
        return permisoRepository.findAll(Sort.by("modulo", "nombre"))
                .stream()
                .map(permisoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoDTO> listarPorModulo(ModuloEnum modulo) {
        return permisoRepository.findByModulo(modulo)
                .stream()
                .map(permisoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoDTO obtenerPorId(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Permiso no encontrado con ID: " + id));
        return permisoMapper.toDTO(permiso);
    }
}
