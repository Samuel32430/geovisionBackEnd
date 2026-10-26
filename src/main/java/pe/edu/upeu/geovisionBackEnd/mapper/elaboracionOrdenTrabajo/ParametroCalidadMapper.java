package pe.edu.upeu.geovisionBackEnd.mapper.elaboracionOrdenTrabajo;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo.ParametroCalidadRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo.ParametroCalidadResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.ControlCalidad;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.ParametroCalidad;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.TipoParametroCalidad;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.MedidaOjo;

/** Las referencias se resuelven desde repositorios dentro de una transaccion. */
@Component
public class ParametroCalidadMapper {

    public ParametroCalidad toEntity(ParametroCalidadRequestDTO dto, ControlCalidad control,
                                    TipoParametroCalidad criterio, MedidaOjo ojo) {
        if (dto == null || control == null || criterio == null || ojo == null
                || dto.getIdControlCalidad() == null || dto.getIdTipoParametroCalidad() == null
                || dto.getIdMedidaOjo() == null
                || !dto.getIdControlCalidad().equals(control.getIdControlCalidad())
                || !dto.getIdTipoParametroCalidad().equals(criterio.getIdTipoParametroCalidad())
                || !dto.getIdMedidaOjo().equals(ojo.getIdMedidaOjo())) {
            throw new IllegalArgumentException("Las referencias no corresponden a los identificadores solicitados");
        }
        return ParametroCalidad.crear(control, criterio, ojo, dto.getValorObtenido());
    }

    public ParametroCalidadResponseDTO toResponseDTO(ParametroCalidad entity) {
        if (entity == null) {
            return null;
        }
        return new ParametroCalidadResponseDTO(entity.getIdParametroCalidad(),
                entity.getTolerancia(), entity.getValorEsperado(), entity.getValorObtenido(),
                entity.getPeriodo(), entity.getAprobado(), entity.getControlCalidad().getIdControlCalidad(),
                entity.getTipoParametroCalidad().getIdTipoParametroCalidad(), entity.getMedidaOjo().getIdMedidaOjo());
    }
}
