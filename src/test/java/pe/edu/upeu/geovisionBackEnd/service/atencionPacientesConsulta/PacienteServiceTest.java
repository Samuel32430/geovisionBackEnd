package pe.edu.upeu.geovisionBackEnd.service.atencionPacientesConsulta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PacienteServiceImpl;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {
    @Mock ActorPacienteActual actor;
    @Mock SoportePaciente soporte;
    @Mock PacienteRepository pacientes;
    @Mock PersonaRepository personas;
    @Mock DocumentoRepository documentos;
    @Mock PacienteLogRepository logs;
    PacienteServiceImpl service;

    @BeforeEach void setup() {
        service = new PacienteServiceImpl(actor, soporte, pacientes, personas, documentos, logs);
    }

    @Test void existingPersonCannotBeRegisteredTwice() {
        when(personas.bloquear(1L)).thenReturn(Optional.of(new Persona()));
        when(pacientes.existsById(1L)).thenReturn(true);
        var req = new PacienteRequestDTO();
        req.setIdPersona(1L);
        assertThrows(ReglaNegocioException.class, () -> service.create(req));
        verify(pacientes, never()).saveAndFlush(any());
    }

    @Test void newRegistrationNormalizesPersonAndRecordsActor() {
        var current = new ActorPacienteActual.Actor(4L, 5L);
        when(actor.escritura()).thenReturn(current);
        when(personas.save(any())).thenAnswer(i -> {
            Persona person = i.getArgument(0);
            person.setIdPersona(7L);
            return person;
        });
        when(pacientes.save(any())).thenAnswer(i -> {
            Paciente patient = i.getArgument(0);
            patient.setIdPersona(7L);
            return patient;
        });
        var person = new PersonaRequestDTO();
        person.setNombres(" Ana ");
        person.setApellidos(" Perez ");
        person.setDireccion(" Calle 1 ");
        var req = new PacienteRegistroRequestDTO();
        req.setPersona(person);
        var result = service.registrar(req);
        assertEquals("Ana", result.getPersona().getNombres());
        assertEquals("Calle 1", result.getPersona().getDireccion());
        assertEquals(7L, result.getPaciente().getIdPersona());
        verify(soporte).registrarLog(any(Paciente.class), eq(current));
        verify(actor).escritura();
    }

    @Test void documentsAreCheckedBeforeRegistrationCompletes() {
        var person = new Persona();
        person.setIdPersona(7L);
        var patient = new Paciente();
        patient.setPersona(person);
        patient.setIdPersona(7L);
        when(personas.save(any())).thenReturn(person);
        when(pacientes.save(any())).thenReturn(patient);
        var type = new TipoDocumento();
        type.setIdTipoDocumento(1L);
        when(soporte.tipoDocumento(1L)).thenReturn(type);
        when(documentos.existsByTipoDocumentoIdTipoDocumentoAndNumero(1L, "123")).thenReturn(true);
        var item = new DocumentoRegistroRequestDTO();
        item.setNumero(" 123 ");
        item.setIdTipoDocumento(1L);
        var req = new PacienteRegistroRequestDTO();
        req.setPersona(new PersonaRequestDTO());
        req.setDocumentos(List.of(item));
        assertThrows(ReglaNegocioException.class, () -> service.registrar(req));
        verify(soporte, never()).registrarLog(any(), any());
    }

    @Test void invalidStateIsRejected() {
        assertThrows(ReglaNegocioException.class,
                () -> service.listar(null, 2, 0, 10, null, null));
        verifyNoInteractions(pacientes);
    }
}

