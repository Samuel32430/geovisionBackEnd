package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Medida;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class MedidaMapper {
    private MedidaMapper() {}

    public static MedidaResponseDTO convertToResponse(Medida entity) {
        var dto = new MedidaResponseDTO();
        dto.setIdMedida(entity.getIdMedida());
        dto.setObservaciones(entity.getObservaciones());
        dto.setIdExamen(entity.getExamen().getIdExamen());
        dto.setIdTipoMedida(entity.getTipoMedida().getIdTipoMedida());
        return dto;
    }

    public static void updateEntity(MedidaRequestDTO request, Medida entity) {
        entity.setObservaciones(normalizar(request.getObservaciones()));
    }
}

