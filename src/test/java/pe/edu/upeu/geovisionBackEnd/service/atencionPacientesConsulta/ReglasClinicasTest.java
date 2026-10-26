package pe.edu.upeu.geovisionBackEnd.service.atencionPacientesConsulta;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.exception.*;
import pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReglasClinicasTest {
    final ActorPacienteActual actor = mock(ActorPacienteActual.class);
    final SoportePaciente soporte = mock(SoportePaciente.class);
    final RecetaRepository recetas = mock(RecetaRepository.class);
    final RecetaServiceImpl service = new RecetaServiceImpl(actor, soporte, recetas);

    @Test void prescriptionCopiesExternalFlagAndAuthenticatedAuthor() {
        var employee = new ActorPacienteActual.Actor(9L, 10L);
        when(actor.escritura()).thenReturn(employee);
        var exam = new Examen();
        exam.setIdExamen(2L);
        exam.setPaciente(new Paciente());
        exam.setEsExterno(1);
        when(soporte.examenEditable(2L)).thenReturn(exam);
        when(recetas.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var req = new RecetaRequestDTO();
        req.setIdExamen(2L);
        var response = service.create(req);
        assertEquals(1, response.getEsExterno());
        verify(recetas).saveAndFlush(argThat(r -> r.getIdEmpleadoRegistro().equals(9L)
                && r.getExamen() == exam));
        verify(soporte).registrarLog(exam.getPaciente(), employee);
    }

    @Test void invalidDeliveryIsRejectedBeforeSaving() {
        when(actor.escritura()).thenReturn(new ActorPacienteActual.Actor(9L, 10L));
        var exam = new Examen();
        when(soporte.examenEditable(1L)).thenReturn(exam);
        var req = new RecetaRequestDTO();
        req.setIdExamen(1L);
        req.setFechaDeEntrega(LocalDateTime.of(2000, 1, 1, 0, 0));
        assertThrows(ReglaNegocioException.class, () -> service.create(req));
        verify(recetas, never()).saveAndFlush(any());
    }

    @Test void repeatedDeliveryDoesNotOverwriteHistoricalDate() {
        var prescription = new Receta();
        var date = LocalDateTime.of(2026, 10, 20, 12, 0);
        prescription.setFechaDeEntrega(date);
        when(recetas.bloquear(1L)).thenReturn(Optional.of(prescription));
        var req = new RecetaEntregaRequestDTO();
        req.setFechaDeEntrega(date.plusDays(1));
        assertThrows(ReglaNegocioException.class, () -> service.registrarEntrega(1L, req));
        assertEquals(date, prescription.getFechaDeEntrega());
    }

    @Test void missingPrescriptionReturnsNotFound() {
        when(recetas.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursosNoEncontradosException.class, () -> service.read(1L));
    }

    @Test void issuedPrescriptionFreezesExamThroughSharedLock() {
        var examenes = mock(ExamenRepository.class);
        var pacientes = mock(PacienteRepository.class);
        var support = new SoportePaciente(mock(PersonaRepository.class), pacientes, examenes,
                mock(MedidaRepository.class), recetas, mock(PacienteLogRepository.class),
                mock(TipoDocumentoRepository.class), mock(TipoMedidaRepository.class), mock(TipoOjoRepository.class));
        var patient = new Paciente();
        patient.setIdPersona(3L);
        var exam = new Examen();
        exam.setPaciente(patient);
        when(examenes.bloquear(2L)).thenReturn(Optional.of(exam));
        when(pacientes.findById(3L)).thenReturn(Optional.of(patient));
        when(recetas.existsByExamenIdExamen(2L)).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> support.examenEditable(2L));
        verify(examenes).bloquear(2L);
    }

    @Test void duplicateEyeIsRejected() {
        var eyes = mock(MedidaOjoRepository.class);
        var eyesService = new MedidaOjoServiceImpl(actor, soporte, eyes);
        var measure = new Medida();
        var exam = new Examen();
        exam.setIdExamen(3L);
        measure.setExamen(exam);
        when(soporte.medida(1L)).thenReturn(measure);
        when(eyes.existsByMedidaIdMedidaAndTipoOjoIdTipoOjoAndIdMedidaOjoNot(1L, 2L, 0L)).thenReturn(true);
        var req = new MedidaOjoRequestDTO();
        req.setIdMedida(1L);
        req.setIdTipoOjo(2L);
        assertThrows(ReglaNegocioException.class, () -> eyesService.create(req));
        verify(eyes, never()).saveAndFlush(any());
    }
}

