package pe.edu.upeu.geovisionBackEnd.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TipoMedidaEnumTest {
    @Test void usesTheAgreedCodesRatherThanOrdinals() {
        assertEquals(TipoMedidaEnum.LEJOS, TipoMedidaEnum.fromCodigo(1));
        assertEquals(TipoMedidaEnum.MEDIO, TipoMedidaEnum.fromCodigo(2));
        assertEquals(TipoMedidaEnum.CERCA, TipoMedidaEnum.fromCodigo(3));
        assertNull(TipoMedidaEnum.fromCodigo(null));
    }

    @Test void rejectsUndefinedCodes() {
        assertThrows(IllegalArgumentException.class, () -> TipoMedidaEnum.fromCodigo(0));
        assertThrows(IllegalArgumentException.class, () -> TipoMedidaEnum.fromCodigo(4));
    }
}

