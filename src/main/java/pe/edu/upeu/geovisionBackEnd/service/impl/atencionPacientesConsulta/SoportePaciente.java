package pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.exception.*;
import pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta.*;

@Component
@RequiredArgsConstructor
public class SoportePaciente {
    private final PersonaRepository personas;
    private final PacienteRepository pacientes;
    private final ExamenRepository examenes;
    private final MedidaRepository medidas;
    private final RecetaRepository recetas;
    private final PacienteLogRepository logs;
    private final TipoDocumentoRepository tiposDocumento;
    private final TipoMedidaRepository tiposMedida;
    private final TipoOjoRepository tiposOjo;

    public Persona persona(Long id) {
        return personas.findById(id).orElseThrow(() -> ausente("Persona"));
    }
    public Paciente paciente(Long id) {
        return pacientes.findById(id).orElseThrow(() -> ausente("Paciente"));
    }
    public Paciente pacienteActivo(Long id) {
        var paciente = paciente(id);
        if (!Integer.valueOf(1).equals(paciente.getEstado())) {
            throw new ReglaNegocioException("El paciente esta inactivo");
        }
        return paciente;
    }
    public Examen examen(Long id) {
        return examenes.findById(id).orElseThrow(() -> ausente("Examen"));
    }
    public Medida medida(Long id) {
        return medidas.findById(id).orElseThrow(() -> ausente("Medida"));
    }
    public Examen examenEditable(Long id) {
        var examen = examenes.bloquear(id).orElseThrow(() -> ausente("Examen"));
        pacienteActivo(examen.getPaciente().getIdPersona());
        if (recetas.existsByExamenIdExamen(id)) {
            throw new ReglaNegocioException("No se puede modificar un examen con receta emitida");
        }
        return examen;
    }
    public TipoDocumento tipoDocumento(Long id) {
        return tiposDocumento.findById(id).orElseThrow(() -> ausente("Tipo de documento"));
    }
    public TipoMedida tipoMedida(Long id) {
        return tiposMedida.findById(id).orElseThrow(() -> ausente("Tipo de medida"));
    }
    public TipoOjo tipoOjo(Long id) {
        return tiposOjo.findById(id).orElseThrow(() -> ausente("Tipo de ojo"));
    }
    public void registrarLog(Paciente paciente, ActorPacienteActual.Actor actor) {
        var log = new PacienteLog();
        log.setPaciente(paciente);
        log.setIdUsuario(actor.idUsuario());
        logs.save(log);
    }
    public void registrarLogPersona(Long persona, ActorPacienteActual.Actor actor) {
        pacientes.findById(persona).ifPresent(p -> registrarLog(p, actor));
    }
    public static RecursosNoEncontradosException ausente(String recurso) {
        return new RecursosNoEncontradosException(recurso + " no encontrado");
    }
}

