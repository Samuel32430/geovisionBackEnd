package pe.edu.upeu.geovision_backend.service.gestion_mantenimiento.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.CategoriaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.LaboratorioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.ServicioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Laboratorio;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Servicio;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.gestion_mantenimiento.MantenimientoMapper;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.CategoriaRepository;
import pe.edu.upeu.geovision_backend.repository.orden_trabajo.EnvioLaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.LaboratorioRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MantenimientoLock;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MaterialCristalRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.ServicioRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ServicioVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.TratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.service.gestion_mantenimiento.MantenimientoService;

import java.util.Locale;
import java.util.function.Function;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MantenimientoServiceImpl implements MantenimientoService {
    private final CategoriaRepository categoriaRepository;
    private final MaterialCristalRepository materialCristalRepository;
    private final TratamientoCristalRepository tratamientoCristalRepository;
    private final ServicioRepository servicioRepository;
    private final LaboratorioRepository laboratorioRepository;
    private final ProductoRepository productoRepository;
    private final ServicioVendidoRepository servicioVendidoRepository;
    private final EnvioLaboratorioRepository envioLaboratorioRepository;
    private final MantenimientoMapper mapper;
    private final MantenimientoLock lock;

    @Override
    public Page<CategoriaDTO> listarCategoria(String termino, Integer estado, Pageable pageable) {
        return listar(categoriaRepository, termino, estado, pageable, mapper::toDTO);
    }

    @Override
    public CategoriaDTO obtenerCategoria(Long id) {
        return mapper.toDTO(obtener(categoriaRepository, id, "Categoria"));
    }

    @Override
    @Transactional
    public CategoriaDTO registrarCategoria(CategoriaDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.CATEGORIA, false);
        validarNombre(categoriaRepository, request.getNombre(), "idCategoria", null, "Categoria");
        Categoria entity = new Categoria();
        mapper.actualizar(request, entity);
        return mapper.toDTO(categoriaRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.CATEGORIA, Integer.valueOf(0).equals(request.getEstado()));
        Categoria entity = obtener(categoriaRepository, id, "Categoria");
        validarNombre(categoriaRepository, request.getNombre(), "idCategoria", id, "Categoria");
        if (Integer.valueOf(0).equals(request.getEstado())) validarDependenciasCategoria(id);
        mapper.actualizar(request, entity);
        return mapper.toDTO(categoriaRepository.saveAndFlush(entity));
    }

    @Override
    public Page<MaterialCristalDTO> listarMaterialCristal(String termino, Pageable pageable) {
        return listar(materialCristalRepository, termino, null, pageable, mapper::toDTO);
    }

    @Override
    public MaterialCristalDTO obtenerMaterialCristal(Long id) {
        return mapper.toDTO(obtener(materialCristalRepository, id, "MaterialCristal"));
    }

    @Override
    @Transactional
    public MaterialCristalDTO registrarMaterialCristal(MaterialCristalDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.MATERIAL_CRISTAL, false);
        validarNombre(materialCristalRepository, request.getNombre(), "idMaterialCristal", null, "MaterialCristal");
        MaterialCristal entity = new MaterialCristal();
        mapper.actualizar(request, entity);
        return mapper.toDTO(materialCristalRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public MaterialCristalDTO actualizarMaterialCristal(Long id, MaterialCristalDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.MATERIAL_CRISTAL, false);
        MaterialCristal entity = obtener(materialCristalRepository, id, "MaterialCristal");
        validarNombre(materialCristalRepository, request.getNombre(), "idMaterialCristal", id, "MaterialCristal");
        mapper.actualizar(request, entity);
        return mapper.toDTO(materialCristalRepository.saveAndFlush(entity));
    }

    @Override
    public Page<TratamientoCristalDTO> listarTratamientoCristal(String termino, Pageable pageable) {
        return listar(tratamientoCristalRepository, termino, null, pageable, mapper::toDTO);
    }

    @Override
    public TratamientoCristalDTO obtenerTratamientoCristal(Long id) {
        return mapper.toDTO(obtener(tratamientoCristalRepository, id, "TratamientoCristal"));
    }

    @Override
    @Transactional
    public TratamientoCristalDTO registrarTratamientoCristal(TratamientoCristalDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.TRATAMIENTO_CRISTAL, false);
        validarNombre(tratamientoCristalRepository, request.getNombre(), "idTratamientoCristal", null, "TratamientoCristal");
        TratamientoCristal entity = new TratamientoCristal();
        mapper.actualizar(request, entity);
        return mapper.toDTO(tratamientoCristalRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public TratamientoCristalDTO actualizarTratamientoCristal(Long id, TratamientoCristalDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.TRATAMIENTO_CRISTAL, false);
        TratamientoCristal entity = obtener(tratamientoCristalRepository, id, "TratamientoCristal");
        validarNombre(tratamientoCristalRepository, request.getNombre(), "idTratamientoCristal", id, "TratamientoCristal");
        mapper.actualizar(request, entity);
        return mapper.toDTO(tratamientoCristalRepository.saveAndFlush(entity));
    }

    @Override
    public Page<ServicioDTO> listarServicio(String termino, Integer estado, Pageable pageable) {
        return listar(servicioRepository, termino, estado, pageable, mapper::toDTO);
    }

    @Override
    public ServicioDTO obtenerServicio(Long id) {
        return mapper.toDTO(obtener(servicioRepository, id, "Servicio"));
    }

    @Override
    @Transactional
    public ServicioDTO registrarServicio(ServicioDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.SERVICIO, false);
        validarNombre(servicioRepository, request.getNombre(), "idServicio", null, "Servicio");
        Servicio entity = new Servicio();
        mapper.actualizar(request, entity);
        return mapper.toDTO(servicioRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public ServicioDTO actualizarServicio(Long id, ServicioDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.SERVICIO, Integer.valueOf(0).equals(request.getEstado()));
        Servicio entity = obtener(servicioRepository, id, "Servicio");
        validarNombre(servicioRepository, request.getNombre(), "idServicio", id, "Servicio");
        if (Integer.valueOf(0).equals(request.getEstado())) validarDependenciasServicio(id);
        mapper.actualizar(request, entity);
        return mapper.toDTO(servicioRepository.saveAndFlush(entity));
    }

    @Override
    public Page<LaboratorioDTO> listarLaboratorio(String termino, Integer estado, Pageable pageable) {
        return listar(laboratorioRepository, termino, estado, pageable, mapper::toDTO);
    }

    @Override
    public LaboratorioDTO obtenerLaboratorio(Long id) {
        return mapper.toDTO(obtener(laboratorioRepository, id, "Laboratorio"));
    }

    @Override
    @Transactional
    public LaboratorioDTO registrarLaboratorio(LaboratorioDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.LABORATORIO, false);
        validarNombre(laboratorioRepository, request.getNombre(), "idLaboratorio", null, "Laboratorio");
        Laboratorio entity = new Laboratorio();
        mapper.actualizar(request, entity);
        return mapper.toDTO(laboratorioRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public LaboratorioDTO actualizarLaboratorio(Long id, LaboratorioDTO request) {
        lock.bloquear(MantenimientoLock.Catalogo.LABORATORIO, Integer.valueOf(0).equals(request.getEstado()));
        Laboratorio entity = obtener(laboratorioRepository, id, "Laboratorio");
        validarNombre(laboratorioRepository, request.getNombre(), "idLaboratorio", id, "Laboratorio");
        if (Integer.valueOf(0).equals(request.getEstado())) validarDependenciasLaboratorio(id);
        mapper.actualizar(request, entity);
        return mapper.toDTO(laboratorioRepository.saveAndFlush(entity));
    }

    private void validarDependenciasCategoria(Long id) {
        if (productoRepository.existsByCategoriaIdCategoriaAndEstado(id, EstadoGeneralEnum.ACTIVO)) {
            throw new ReglaNegocioException("No se puede desactivar la categoría: tiene productos activos.");
        }
    }

    private void validarDependenciasServicio(Long id) {
        if (servicioVendidoRepository.existsByServicioIdServicioAndVentaEstado(id, EstadoVentaEnum.PENDIENTE)) {
            throw new ReglaNegocioException("No se puede desactivar el servicio: está asociado a ventas pendientes.");
        }
    }

    private void validarDependenciasLaboratorio(Long id) {
        Specification<EnvioLaboratorio> pendientes = (root, query, cb) -> cb.and(
                cb.equal(root.get("laboratorio").get("idLaboratorio"), id),
                cb.or(cb.isNull(root.get("fechaRecepcion")), cb.isNull(root.get("fechaRevision"))));
        if (envioLaboratorioRepository.exists(pendientes)) {
            throw new ReglaNegocioException("No se puede desactivar el laboratorio: tiene envíos pendientes de recepción o revisión.");
        }
    }

    private <E> E obtener(JpaRepository<E, Long> repository, Long id, String catalogo) {
        return repository.findById(id).orElseThrow(
                () -> new RecursosNoEncontradosException(catalogo + " no encontrado con ID: " + id));
    }

    private <E> void validarNombre(JpaSpecificationExecutor<E> repository, String nombre,
                                   String campoId, Long id, String catalogo) {
        String normalizado = nombre.trim().toLowerCase(Locale.ROOT);
        Specification<E> duplicado = (root, query, cb) -> {
            var mismoNombre = cb.equal(cb.lower(cb.trim(root.get("nombre"))), normalizado);
            return id == null ? mismoNombre : cb.and(mismoNombre, cb.notEqual(root.get(campoId), id));
        };
        if (repository.exists(duplicado)) {
            throw new ReglaNegocioException("Ya existe un registro de " + catalogo + " con ese nombre.");
        }
    }

    private <E, D> Page<D> listar(JpaSpecificationExecutor<E> repository, String termino, Integer estado,
                                  Pageable pageable, Function<E, D> convertir) {
        Specification<E> filtro = (root, query, cb) -> {
            var condiciones = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (termino != null && !termino.isBlank()) {
                String literal = termino.trim().toLowerCase(Locale.ROOT)
                        .replace("!", "!!").replace("%", "!%").replace("_", "!_");
                condiciones.add(cb.like(cb.lower(root.get("nombre")), "%" + literal + "%", '!'));
            }
            if (estado != null) condiciones.add(cb.equal(root.get("estado"), EstadoGeneralEnum.fromCodigo(estado)));
            return cb.and(condiciones.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        return repository.findAll(filtro, pageable).map(convertir);
    }
}
