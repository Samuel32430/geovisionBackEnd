package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.CatalogoPacienteService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/atencion/catalogos")
public class CatalogoPacienteController {
    private final CatalogoPacienteService service;

    @GetMapping("/tipos-documento")
    public ResponseEntity<List<CatalogoPacienteResponseDTO>> tiposDocumento() {
        return ResponseEntity.ok(service.tiposDocumento());
    }

    @GetMapping("/tipos-medida")
    public ResponseEntity<List<CatalogoPacienteResponseDTO>> tiposMedida() {
        return ResponseEntity.ok(service.tiposMedida());
    }

    @GetMapping("/tipos-ojo")
    public ResponseEntity<List<CatalogoPacienteResponseDTO>> tiposOjo() {
        return ResponseEntity.ok(service.tiposOjo());
    }
}

