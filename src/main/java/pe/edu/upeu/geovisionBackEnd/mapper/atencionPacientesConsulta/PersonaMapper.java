package pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta;

import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Persona;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;

public final class PersonaMapper {
    private PersonaMapper() {}

    public static PersonaResponseDTO convertToResponse(Persona entity) {
        var dto = new PersonaResponseDTO();
        dto.setIdPersona(entity.getIdPersona());
        dto.setNombres(entity.getNombres());
        dto.setApellidos(entity.getApellidos());
        dto.setTelefono(entity.getTelefono());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setSexo(entity.getSexo());
        dto.setDireccion(entity.getDireccion());

        return dto;
    }

    public static void updateEntity(PersonaRequestDTO request, Persona entity) {
        entity.setNombres(normalizar(request.getNombres()));
        entity.setApellidos(normalizar(request.getApellidos()));
        entity.setTelefono(normalizar(request.getTelefono()));
        entity.setFechaNacimiento(request.getFechaNacimiento());
        entity.setSexo(request.getSexo());
        entity.setDireccion(normalizar(request.getDireccion()));
    }
}

