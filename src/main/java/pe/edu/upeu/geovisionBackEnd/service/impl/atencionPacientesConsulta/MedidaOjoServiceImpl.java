package pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.MedidaOjoService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedidaOjoServiceImpl implements MedidaOjoService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final MedidaOjoRepository ojos;

    @Override
    @Transactional
    public MedidaOjoResponseDTO create(MedidaOjoRequestDTO request) {
        var actor = actorActual.escritura();
        var medida = soporte.medida(request.getIdMedida());
        soporte.examenEditable(medida.getExamen().getIdExamen());
        var tipo = soporte.tipoOjo(request.getIdTipoOjo());
        verificarDuplicado(request, 0L);
        var ojo = new MedidaOjo();
        ojo.setMedida(medida);
        ojo.setTipoOjo(tipo);
        MedidaOjoMapper.updateEntity(request, ojo);
        ojo = ojos.saveAndFlush(ojo);
        soporte.registrarLog(medida.getExamen().getPaciente(), actor);
        return MedidaOjoMapper.convertToResponse(ojo);
    }

    @Override
    @Transactional
    public MedidaOjoResponseDTO update(Long id, MedidaOjoRequestDTO request) {
        var actor = actorActual.escritura();
        var ojo = ojos.findById(id).orElseThrow(() -> ausente("Medida de ojo"));
        soporte.examenEditable(ojo.getMedida().getExamen().getIdExamen());
        if (!ojo.getMedida().getIdMedida().equals(request.getIdMedida())) {
            throw new ReglaNegocioException("No se puede transferir un ojo a otra medida");
        }
        var tipo = soporte.tipoOjo(request.getIdTipoOjo());
        verificarDuplicado(request, id);
        ojo.setTipoOjo(tipo);
        MedidaOjoMapper.updateEntity(request, ojo);
        ojos.flush();
        soporte.registrarLog(ojo.getMedida().getExamen().getPaciente(), actor);
        return MedidaOjoMapper.convertToResponse(ojo);
    }

    private void verificarDuplicado(MedidaOjoRequestDTO request, Long id) {
        if (ojos.existsByMedidaIdMedidaAndTipoOjoIdTipoOjoAndIdMedidaOjoNot(
                request.getIdMedida(), request.getIdTipoOjo(), id)) {
            throw new ReglaNegocioException("La medida ya tiene un registro de este ojo");
        }
    }

    @Override
    public MedidaOjoResponseDTO read(Long id) {
        actorActual.lectura();
        return MedidaOjoMapper.convertToResponse(ojos.findById(id).orElseThrow(() -> ausente("Medida de ojo")));
    }

    @Override
    public List<MedidaOjoResponseDTO> listarPorMedida(Long medida) {
        actorActual.lectura();
        soporte.medida(medida);
        return ojos.findByMedidaIdMedidaOrderByIdMedidaOjoAsc(medida).stream()
                .map(MedidaOjoMapper::convertToResponse).toList();
    }
}

