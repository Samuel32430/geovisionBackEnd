package pe.edu.upeu.geovision_backend.service.atencion.impl;

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
import pe.edu.upeu.geovision_backend.entity.atencion.LenteMedida;
import pe.edu.upeu.geovision_backend.entity.atencion.LenteTratamientoCristal;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;
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
import pe.edu.upeu.geovision_backend.repository.atencion.LenteMedidaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteTratamientoCristalRepository;
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
@Transactional
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final MedidaRepository medidaRepository;
    private final MedidaOjoRepository medidaOjoRepository;
    private final PacienteRepository pacienteRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RecetaMapper recetaMapper;
    private final OpticaValidator opticaValidator;
    private final LenteRepository lenteRepository;
    private final LenteMedidaRepository lenteMedidaRepository;
    private final LenteTratamientoCristalRepository lenteTratamientoCristalRepository;
    private final MaterialCristalRepository materialCristalRepository;
    private final TratamientoCristalRepository tratamientoCristalRepository;
    private final MonturaRepository monturaRepository;
    private final VentaRepository ventaRepository;

    public RecetaServiceImpl(RecetaRepository recetaRepository, MedidaRepository medidaRepository,
                             MedidaOjoRepository medidaOjoRepository, PacienteRepository pacienteRepository,
                             PersonaRepository personaRepository, UsuarioRepository usuarioRepository,
                             EmpleadoRepository empleadoRepository, RecetaMapper recetaMapper,
                             OpticaValidator opticaValidator, LenteRepository lenteRepository,
                             LenteMedidaRepository lenteMedidaRepository,
                             LenteTratamientoCristalRepository lenteTratamientoCristalRepository,
                             MaterialCristalRepository materialCristalRepository,
                             TratamientoCristalRepository tratamientoCristalRepository,
                             MonturaRepository monturaRepository, VentaRepository ventaRepository) {
        this.recetaRepository = recetaRepository;
        this.medidaRepository = medidaRepository;
        this.medidaOjoRepository = medidaOjoRepository;
        this.pacienteRepository = pacienteRepository;
        this.personaRepository = personaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.recetaMapper = recetaMapper;
        this.opticaValidator = opticaValidator;
        this.lenteRepository = lenteRepository;
        this.lenteMedidaRepository = lenteMedidaRepository;
        this.lenteTratamientoCristalRepository = lenteTratamientoCristalRepository;
        this.materialCristalRepository = materialCristalRepository;
        this.tratamientoCristalRepository = tratamientoCristalRepository;
        this.monturaRepository = monturaRepository;
        this.ventaRepository = ventaRepository;
    }

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
        return mapearRecetas(recetaRepository.findByPacienteIdPersonaOrderByFechaRegistroDescIdRecetaDesc(idPaciente));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecetaResponseDTO> listarRecetasPaginadas(Long idPaciente, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged() || pageable.getPageSize() > 100
                || pageable.getOffset() > Integer.MAX_VALUE) {
            throw new ReglaNegocioException("La consulta debe estar paginada con un máximo de 100 recetas");
        }
        verificarPaciente(idPaciente);
        // El orden clínico es siempre cronológico y estable, independientemente del orden recibido.
        Pageable pagina = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "fechaRegistro", "idReceta"));
        Page<Receta> recetas = recetaRepository.findByPacienteIdPersona(idPaciente, pagina);
        return new PageImpl<>(mapearRecetas(recetas.getContent()), pagina, recetas.getTotalElements());
    }

    private void verificarPaciente(Long idPaciente) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new RecursosNoEncontradosException("Paciente no encontrado");
        }
    }

    private List<RecetaResponseDTO> mapearRecetas(List<Receta> recetas) {
        if (recetas.isEmpty()) {
            return List.of();
        }
        List<Long> idsReceta = recetas.stream().map(Receta::getIdReceta).toList();
        Map<Long, List<Medida>> medidasPorReceta = medidaRepository
                .findByRecetaIdRecetaInOrderByIdMedidaAsc(idsReceta).stream()
                .collect(Collectors.groupingBy(medida -> medida.getReceta().getIdReceta()));
        List<Lente> lentes = lenteRepository.findByRecetaIdRecetaInOrderByIdLenteAsc(idsReceta);
        Map<Long, List<LenteResponseDTO>> lentesPorReceta = new HashMap<>();
        if (!lentes.isEmpty()) {
            List<Long> idsLente = lentes.stream().map(Lente::getIdLente).toList();
            Map<Long, List<Medida>> medidasPorLente = lenteMedidaRepository
                    .findByLenteIdLenteInOrderByMedidaIdMedidaAsc(idsLente).stream()
                    .collect(Collectors.groupingBy(vinculo -> vinculo.getLente().getIdLente(),
                            Collectors.mapping(LenteMedida::getMedida, Collectors.toList())));
            Map<Long, List<TratamientoCristal>> tratamientosPorLente = lenteTratamientoCristalRepository
                    .findByLenteIdLenteInOrderByTratamientoCristalIdTratamientoCristalAsc(idsLente).stream()
                    .collect(Collectors.groupingBy(vinculo -> vinculo.getLente().getIdLente(),
                            Collectors.mapping(LenteTratamientoCristal::getTratamientoCristal, Collectors.toList())));
            for (Lente lente : lentes) {
                LenteResponseDTO detalle = recetaMapper.toLenteResponse(lente,
                        medidasPorLente.getOrDefault(lente.getIdLente(), List.of()),
                        tratamientosPorLente.getOrDefault(lente.getIdLente(), List.of()));
                lentesPorReceta.computeIfAbsent(lente.getReceta().getIdReceta(), id -> new ArrayList<>()).add(detalle);
            }
        }
        return recetas.stream().map(receta -> recetaMapper.toRecetaResponse(receta,
                medidasPorReceta.getOrDefault(receta.getIdReceta(), List.of()),
                lentesPorReceta.getOrDefault(receta.getIdReceta(), List.of()))).toList();
    }

    @Override
    public LenteResponseDTO configurarLente(Long idReceta, LenteRequestDTO request) {
        if (!request.getIdReceta().equals(idReceta)) {
            throw new ReglaNegocioException("La receta del cuerpo debe coincidir con la receta de la ruta");
        }
        Receta receta = recetaRepository.findByIdForUpdate(idReceta)
                .orElseThrow(() -> new RecursosNoEncontradosException("Receta no encontrada"));
        // Ventas debe tomar este mismo bloqueo antes de asociar una venta a la receta.
        if (ventaRepository.existsByRecetaIdReceta(idReceta)) {
            throw new ReglaNegocioException("No se puede configurar una receta que ya está vinculada a una venta");
        }
        MaterialCristal material = materialCristalRepository.findById(request.getIdMaterialCristal())
                .orElseThrow(() -> new RecursosNoEncontradosException("Material de cristal no encontrado"));
        Montura montura = null;
        if (request.getIdProducto() != null) {
            montura = monturaRepository.findById(request.getIdProducto())
                    .orElseThrow(() -> new RecursosNoEncontradosException("Montura no encontrada"));
            if (montura.getProducto().getEstado() != EstadoGeneralEnum.ACTIVO
                    || montura.getProducto().getStock() <= 0) {
                throw new ReglaNegocioException("La montura debe estar activa y tener stock disponible");
            }
        }
        List<Medida> medidas = seleccionarMedidas(idReceta, request.getMedidasIds());
        List<TratamientoCristal> tratamientos = seleccionarTratamientos(request.getTratamientosCristalIds());
        Lente lente = lenteRepository.saveAndFlush(Lente.builder().receta(receta).materialCristal(material)
                .montura(montura).subTotal(request.getSubTotal()).build());
        lenteMedidaRepository.saveAllAndFlush(medidas.stream()
                .map(medida -> LenteMedida.builder().lente(lente).medida(medida).build()).toList());
        lenteTratamientoCristalRepository.saveAllAndFlush(tratamientos.stream()
                .map(tratamiento -> LenteTratamientoCristal.builder().lente(lente)
                        .tratamientoCristal(tratamiento).build()).toList());
        return recetaMapper.toLenteResponse(lente, medidas, tratamientos);
    }

    private List<Medida> seleccionarMedidas(Long idReceta, List<Long> ids) {
        List<Medida> medidas;
        if (ids == null || ids.isEmpty()) {
            medidas = medidaRepository.findByRecetaIdRecetaOrderByIdMedidaAsc(idReceta);
        } else {
            if (new HashSet<>(ids).size() != ids.size()) {
                throw new ReglaNegocioException("No se deben repetir IDs de medidas");
            }
            medidas = medidaRepository.findByIdMedidaInAndRecetaIdReceta(ids, idReceta);
            if (medidas.size() != ids.size()) {
                throw new ReglaNegocioException("Todas las medidas deben existir y pertenecer a la receta");
            }
            medidas.sort(Comparator.comparing(Medida::getIdMedida));
        }
        if (medidas.isEmpty()) {
            throw new ReglaNegocioException("La receta debe tener medidas para configurar un lente");
        }
        if (medidas.stream().anyMatch(medida -> medida.getMedidaOjoDerecha() == null
                || medida.getMedidaOjoIzquierda() == null)) {
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
        tratamientos.sort(Comparator.comparing(TratamientoCristal::getIdTratamientoCristal));
        return tratamientos;
    }

    @Override
    public RecetaResponseDTO registrarExamenVisual(RecetaRequestDTO request, String usernameAuditoria) {
        if (request.getEsExterno() == 1) {
            throw new ReglaNegocioException("Utilice el endpoint de receta externa para una prescripción externa");
        }
        return registrar(request, usernameAuditoria, 0);
    }

    @Override
    public RecetaResponseDTO registrarRecetaExterna(RecetaRequestDTO request, String usernameAuditoria) {
        return registrar(request, usernameAuditoria, 1);
    }

    private RecetaResponseDTO registrar(RecetaRequestDTO request, String username, int esExterno) {
        Empleado empleado = obtenerEmpleadoActual(username, request.getIdEmpleado());
        validarDatosNuevos(request.getMedidas());
        opticaValidator.validarMedidas(request.getMedidas());

        // Comparte el bloqueo con la gestión del paciente para serializar su desactivación.
        personaRepository.findByIdForUpdate(request.getIdPaciente())
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
        Paciente paciente = pacienteRepository.findByIdWithDetails(request.getIdPaciente())
                .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado"));
        if (paciente.getEstado() != EstadoGeneralEnum.ACTIVO) {
            throw new ReglaNegocioException("El paciente debe estar activo para registrar una receta");
        }

        Receta receta = Receta.builder().paciente(paciente).empleado(empleado).esExterno(esExterno)
                .fechaRegistro(request.getFechaRegistro() == null ? null : request.getFechaRegistro().withNano(0))
                .build();
        recetaRepository.saveAndFlush(receta);
        List<Medida> medidas = new ArrayList<>();
        for (MedidaRequestDTO medida : request.getMedidas()) {
            MedidaOjo derecha = medidaOjoRepository.saveAndFlush(recetaMapper.toMedidaOjo(medida.getOjoDerecho()));
            MedidaOjo izquierda = medidaOjoRepository.saveAndFlush(recetaMapper.toMedidaOjo(medida.getOjoIzquierdo()));
            medidas.add(medidaRepository.saveAndFlush(recetaMapper.toMedida(medida, receta, derecha, izquierda)));
        }
        return recetaMapper.toRecetaResponse(receta, medidas);
    }

    private Empleado obtenerEmpleadoActual(String username, Long idSolicitado) {
        if (username == null || username.isBlank()) {
            throw new AccessDeniedException("Se requiere un usuario autenticado");
        }
        Usuario usuario = usuarioRepository.findByNombre(username)
                .filter(actual -> actual.getEstado() == EstadoGeneralEnum.ACTIVO)
                .orElseThrow(() -> new AccessDeniedException("Usuario no habilitado"));
        Empleado empleado = empleadoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .filter(actual -> actual.getEstado() == EstadoGeneralEnum.ACTIVO)
                .orElseThrow(() -> new AccessDeniedException("Se requiere un empleado activo vinculado al usuario"));
        if (!empleado.getIdPersona().equals(idSolicitado)) {
            throw new AccessDeniedException("El empleado debe corresponder al usuario autenticado");
        }
        return empleado;
    }

    private void validarDatosNuevos(List<MedidaRequestDTO> medidas) {
        for (MedidaRequestDTO medida : medidas) {
            if (medida.getIdReceta() != null || medida.getIdEnvioLaboratorio() != null
                    || medida.getOjoDerecho().getIdMedidaOjo() != null
                    || medida.getOjoIzquierdo().getIdMedidaOjo() != null) {
                throw new ReglaNegocioException("Las medidas nuevas no deben incluir IDs de receta, ojos o envío");
            }
        }
    }
}
