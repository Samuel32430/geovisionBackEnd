package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.EmpleadoRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.EmpleadoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.Cargo;

import java.time.LocalDate;

@Component
public class EmpleadoMapper {

    public Empleado toEntity(EmpleadoRequestDTO dto, Persona persona, Cargo cargo, Usuario usuario) {
        if (dto == null) {
            return null;
        }
        return Empleado.builder()
                .idPersona(dto.getIdPersona())
                .persona(persona)
                .cargo(cargo)
                .usuario(usuario)
                .fechaContrato(dto.getFechaContrato() != null ? dto.getFechaContrato() : LocalDate.now())
                .estado(dto.getEstado() != null ? dto.getEstado() : 1)
                .build();
    }

    public EmpleadoResponseDTO toResponseDTO(Empleado entity) {
        if (entity == null) {
            return null;
        }
        String nombres = entity.getPersona() != null ? entity.getPersona().getNombres() : null;
        String apellidos = entity.getPersona() != null ? entity.getPersona().getApellidos() : null;
        Long idCargo = entity.getCargo() != null ? entity.getCargo().getIdCargo() : null;
        String nombreCargo = entity.getCargo() != null ? entity.getCargo().getNombre() : null;
        Long idUsuario = entity.getUsuario() != null ? entity.getUsuario().getIdUsuario() : null;
        String nombreUsuario = entity.getUsuario() != null ? entity.getUsuario().getNombre() : null;

        return EmpleadoResponseDTO.builder()
                .idPersona(entity.getIdPersona())
                .nombres(nombres)
                .apellidos(apellidos)
                .idCargo(idCargo)
                .nombreCargo(nombreCargo)
                .fechaContrato(entity.getFechaContrato())
                .estado(entity.getEstado())
                .idUsuario(idUsuario)
                .nombreUsuario(nombreUsuario)
                .build();
    }
}
