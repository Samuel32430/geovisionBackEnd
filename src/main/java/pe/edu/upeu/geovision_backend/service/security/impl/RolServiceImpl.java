package pe.edu.upeu.geovision_backend.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.entity.security.Permiso;
import pe.edu.upeu.geovision_backend.entity.security.Rol;
import pe.edu.upeu.geovision_backend.entity.security.RolPermiso;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.security.PermisoMapper;
import pe.edu.upeu.geovision_backend.mapper.security.RolMapper;
import pe.edu.upeu.geovision_backend.repository.security.PermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolRepository;
import pe.edu.upeu.geovision_backend.service.security.RolService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    // >>>> CONSTANTS
    private static final Logger log = LoggerFactory.getLogger(RolServiceImpl.class);

    // >>>> DEPENDENCIES
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;

    // >>>> QUERIES
    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listar() {
        return rolRepository.findAll(Sort.by("nombre"))
                .stream()
                .map(this::cargarRolConPermisos)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO obtenerPorId(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Rol no encontrado con ID: " + id));
        return cargarRolConPermisos(rol);
    }

    // >>>> CREATION
    @Override
    @Transactional
    public RolDTO crear(RolDTO request) {
        String nombre = request.nombre().trim().toUpperCase();

        if (rolRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un rol con el nombre: " + nombre);
        }

        Rol rol = new Rol(null, nombre);
        Rol guardado = rolRepository.save(rol);

        log.info("Rol creado: id={} nombre={}", guardado.getId(), guardado.getNombre());
        return RolMapper.toDTO(guardado, Collections.emptySet());
    }

    // >>>> UPDATES
    @Override
    @Transactional
    public RolDTO asignarPermisos(Long idRol, Set<Long> permisoIds) {
        Rol rol = rolRepository.findByIdForUpdate(idRol)
                .orElseThrow(() -> new RecursosNoEncontradosException("Rol no encontrado con ID: " + idRol));

        Set<Long> ids = (permisoIds != null) ? permisoIds : Collections.emptySet();

        List<Permiso> permisos = permisoRepository.findAllById(ids);
        if (permisos.size() != ids.size()) {
            Set<Long> encontrados = permisos.stream().map(Permiso::getId).collect(Collectors.toSet());
            Set<Long> faltantes = new HashSet<>(ids);
            faltantes.removeAll(encontrados);
            throw new RecursosNoEncontradosException("Permisos no encontrados con IDs: " + faltantes);
        }

        List<RolPermiso> actuales = rolPermisoRepository.findByRolId(idRol);
        Set<Long> existentes = actuales.stream().map(rp -> rp.getPermiso().getId()).collect(Collectors.toSet());
        rolPermisoRepository.deleteAll(actuales.stream().filter(rp -> !ids.contains(rp.getPermiso().getId())).toList());

        List<RolPermiso> nuevosRolPermisos = permisos.stream().filter(p -> !existentes.contains(p.getId()))
                .map(permiso -> new RolPermiso(null, rol, permiso))
                .toList();
        rolPermisoRepository.saveAllAndFlush(nuevosRolPermisos);

        log.info("Permisos asignados al rol {}: total={}", rol.getNombre(), permisos.size());

        Set<PermisoDTO> permisoDTOs = permisos.stream()
                .map(PermisoMapper::toDTO)
                .collect(Collectors.toSet());

        return RolMapper.toDTO(rol, permisoDTOs);
    }

    // >>>> HELPERS
    private RolDTO cargarRolConPermisos(Rol rol) {
        List<RolPermiso> rolPermisos = rolPermisoRepository.findByRolId(rol.getId());
        Set<PermisoDTO> permisos = rolPermisos.stream()
                .map(rp -> PermisoMapper.toDTO(rp.getPermiso()))
                .collect(Collectors.toSet());
        return RolMapper.toDTO(rol, permisos);
    }

}
