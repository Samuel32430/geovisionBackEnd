package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Documento;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class DocumentoMapper {
    private DocumentoMapper() {}

    public static DocumentoResponseDTO convertToResponse(Documento entity) {
        var dto = new DocumentoResponseDTO();
        dto.setIdDocumento(entity.getIdDocumento());
        dto.setNumero(entity.getNumero());
        dto.setIdPersona(entity.getPersona().getIdPersona());
        dto.setIdTipoDocumento(entity.getTipoDocumento().getIdTipoDocumento());
        return dto;
    }

    public static void updateEntity(DocumentoRequestDTO request, Documento entity) {
        entity.setNumero(normalizar(request.getNumero()));
    }
}

