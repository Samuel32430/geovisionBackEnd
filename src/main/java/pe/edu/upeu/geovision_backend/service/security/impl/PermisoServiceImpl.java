package pe.edu.upeu.geovision_backend.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.entity.security.Permiso;
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.mapper.security.PermisoMapper;
import pe.edu.upeu.geovision_backend.repository.security.PermisoRepository;
import pe.edu.upeu.geovision_backend.service.security.PermisoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermisoDTO> listar() {
        return permisoRepository.findAll(Sort.by("modulo", "nombre"))
                .stream()
                .map(PermisoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoDTO> listarPorModulo(ModuloEnum modulo) {
        return permisoRepository.findByModulo(modulo)
                .stream()
                .map(PermisoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoDTO obtenerPorId(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Permiso no encontrado con ID: " + id));
        return PermisoMapper.toDTO(permiso);
    }
}
