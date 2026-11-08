package pe.edu.upeu.geovision_backend.controller.atencion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.service.atencion.CatalogoOpticoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogos-opticos")
@PreAuthorize("hasAnyAuthority('PACIENTES_LEER', 'EXAMENES_REGISTRAR', 'RECETAS_REGISTRAR')")
public class CatalogoOpticoController {

    private final CatalogoOpticoService catalogoOpticoService;

    public CatalogoOpticoController(CatalogoOpticoService catalogoOpticoService) {
        this.catalogoOpticoService = catalogoOpticoService;
    }

    @GetMapping("/materiales-cristal")
    public ResponseEntity<List<MaterialCristalDTO>> listarMaterialesCristal() {
        return ResponseEntity.ok(catalogoOpticoService.listarMaterialesCristal());
    }

    @GetMapping("/tratamientos-cristal")
    public ResponseEntity<List<TratamientoCristalDTO>> listarTratamientosCristal() {
        return ResponseEntity.ok(catalogoOpticoService.listarTratamientosCristal());
    }

    @GetMapping("/monturas")
    public ResponseEntity<Page<MonturaDTO>> listarMonturasDisponibles(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        if ((long) page * size > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La página solicitada excede el límite permitido");
        }
        return ResponseEntity.ok(catalogoOpticoService.listarMonturasDisponibles(PageRequest.of(page, size)));
    }
}
