package pe.edu.upeu.geovision_backend.dto.orden_trabajo;
import jakarta.validation.constraints.Size;
public record RecepcionLaboratorioRequestDTO(@Size(max=500) String observaciones) {}
