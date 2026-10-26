package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.MedidaOjo;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class MedidaOjoMapper {
    private MedidaOjoMapper() {}

    public static MedidaOjoResponseDTO convertToResponse(MedidaOjo entity) {
        var dto = new MedidaOjoResponseDTO();
        dto.setIdMedidaOjo(entity.getIdMedidaOjo());
        dto.setAgudezaVisual(entity.getAgudezaVisual());
        dto.setEsfera(entity.getEsfera());
        dto.setCilindro(entity.getCilindro());
        dto.setEje(entity.getEje());
        dto.setAdicion(entity.getAdicion());
        dto.setDistanciaPupilarMm(entity.getDistanciaPupilarMm());
        dto.setPrisma(entity.getPrisma());
        dto.setBasePrisma(entity.getBasePrisma());
        dto.setIdMedida(entity.getMedida().getIdMedida());
        dto.setIdTipoOjo(entity.getTipoOjo().getIdTipoOjo());
        return dto;
    }

    public static void updateEntity(MedidaOjoRequestDTO request, MedidaOjo entity) {
        entity.setAgudezaVisual(request.getAgudezaVisual());
        entity.setEsfera(request.getEsfera());
        entity.setCilindro(request.getCilindro());
        entity.setEje(request.getEje());
        entity.setAdicion(request.getAdicion());
        entity.setDistanciaPupilarMm(request.getDistanciaPupilarMm());
        entity.setPrisma(request.getPrisma());
        entity.setBasePrisma(normalizar(request.getBasePrisma()));
    }
}

