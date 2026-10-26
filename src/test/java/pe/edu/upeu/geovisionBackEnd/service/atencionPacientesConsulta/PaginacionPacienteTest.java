package pe.edu.upeu.geovisionBackEnd.service.atencionPacientesConsulta;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.PaginacionPaciente;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class PaginacionPacienteTest {
    @Test void rejectsOversizedOrNegativePagesAndUnknownSorts() {
        assertThrows(ReglaNegocioException.class, () -> page(-1, 10, "idPersona"));
        assertThrows(ReglaNegocioException.class, () -> page(0, 101, "idPersona"));
        assertThrows(ReglaNegocioException.class, () -> page(0, 10, "password"));
    }
    @Test void sortingUsesActualPrimaryKeyAsTieBreaker() {
        var page = page(0, 10, "fechaRegistro");
        assertNotNull(page.getSort().getOrderFor("idPersona"));
        assertNull(page.getSort().getOrderFor("id"));
    }
    @Test void wildcardCharactersAreLiteralAndSearchIsNormalized() {
        assertEquals("%ana!_!%!!%", PaginacionPaciente.termino(" Ana_%! "));
        assertNull(PaginacionPaciente.termino(null));
        assertNull(PaginacionPaciente.termino("  "));
    }
    private org.springframework.data.domain.PageRequest page(int page, int size, String sort) {
        return PaginacionPaciente.crear(page, size, sort, "asc", "fechaRegistro", "idPersona",
                Set.of("idPersona", "fechaRegistro"));
    }
}

