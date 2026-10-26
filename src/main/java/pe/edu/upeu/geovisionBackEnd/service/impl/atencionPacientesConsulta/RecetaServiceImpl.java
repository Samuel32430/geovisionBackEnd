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
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.RecetaService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecetaServiceImpl implements RecetaService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final RecetaRepository recetas;

    @Override
    @Transactional
    public RecetaResponseDTO create(RecetaRequestDTO request) {
        var actor = actorActual.escritura();
        // The same exam lock is used by measurement edits and prescription issuance.
        var examen = soporte.examenEditable(request.getIdExamen());
        var receta = new Receta();
        RecetaMapper.updateEntity(request, receta);
        receta.setExamen(examen);
        receta.setEsExterno(examen.getEsExterno());
        receta.setIdEmpleadoRegistro(actor.idEmpleado());
        receta.setFechaDeEntrega(request.getFechaDeEntrega() == null ? null : request.getFechaDeEntrega().withNano(0));
        if (!receta.isFechaEntregaValida()) {
            throw new ReglaNegocioException("La entrega no puede ser anterior al registro");
        }
        receta = recetas.saveAndFlush(receta);
        soporte.registrarLog(examen.getPaciente(), actor);
        return RecetaMapper.convertToResponse(receta);
    }

    @Override
    public RecetaResponseDTO read(Long id) {
        actorActual.lectura();
        return RecetaMapper.convertToResponse(recetas.findById(id).orElseThrow(() -> ausente("Receta")));
    }

    @Override
    @Transactional
    public RecetaResponseDTO registrarEntrega(Long id, RecetaEntregaRequestDTO request) {
        var actor = actorActual.escritura();
        var receta = recetas.bloquear(id).orElseThrow(() -> ausente("Receta"));
        if (receta.getFechaDeEntrega() != null) {
            throw new ReglaNegocioException("La entrega de la receta ya fue registrada");
        }
        var fechaEntrega = request.getFechaDeEntrega().withNano(0);
        if (fechaEntrega.isBefore(receta.getFechaRegistro())) {
            throw new ReglaNegocioException("La entrega no puede ser anterior al registro");
        }
        receta.setFechaDeEntrega(fechaEntrega);
        soporte.registrarLog(receta.getExamen().getPaciente(), actor);
        return RecetaMapper.convertToResponse(receta);
    }

    @Override
    public PaginaResponse<RecetaResponseDTO> listarPorPaciente(Long paciente, int pagina, int tamanio) {
        actorActual.lectura();
        soporte.paciente(paciente);
        var pageable = PaginacionPaciente.crear(pagina, tamanio, "fechaRegistro", "desc",
                "fechaRegistro", "idReceta", Set.of("fechaRegistro"));
        return PaginaResponse.de(recetas.findByExamenPacienteIdPersona(paciente, pageable)
                .map(RecetaMapper::convertToResponse));
    }
}

