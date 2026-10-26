package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Examen;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class ExamenMapper {
    private ExamenMapper() {}

    public static ExamenResponseDTO convertToResponse(Examen entity) {
        var dto = new ExamenResponseDTO();
        dto.setIdExamen(entity.getIdExamen());
        dto.setFechaRegistro(entity.getFechaRegistro());
        dto.setEsExterno(entity.getEsExterno());
        dto.setProfesionalExterno(entity.getProfesionalExterno());
        dto.setDocumentoExterno(entity.getDocumentoExterno());
        dto.setObservaciones(entity.getObservaciones());
        dto.setIdPaciente(entity.getPaciente().getIdPersona());
        return dto;
    }

    public static void updateEntity(ExamenRequestDTO request, Examen entity) {
        entity.setEsExterno(request.getEsExterno());
        entity.setProfesionalExterno(normalizar(request.getProfesionalExterno()));
        entity.setDocumentoExterno(normalizar(request.getDocumentoExterno()));
        entity.setObservaciones(normalizar(request.getObservaciones()));
    }
}

