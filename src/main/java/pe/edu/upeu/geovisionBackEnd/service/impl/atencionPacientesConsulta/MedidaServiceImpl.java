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
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.MedidaService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedidaServiceImpl implements MedidaService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final MedidaRepository medidas;
    private final MedidaOjoRepository ojos;

    @Override
    @Transactional
    public MedidaResponseDTO create(MedidaRequestDTO request) {
        var actor = actorActual.escritura();
        var examen = soporte.examenEditable(request.getIdExamen());
        var tipo = soporte.tipoMedida(request.getIdTipoMedida());
        verificarDuplicado(request, 0L);
        var medida = new Medida();
        medida.setExamen(examen);
        medida.setTipoMedida(tipo);
        MedidaMapper.updateEntity(request, medida);
        medida = medidas.saveAndFlush(medida);
        soporte.registrarLog(examen.getPaciente(), actor);
        return MedidaMapper.convertToResponse(medida);
    }

    @Override
    @Transactional
    public MedidaResponseDTO update(Long id, MedidaRequestDTO request) {
        var actor = actorActual.escritura();
        var medida = soporte.medida(id);
        soporte.examenEditable(medida.getExamen().getIdExamen());
        if (!medida.getExamen().getIdExamen().equals(request.getIdExamen())) {
            throw new ReglaNegocioException("No se puede transferir una medida a otro examen");
        }
        var tipo = soporte.tipoMedida(request.getIdTipoMedida());
        verificarDuplicado(request, id);
        medida.setTipoMedida(tipo);
        MedidaMapper.updateEntity(request, medida);
        medidas.flush();
        soporte.registrarLog(medida.getExamen().getPaciente(), actor);
        return MedidaMapper.convertToResponse(medida);
    }

    private void verificarDuplicado(MedidaRequestDTO request, Long id) {
        if (medidas.existsByExamenIdExamenAndTipoMedidaIdTipoMedidaAndIdMedidaNot(
                request.getIdExamen(), request.getIdTipoMedida(), id)) {
            throw new ReglaNegocioException("El examen ya tiene una medida de este tipo");
        }
    }

    @Override
    public MedidaDetalleResponseDTO read(Long id) {
        actorActual.lectura();
        var medida = soporte.medida(id);
        var dto = new MedidaDetalleResponseDTO();
        dto.setMedida(MedidaMapper.convertToResponse(medida));
        dto.setOjos(ojos.findByMedidaIdMedidaOrderByIdMedidaOjoAsc(id).stream()
                .map(MedidaOjoMapper::convertToResponse).toList());
        return dto;
    }

    @Override
    public List<MedidaResponseDTO> listarPorExamen(Long examen) {
        actorActual.lectura();
        soporte.examen(examen);
        return medidas.findByExamenIdExamenOrderByIdMedidaAsc(examen).stream()
                .map(MedidaMapper::convertToResponse).toList();
    }
}

