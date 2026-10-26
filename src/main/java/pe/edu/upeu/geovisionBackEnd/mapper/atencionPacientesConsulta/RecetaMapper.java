package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Receta;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class RecetaMapper {
    private RecetaMapper() {}

    public static RecetaResponseDTO convertToResponse(Receta entity) {
        var dto = new RecetaResponseDTO();
        dto.setIdReceta(entity.getIdReceta());
        dto.setFechaRegistro(entity.getFechaRegistro());
        dto.setFechaDeEntrega(entity.getFechaDeEntrega());
        dto.setEsExterno(entity.getEsExterno());
        dto.setObservaciones(entity.getObservaciones());
        dto.setIdExamen(entity.getExamen().getIdExamen());
        return dto;
    }

    public static void updateEntity(RecetaRequestDTO request, Receta entity) {
        entity.setObservaciones(normalizar(request.getObservaciones()));
    }
}

