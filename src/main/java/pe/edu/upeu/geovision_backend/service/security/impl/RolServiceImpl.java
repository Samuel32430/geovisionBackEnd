package pe.edu.upeu.geovision_backend.service.security.impl;

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
public class RolServiceImpl implements RolService {

    private static final Logger log = LoggerFactory.getLogger(RolServiceImpl.class);

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final RolMapper rolMapper;
    private final PermisoMapper permisoMapper;

    public RolServiceImpl(
            RolRepository rolRepository,
            PermisoRepository permisoRepository,
            RolPermisoRepository rolPermisoRepository,
            RolMapper rolMapper,
            PermisoMapper permisoMapper) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolMapper = rolMapper;
        this.permisoMapper = permisoMapper;
    }

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

    @Override
    @Transactional
    public RolDTO crear(RolDTO request) {
        String nombre = request.getNombre().trim().toUpperCase();

        if (rolRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un rol con el nombre: " + nombre);
        }

        Rol rol = Rol.builder()
                .nombre(nombre)
                .build();
        Rol guardado = rolRepository.save(rol);

        log.info("Rol creado: id={} nombre={}", guardado.getIdRol(), guardado.getNombre());
        return rolMapper.toDTO(guardado, Collections.emptySet());
    }

    @Override
    @Transactional
    public RolDTO asignarPermisos(Long idRol, Set<Long> permisoIds) {
        Rol rol = rolRepository.findByIdForUpdate(idRol)
                .orElseThrow(() -> new RecursosNoEncontradosException("Rol no encontrado con ID: " + idRol));

        Set<Long> ids = (permisoIds != null) ? permisoIds : Collections.emptySet();

        List<Permiso> permisos = permisoRepository.findAllById(ids);
        if (permisos.size() != ids.size()) {
            Set<Long> encontrados = permisos.stream().map(Permiso::getIdPermiso).collect(Collectors.toSet());
            Set<Long> faltantes = new HashSet<>(ids);
            faltantes.removeAll(encontrados);
            throw new RecursosNoEncontradosException("Permisos no encontrados con IDs: " + faltantes);
        }

        List<RolPermiso> actuales = rolPermisoRepository.findByRolIdRol(idRol);
        Set<Long> existentes = actuales.stream().map(rp -> rp.getPermiso().getIdPermiso()).collect(Collectors.toSet());
        rolPermisoRepository.deleteAll(actuales.stream().filter(rp -> !ids.contains(rp.getPermiso().getIdPermiso())).toList());

        List<RolPermiso> nuevosRolPermisos = permisos.stream().filter(p -> !existentes.contains(p.getIdPermiso()))
                .map(permiso -> RolPermiso.builder()
                        .rol(rol)
                        .permiso(permiso)
                        .build())
                .toList();
        rolPermisoRepository.saveAllAndFlush(nuevosRolPermisos);

        log.info("Permisos asignados al rol {}: total={}", rol.getNombre(), permisos.size());

        Set<PermisoDTO> permisoDTOs = permisos.stream()
                .map(permisoMapper::toDTO)
                .collect(Collectors.toSet());

        return rolMapper.toDTO(rol, permisoDTOs);
    }

    private RolDTO cargarRolConPermisos(Rol rol) {
        List<RolPermiso> rolPermisos = rolPermisoRepository.findByRolIdRol(rol.getIdRol());
        Set<PermisoDTO> permisos = rolPermisos.stream()
                .map(rp -> permisoMapper.toDTO(rp.getPermiso()))
                .collect(Collectors.toSet());
        return rolMapper.toDTO(rol, permisos);
    }
}
