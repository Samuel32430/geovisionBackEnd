package pe.edu.upeu.geovision_backend.util.orden_trabajo;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PoliticaCalidadDTO;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

public final class ConstanciaOrdenTrabajo {
    public static final int CAPACIDAD = 500;
    private static final int MAX_USUARIO = 100;
    private static final String SEPARADOR = ". ";

    public enum Etapa {
        CREACION("[Creacion] Usuario: "),
        ENVIO("[Envio] Usuario: "),
        RECEPCION("[Recepcion] Usuario: "),
        CALIDAD("[Calidad] Usuario: "),
        ENTREGA("[Entrega] Usuario: ");

        private final String prefijo;

        Etapa(String prefijo) {
            this.prefijo = prefijo;
        }

        private int reserva() {
            return 1 + prefijo.length() + MAX_USUARIO;
        }
    }

    private static final int RESERVA_CALIDAD = Etapa.CALIDAD.reserva() + detalleCalidad(false,
            new BigDecimal("89.99"), new BigDecimal("999.99"), new BigDecimal("999.99")).length();

    private ConstanciaOrdenTrabajo() {}

    /**
     * Devuelve -1 si ni siquiera cabe la constancia obligatoria. Reserva espacio para
     * los pasos posteriores antes de aceptar observaciones nuevas.
     */
    public static int limiteObservaciones(String historial, Etapa etapa, String usuario, PoliticaCalidadDTO politica) {
        String detalle = etapa == Etapa.CALIDAD
                ? detalleCalidad(false, politica.ejeGrados(), politica.distanciaPupilarMm(), politica.prisma())
                : "";
        int disponible = CAPACIDAD - bytes(base(historial, etapa, usuario, detalle));
        if (disponible < 0) return -1;

        int reserva = switch (etapa) {
            case CREACION -> Etapa.ENTREGA.reserva();
            case ENVIO -> Etapa.RECEPCION.reserva() + RESERVA_CALIDAD;
            case RECEPCION -> RESERVA_CALIDAD;
            case CALIDAD, ENTREGA -> 0;
        };
        return Math.max(0, disponible - reserva - SEPARADOR.length());
    }

    public static String registrar(String historial, Etapa etapa, String usuario, String observaciones) {
        return registrar(historial, etapa, usuario, observaciones, null, false);
    }

    public static String registrarCalidad(String historial, String usuario, String observaciones,
                                          PoliticaCalidadDTO politica, boolean aprobado) {
        return registrar(historial, Etapa.CALIDAD, usuario, observaciones, politica, aprobado);
    }

    private static String registrar(String historial, Etapa etapa, String usuario, String observaciones,
                                    PoliticaCalidadDTO politica, boolean aprobado) {
        int limite = limiteObservaciones(historial, etapa, usuario, politica);
        if (limite < 0) {
            throw new ReglaNegocioException("El historial ocupa todo el espacio disponible para registrar este paso");
        }
        String texto = observaciones == null ? "" : observaciones.trim();
        if (bytes(texto) > limite) {
            throw new ReglaNegocioException("Las observaciones de este paso admiten hasta " + limite + " bytes");
        }
        String detalle = etapa == Etapa.CALIDAD
                ? detalleCalidad(aprobado, politica.ejeGrados(), politica.distanciaPupilarMm(), politica.prisma())
                : "";
        String resultado = base(historial, etapa, usuario, detalle);
        return texto.isEmpty() ? resultado : resultado + SEPARADOR + texto;
    }

    private static String base(String historial, Etapa etapa, String usuario, String detalle) {
        String evento = etapa.prefijo + usuario + detalle;
        return historial == null || historial.isBlank() ? evento : historial + "\n" + evento;
    }

    private static String detalleCalidad(boolean aprobado, BigDecimal eje, BigDecimal distanciaPupilar, BigDecimal prisma) {
        return ". " + (aprobado ? "Aprobado" : "Rechazado")
                + ". Margenes: eje=" + eje.stripTrailingZeros().toPlainString()
                + ", DP=" + distanciaPupilar.stripTrailingZeros().toPlainString()
                + ", prisma=" + prisma.stripTrailingZeros().toPlainString();
    }

    private static int bytes(String texto) {
        return texto.getBytes(StandardCharsets.UTF_8).length;
    }
}
