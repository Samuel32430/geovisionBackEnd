package pe.edu.upeu.geovisionBackEnd.dto;
import jakarta.validation.constraints.Size;
public record RecepcionLaboratorioRequestDTO(@Size(max=500) String observaciones) {}
