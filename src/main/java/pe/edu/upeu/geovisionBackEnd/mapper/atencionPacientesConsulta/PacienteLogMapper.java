package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.PacienteLog;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class PacienteLogMapper {
    private PacienteLogMapper() {}

    public static PacienteLogResponseDTO convertToResponse(PacienteLog entity) {
        var dto = new PacienteLogResponseDTO();
        dto.setIdPacienteLog(entity.getIdPacienteLog());
        dto.setFechaLog(entity.getFechaLog());
        dto.setIdPaciente(entity.getPaciente().getIdPersona());
        return dto;
    }
}

