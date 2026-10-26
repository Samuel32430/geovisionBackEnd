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
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.ExamenService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamenServiceImpl implements ExamenService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final ExamenRepository examenes;
    private final MedidaRepository medidas;
    private final MedidaOjoRepository ojos;
    private final RecetaRepository recetas;

    @Override
    @Transactional
    public ExamenResponseDTO create(ExamenRequestDTO request) {
        var actor = actorActual.escritura();
        var paciente = soporte.pacienteActivo(request.getIdPaciente());
        var examen = new Examen();
        ExamenMapper.updateEntity(request, examen);
        examen.setPaciente(paciente);
        examen.setIdEmpleadoRegistro(actor.idEmpleado());
        examen = examenes.save(examen);
        soporte.registrarLog(paciente, actor);
        return ExamenMapper.convertToResponse(examen);
    }

    @Override
    @Transactional
    public ExamenResponseDTO update(Long id, ExamenRequestDTO request) {
        var actor = actorActual.escritura();
        var examen = soporte.examenEditable(id);
        if (!examen.getPaciente().getIdPersona().equals(request.getIdPaciente())) {
            throw new ReglaNegocioException("No se puede transferir un examen a otro paciente");
        }
        ExamenMapper.updateEntity(request, examen);
        soporte.registrarLog(examen.getPaciente(), actor);
        return ExamenMapper.convertToResponse(examen);
    }

    @Override
    public ExamenDetalleResponseDTO read(Long id) {
        actorActual.lectura();
        var examen = soporte.examen(id);
        var detalle = new ExamenDetalleResponseDTO();
        detalle.setExamen(ExamenMapper.convertToResponse(examen));
        detalle.setMedidas(medidas.findByExamenIdExamenOrderByIdMedidaAsc(id).stream().map(medida -> {
            var dto = new MedidaDetalleResponseDTO();
            dto.setMedida(MedidaMapper.convertToResponse(medida));
            dto.setOjos(ojos.findByMedidaIdMedidaOrderByIdMedidaOjoAsc(medida.getIdMedida())
                    .stream().map(MedidaOjoMapper::convertToResponse).toList());
            return dto;
        }).toList());
        detalle.setReceta(recetas.findByExamenIdExamen(id).map(RecetaMapper::convertToResponse).orElse(null));
        return detalle;
    }

    @Override
    public PaginaResponse<ExamenResponseDTO> listarPorPaciente(Long paciente, int pagina, int tamanio) {
        actorActual.lectura();
        soporte.paciente(paciente);
        var pageable = PaginacionPaciente.crear(pagina, tamanio, "fechaRegistro", "desc",
                "fechaRegistro", "idExamen", Set.of("fechaRegistro"));
        return PaginaResponse.de(examenes.findByPacienteIdPersona(paciente, pageable).map(ExamenMapper::convertToResponse));
    }
}

