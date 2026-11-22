package pe.edu.upeu.geovision_backend.dto.inicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ResumenInicioDTO {
    private ResumenInicioDTO() {}

    public record Ventas(BigDecimal total, long cantidad) {}
    public record Ordenes(long enProceso, long listasParaEntrega) {}
    public record Atencion(Long idReceta, Long idPaciente, String paciente, LocalDateTime fechaRegistro) {}
    public record Atenciones(long pacientes, long primerasVeces, long totalConsultas, List<Atencion> recientes) {}
}
