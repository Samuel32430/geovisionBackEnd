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
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.PersonaService;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.SoportePaciente.ausente;
import static pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente.normalizar;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonaServiceImpl implements PersonaService {
    private final ActorPacienteActual actorActual;
    private final SoportePaciente soporte;
    private final PersonaRepository personas;

    @Override
    @Transactional
    public PersonaResponseDTO create(PersonaRequestDTO request) {
        actorActual.escritura();
        var persona = new Persona();
        PersonaMapper.updateEntity(request, persona);
        return PersonaMapper.convertToResponse(personas.save(persona));
    }

    @Override
    @Transactional
    public PersonaResponseDTO update(Long id, PersonaRequestDTO request) {
        var actor = actorActual.escritura();
        var persona = personas.bloquear(id).orElseThrow(() -> ausente("Persona"));
        PersonaMapper.updateEntity(request, persona);
        soporte.registrarLogPersona(id, actor);
        return PersonaMapper.convertToResponse(persona);
    }

    @Override
    public PersonaResponseDTO read(Long id) {
        actorActual.lectura();
        return PersonaMapper.convertToResponse(soporte.persona(id));
    }
}

