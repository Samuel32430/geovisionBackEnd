package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Paciente;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class PacienteMapper {
    private PacienteMapper() {}

    public static PacienteResponseDTO convertToResponse(Paciente entity) {
        var dto = new PacienteResponseDTO();
        dto.setIdPersona(entity.getIdPersona());
        dto.setEstado(entity.getEstado());
        dto.setFechaRegistro(entity.getFechaRegistro());
        dto.setNombres(entity.getPersona().getNombres());
        dto.setApellidos(entity.getPersona().getApellidos());
        dto.setTelefono(entity.getPersona().getTelefono());
        return dto;
    }
}

