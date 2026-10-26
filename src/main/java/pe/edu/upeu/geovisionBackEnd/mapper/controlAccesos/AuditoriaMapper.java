package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.AuditoriaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.AuditoriaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Auditoria;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;

import java.time.LocalDateTime;

@Component
public class AuditoriaMapper {

    public Auditoria toEntity(AuditoriaRequestDTO dto, Usuario usuario) {
        if (dto == null) {
            return null;
        }
        return Auditoria.builder()
                .entidad(dto.getEntidad())
                .idEntidad(dto.getIdEntidad())
                .accion(dto.getAccion())
                .datosAnteriores(dto.getDatosAnteriores())
                .datosNuevos(dto.getDatosNuevos())
                .usuario(usuario)
                .fechaLog(LocalDateTime.now())
                .build();
    }

    public AuditoriaResponseDTO toResponseDTO(Auditoria entity) {
        if (entity == null) {
            return null;
        }
        return AuditoriaResponseDTO.builder()
                .idAuditoria(entity.getIdAuditoria())
                .fechaLog(entity.getFechaLog())
                .entidad(entity.getEntidad())
                .idEntidad(entity.getIdEntidad())
                .accion(entity.getAccion())
                .datosAnteriores(entity.getDatosAnteriores())
                .datosNuevos(entity.getDatosNuevos())
                .idUsuario(entity.getUsuario() != null ? entity.getUsuario().getIdUsuario() : null)
                .nombreUsuario(entity.getUsuario() != null ? entity.getUsuario().getNombre() : null)
                .build();
    }
}
