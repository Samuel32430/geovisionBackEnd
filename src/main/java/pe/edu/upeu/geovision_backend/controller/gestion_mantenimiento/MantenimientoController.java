package pe.edu.upeu.geovision_backend.controller.gestion_mantenimiento;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.PessimisticLockingFailureException;
import pe.edu.upeu.geovision_backend.exception.dto.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.CategoriaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.LaboratorioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.PaginaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.ServicioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.service.gestion_mantenimiento.MantenimientoService;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/mantenimiento")
@PreAuthorize("hasAuthority('MANTENIMIENTO_GESTIONAR')")
@RequiredArgsConstructor
public class MantenimientoController {

    // >>>> DEPENDENCIES
    private final MantenimientoService service;

    // >>>> QUERIES
    @GetMapping("/categorias")
    public PaginaResponseDTO<CategoriaDTO> listarCategoria(
        @RequestParam(required = false) String termino,
        @RequestParam(required = false) Integer estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var resultado = service.listarCategoria(termino, validarEstado(estado), pagina(page, size, termino, "id"));
        return new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(), resultado.isLast(), resultado.isEmpty()
        );
    }

    @GetMapping("/categorias/{id}")
    public CategoriaDTO obtenerCategoria(@PathVariable Long id) {
        return service.obtenerCategoria(validarId(id));
    }

    @GetMapping("/materiales-cristal")
    public PaginaResponseDTO<MaterialCristalDTO> listarMaterialCristal(
        @RequestParam(required = false) String termino,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var resultado = service.listarMaterialCristal(termino, pagina(page, size, termino, "id"));
        return new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(), resultado.isLast(), resultado.isEmpty()
        );
    }

    @GetMapping("/materiales-cristal/{id}")
    public MaterialCristalDTO obtenerMaterialCristal(@PathVariable Long id) {
        return service.obtenerMaterialCristal(validarId(id));
    }

    @GetMapping("/tratamientos-cristal")
    public PaginaResponseDTO<TratamientoCristalDTO> listarTratamientoCristal(
        @RequestParam(required = false) String termino,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var resultado = service.listarTratamientoCristal(termino, pagina(page, size, termino, "id"));
        return new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(), resultado.isLast(), resultado.isEmpty()
        );
    }

    @GetMapping("/tratamientos-cristal/{id}")
    public TratamientoCristalDTO obtenerTratamientoCristal(@PathVariable Long id) {
        return service.obtenerTratamientoCristal(validarId(id));
    }

    @GetMapping("/servicios")
    public PaginaResponseDTO<ServicioDTO> listarServicio(
        @RequestParam(required = false) String termino,
        @RequestParam(required = false) Integer estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var resultado = service.listarServicio(termino, validarEstado(estado), pagina(page, size, termino, "id"));
        return new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(), resultado.isLast(), resultado.isEmpty()
        );
    }

    @GetMapping("/servicios/{id}")
    public ServicioDTO obtenerServicio(@PathVariable Long id) {
        return service.obtenerServicio(validarId(id));
    }

    @GetMapping("/laboratorios")
    public PaginaResponseDTO<LaboratorioDTO> listarLaboratorio(
        @RequestParam(required = false) String termino,
        @RequestParam(required = false) Integer estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var resultado = service.listarLaboratorio(termino, validarEstado(estado), pagina(page, size, termino, "id"));
        return new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(), resultado.isLast(), resultado.isEmpty()
        );
    }

    @GetMapping("/laboratorios/{id}")
    public LaboratorioDTO obtenerLaboratorio(@PathVariable Long id) {
        return service.obtenerLaboratorio(validarId(id));
    }

    // >>>> CREATION
    @PostMapping("/categorias")
    public ResponseEntity<CategoriaDTO> registrarCategoria(@Valid @RequestBody CategoriaDTO request) {
        var response = service.registrarCategoria(request);
        return ResponseEntity.created(URI.create("/api/v1/mantenimiento/categorias/" + response.id())).body(response);
    }

    @PostMapping("/materiales-cristal")
    public ResponseEntity<MaterialCristalDTO> registrarMaterialCristal(@Valid @RequestBody MaterialCristalDTO request) {
        var response = service.registrarMaterialCristal(request);
        return ResponseEntity.created(URI.create("/api/v1/mantenimiento/materiales-cristal/" + response.id())).body(response);
    }

    @PostMapping("/tratamientos-cristal")
    public ResponseEntity<TratamientoCristalDTO> registrarTratamientoCristal(@Valid @RequestBody TratamientoCristalDTO request) {
        var response = service.registrarTratamientoCristal(request);
        return ResponseEntity.created(URI.create("/api/v1/mantenimiento/tratamientos-cristal/" + response.id())).body(response);
    }

    @PostMapping("/servicios")
    public ResponseEntity<ServicioDTO> registrarServicio(@Valid @RequestBody ServicioDTO request) {
        var response = service.registrarServicio(request);
        return ResponseEntity.created(URI.create("/api/v1/mantenimiento/servicios/" + response.id())).body(response);
    }

    @PostMapping("/laboratorios")
    public ResponseEntity<LaboratorioDTO> registrarLaboratorio(@Valid @RequestBody LaboratorioDTO request) {
        var response = service.registrarLaboratorio(request);
        return ResponseEntity.created(URI.create("/api/v1/mantenimiento/laboratorios/" + response.id())).body(response);
    }

    // >>>> UPDATES
    @PutMapping("/categorias/{id}")
    public CategoriaDTO actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO request) {
        return service.actualizarCategoria(validarId(id), request);
    }

    @PutMapping("/materiales-cristal/{id}")
    public MaterialCristalDTO actualizarMaterialCristal(@PathVariable Long id, @Valid @RequestBody MaterialCristalDTO request) {
        return service.actualizarMaterialCristal(validarId(id), request);
    }

    @PutMapping("/tratamientos-cristal/{id}")
    public TratamientoCristalDTO actualizarTratamientoCristal(@PathVariable Long id, @Valid @RequestBody TratamientoCristalDTO request) {
        return service.actualizarTratamientoCristal(validarId(id), request);
    }

    @PutMapping("/servicios/{id}")
    public ServicioDTO actualizarServicio(@PathVariable Long id, @Valid @RequestBody ServicioDTO request) {
        return service.actualizarServicio(validarId(id), request);
    }

    @PutMapping("/laboratorios/{id}")
    public LaboratorioDTO actualizarLaboratorio(@PathVariable Long id, @Valid @RequestBody LaboratorioDTO request) {
        return service.actualizarLaboratorio(validarId(id), request);
    }

    // >>>> OPERATIONS
    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDTO> bloqueoOcupado(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(
                LocalDateTime.now(), 409, "Conflict",
                "El catálogo está siendo utilizado. Intente nuevamente.",
                request.getRequestURI(), null
            )
        );
    }

    // >>>> HELPERS
    private Long validarId(Long id) {
        if (id == null || id < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser positivo");
        return id;
    }

    private Integer validarEstado(Integer estado) {
        if (estado != null && estado != 0 && estado != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado debe ser 0 o 1");
        }
        return estado;
    }

    private Pageable pagina(int page, int size, String termino, String campoId) {
        if (page < 0 || size < 1 || size > 100 || (long) page * size > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginación inválida: page >= 0 y size entre 1 y 100");
        }
        if (termino != null && termino.length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El término no debe superar los 200 caracteres");
        }
        return PageRequest.of(page, size, Sort.by("nombre").ascending().and(Sort.by(campoId).ascending()));
    }

}
