package pe.edu.upeu.geovision_backend.service.atencion.impl;

import java.util.LinkedHashSet;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Medida;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.atencion.RecetaMapper;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaOjoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MaterialCristalRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.TratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaService;
import pe.edu.upeu.geovision_backend.util.atencion.OpticaValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    // >>>> DEPENDENCIES
    private final RecetaRepository recetaRepository;
    private final MedidaRepository medidaRepository;
    private final MedidaOjoRepository medidaOjoRepository;
    private final PacienteRepository pacienteRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;

    private final LenteRepository lenteRepository;
    private final MaterialCristalRepository materialCristalRepository;
    private final TratamientoCristalRepository tratamientoCristalRepository;
    private final MonturaRepository monturaRepository;
    private final VentaRepository ventaRepository;

    // >>>> QUERIES
    @Override
    @Transactional(readOnly = true)
    public RecetaResponseDTO obtenerRecetaPorId(Long idReceta) {
        Receta receta = recetaRepository.findByIdWithDetails(idReceta)
                .orElseThrow(() -> new RecursosNoEncontradosException("Receta no encontrada"));
        return mapearRecetas(List.of(receta)).getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listarHistorialPorPaciente(Long idPaciente) {
        verificarPaciente(idPaciente);
        return mapearRecetas(recetaRepository.findByPacienteIdOrderByFechaRegistroDescIdDesc(idPaciente));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecetaResponseDTO> listarRecetasPaginadas(Long idPaciente, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged() || pageable.getPageSize() > 100
                || pageable.getOffset() > Integer.MAX_VALUE
        ) {
            throw new ReglaNegocioException("La consulta debe estar paginada con un máximo de 100 recetas");
        }
        verificarPaciente(idPaciente);

        Pageable pagina = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "fechaRegistro", "id")
        );
        Page<Receta> recetas = recetaRepository.findByPacienteId(idPaciente, pagina);
        return new PageImpl<>(mapearRecetas(recetas.getContent()), pagina, recetas.getTotalElements());
    }

    // >>>> CREATION
    @Override
    @Transactional
    public RecetaResponseDTO registrarExamenVisual(RecetaRequestDTO request, String usernameAuditoria) {
        if (request.esExterno() == 1) {
            throw new ReglaNegocioException("Utilice el endpoint de receta externa para una prescripción externa");
        }
        return registrar(request, usernameAuditoria, 0);
    }

    @Override
    @Transactional
    public RecetaResponseDTO registrarRecetaExterna(RecetaRequestDTO request, String usernameAuditoria) {
        return registrar(request, usernameAuditoria, 1);
    }

    // >>>> OPERATIONS
    @Override
    @Transactional
    public LenteResponseDTO configurarLente(Long idReceta, LenteRequestDTO request) {
        if (!request.idReceta().equals(idReceta)) {
            throw new ReglaNegocioException("La receta del cuerpo debe coincidir con la receta de la ruta");
        }
        Receta receta = recetaRepository.findByIdForUpdate(idReceta)
                .orElseThrow(() -> new RecursosNoEncontradosException("Receta no encontrada"));
        // Sales must acquire this same prescription lock.
        if (ventaRepository.existsByRecetaId(idReceta)) {
            throw new ReglaNegocioException("No se puede configurar una receta que ya está vinculada a una venta");
        }
        MaterialCristal material = materialCristalRepository.findById(request.idMaterialCristal())
                .orElseThrow(() -> new RecursosNoEncontradosException("Material de cristal no encontrado"));
        Montura montura = null;
        if (request.idProducto() != null) {
            montura = monturaRepository.findById(request.idProducto())
                    .orElseThrow(() -> new RecursosNoEncontradosException("Montura no encontrada"));
            if (!Boolean.TRUE.equals(montura.getProducto().getActivo())
                    || montura.getProducto().getStock() <= 0
            ) {
                throw new ReglaNegocioException("La montura debe estar activa y tener stock disponible");
            }
        }
        List<Medida> medidas = seleccionarMedidas(idReceta, request.medidasIds());
        List<TratamientoCristal> tratamientos = seleccionarTratamientos(request.tratamientosCristalIds());
        Lente lente = lenteRepository.saveAndFlush(new Lente(null, request.subTotal(), receta, material,
                montura, new LinkedHashSet<>(medidas), new LinkedHashSet<>(tratamientos)
            )
        );
        return RecetaMapper.toLenteResponse(lente, medidas, tratamientos);
    }

    // >>>> HELPERS
    private void verificarPaciente(Long idPaciente) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new RecursosNoEncontradosException("Paciente no encontrado");
        }
    }

    private List<RecetaResponseDTO> mapearRecetas(List<Receta> recetas) {
        if (recetas.isEmpty()) {
            return List.of();
        }
        List<Long> idsReceta = recetas.stream().map(Receta::getId).toList();
        Map<Long, List<Medida>> medidasPorReceta = medidaRepository
                .findByRecetaIdInOrderByIdAsc(idsReceta).stream()
                .collect(Collectors.groupingBy(medida -> medida.getReceta().getId()));
        List<Lente> lentes = lenteRepository.findByRecetaIdInOrderByIdAsc(idsReceta);
        Map<Long, List<LenteResponseDTO>> lentesPorReceta = new HashMap<>();
        if (!lentes.isEmpty()) {
            for (Lente lente : lentes) {
                LenteResponseDTO detalle = RecetaMapper.toLenteResponse(lente,
                        new ArrayList<>(lente.getMedidas()),
                        new ArrayList<>(lente.getTratamientosCristal())
                );
                lentesPorReceta.computeIfAbsent(lente.getReceta().getId(), id -> new ArrayList<>()).add(detalle);
            }
        }
        return recetas.stream().map(receta -> RecetaMapper.toRecetaResponse(receta,
                medidasPorReceta.getOrDefault(receta.getId(), List.of()),
                lentesPorReceta.getOrDefault(receta.getId(), List.of())
            )
        ).toList();
    }

    private List<Medida> seleccionarMedidas(Long idReceta, List<Long> ids) {
        List<Medida> medidas;
        if (ids == null || ids.isEmpty()) {
            medidas = medidaRepository.findByRecetaIdOrderByIdAsc(idReceta);
        } else {
            if (new HashSet<>(ids).size() != ids.size()) {
                throw new ReglaNegocioException("No se deben repetir IDs de medidas");
            }
            medidas = medidaRepository.findByIdInAndRecetaId(ids, idReceta);
            if (medidas.size() != ids.size()) {
                throw new ReglaNegocioException("Todas las medidas deben existir y pertenecer a la receta");
            }
            medidas.sort(Comparator.comparing(Medida::getId));
        }
        if (medidas.isEmpty()) {
            throw new ReglaNegocioException("La receta debe tener medidas para configurar un lente");
        }
        if (medidas.stream().anyMatch(medida -> medida.getMedidaOjoDerecha() == null
                || medida.getMedidaOjoIzquierda() == null
            )
        ) {
            throw new ReglaNegocioException("Cada medida seleccionada debe contener ambos ojos");
        }
        return medidas;
    }

    private List<TratamientoCristal> seleccionarTratamientos(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new ReglaNegocioException("No se deben repetir IDs de tratamientos");
        }
        List<TratamientoCristal> tratamientos = tratamientoCristalRepository.findAllById(ids);
        if (tratamientos.size() != ids.size()) {
            throw new RecursosNoEncontradosException("Uno o más tratamientos de cristal no existen");
        }
        tratamientos.sort(Comparator.comparing(TratamientoCristal::getId));
        return tratamientos;
    }

    private RecetaResponseDTO registrar(RecetaRequestDTO request, String username, int esExterno) {
        Empleado empleado = obtenerEmpleadoActual(username, request.idEmpleado());
        validarDatosNuevos(request.medidas());
        OpticaValidator.validarMedidas(request.medidas());

        // Shared lock with patient activation changes.
        personaRepository.findByIdForUpdate(request.idPaciente())
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
        Paciente paciente = pacienteRepository.findByIdWithDetails(request.idPaciente())
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
        if (!Boolean.TRUE.equals(paciente.getActivo())) {
            throw new ReglaNegocioException("El paciente debe estar activo para registrar una receta");
        }

        Receta receta = new Receta(null, request.fechaRegistro() == null ? null : request.fechaRegistro().withNano(0), esExterno, paciente, empleado);
        recetaRepository.saveAndFlush(receta);
        List<Medida> medidas = new ArrayList<>();
        for (MedidaRequestDTO medida : request.medidas()) {
            MedidaOjo derecha = medidaOjoRepository.saveAndFlush(RecetaMapper.toMedidaOjo(medida.ojoDerecho()));
            MedidaOjo izquierda = medidaOjoRepository.saveAndFlush(RecetaMapper.toMedidaOjo(medida.ojoIzquierdo()));
            medidas.add(medidaRepository.saveAndFlush(RecetaMapper.toMedida(medida, receta, derecha, izquierda)));
        }
        return RecetaMapper.toRecetaResponse(receta, medidas);
    }

    private Empleado obtenerEmpleadoActual(String username, Long idSolicitado) {
        if (username == null || username.isBlank()) {
            throw new AccessDeniedException("Se requiere un usuario autenticado");
        }
        Usuario usuario = usuarioRepository.findByNombre(username)
                .filter(actual -> Boolean.TRUE.equals(actual.getActivo()))
                .orElseThrow(() -> new AccessDeniedException("Usuario no habilitado"));
        Empleado empleado = empleadoRepository.findByUsuarioId(usuario.getId())
                .filter(actual -> Boolean.TRUE.equals(actual.getActivo()))
                .orElseThrow(() -> new AccessDeniedException("Se requiere un empleado activo vinculado al usuario"));
        if (!empleado.getId().equals(idSolicitado)) {
            throw new AccessDeniedException("El empleado debe corresponder al usuario autenticado");
        }
        return empleado;
    }

    private void validarDatosNuevos(List<MedidaRequestDTO> medidas) {
        for (MedidaRequestDTO medida : medidas) {
            if (medida.idReceta() != null || medida.idEnvioLaboratorio() != null
                    || medida.ojoDerecho().id() != null
                    || medida.ojoIzquierdo().id() != null
            ) {
                throw new ReglaNegocioException("Las medidas nuevas no deben incluir IDs de receta, ojos o envío");
            }
        }
    }

}
