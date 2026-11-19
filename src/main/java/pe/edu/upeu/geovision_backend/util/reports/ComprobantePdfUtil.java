package pe.edu.upeu.geovision_backend.util.reports;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ProductoVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ServicioVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ComprobantePdfUtil {

    private static final Logger log = LoggerFactory.getLogger(ComprobantePdfUtil.class);

    private static final float MARGEN = 40f;
    private static final float ANCHO_PAGINA = PDRectangle.A4.getWidth();
    private static final float ALTO_PAGINA = PDRectangle.A4.getHeight();
    private static final float ANCHO_UTIL = ANCHO_PAGINA - (MARGEN * 2);

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PDDocument documento;
    private final PDFont fontRegular;
    private final PDFont fontBold;
    private PDPageContentStream contenidoActual;
    private float yActual;

    public ComprobantePdfUtil() throws IOException {
        this.documento = new PDDocument();
        try (InputStream regularStream = new ClassPathResource("fonts/NotoSans-Regular.ttf").getInputStream();
             InputStream boldStream = new ClassPathResource("fonts/NotoSans-Bold.ttf").getInputStream()) {
            this.fontRegular = PDType0Font.load(documento, regularStream);
            this.fontBold = PDType0Font.load(documento, boldStream);
        }
        nuevaPagina();
    }

    private void nuevaPagina() throws IOException {
        if (contenidoActual != null) {
            contenidoActual.close();
        }
        PDPage nueva = new PDPage(PDRectangle.A4);
        documento.addPage(nueva);
        contenidoActual = new PDPageContentStream(documento, nueva);
        yActual = ALTO_PAGINA - MARGEN;
    }

    private void asegurarEspacio(float altoRequerido) throws IOException {
        if (yActual - altoRequerido < MARGEN + 40f) {
            nuevaPagina();
        }
    }

    public byte[] generarComprobante(VentaResponseDTO venta) throws IOException {
        if (venta == null) {
            throw new IllegalArgumentException("Los datos de la venta son requeridos para emitir el comprobante");
        }

        // 1. Barra de marca superior
        dibujarBarraMarca();

        // 2. Encabezado de la empresa y recuadro oficial de comprobante
        dibujarCabeceraYRecuadroFiscal(venta);

        // 3. Datos del cliente y de la transacción
        dibujarDatosClienteYVenta(venta);

        // 4. Tabla de detalles (Productos y Servicios)
        dibujarTablaDetalles(venta);

        // 5. Totales y métodos de pago
        dibujarTotalesYPagos(venta);

        // 6. Pie de página institucional y legal
        dibujarPieComprobante();

        if (contenidoActual != null) {
            contenidoActual.close();
        }

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            documento.save(salida);
            documento.close();
            return salida.toByteArray();
        }
    }

    private void dibujarBarraMarca() throws IOException {
        contenidoActual.setNonStrokingColor(9 / 255f, 153 / 255f, 142 / 255f); // Teal GeoVision
        contenidoActual.addRect(MARGEN, yActual - 4, ANCHO_UTIL, 4);
        contenidoActual.fill();
        yActual -= 18;
    }

    private void dibujarCabeceraYRecuadroFiscal(VentaResponseDTO venta) throws IOException {
        ConfiguracionEmpresaDTO emp = venta.datosEmpresa();

        String nombreComercial = (emp != null && emp.nombreComercial() != null && !emp.nombreComercial().isBlank())
                ? sanearTexto(emp.nombreComercial())
                : "GeoVision Óptica & Laboratorio";
        String nombreLegal = (emp != null && emp.nombreLegal() != null && !emp.nombreLegal().isBlank())
                ? sanearTexto(emp.nombreLegal())
                : "GEOVISION OPTICA INTEGRAL S.A.C.";
        String ruc = (emp != null && emp.ruc() != null && !emp.ruc().isBlank())
                ? emp.ruc()
                : "20608945123";
        String direccion = (emp != null && emp.direccion() != null && !emp.direccion().isBlank())
                ? sanearTexto(emp.direccion())
                : "Av. Javier Prado Este 2450, San Borja, Lima";
        String telefono = (emp != null && emp.telefono() != null) ? emp.telefono() : "+51 1 445-8920";
        String correo = (emp != null && emp.correo() != null) ? emp.correo() : "contacto@geovision.pe";

        float anchoRecuadro = 200f;
        float altoRecuadro = 76f;
        float xRecuadro = ANCHO_PAGINA - MARGEN - anchoRecuadro;
        float yRecuadro = yActual - altoRecuadro;

        // --- Recuadro Fiscal Derecho ---
        contenidoActual.setNonStrokingColor(248 / 255f, 250 / 255f, 252 / 255f);
        contenidoActual.addRect(xRecuadro, yRecuadro, anchoRecuadro, altoRecuadro);
        contenidoActual.fill();

        contenidoActual.setStrokingColor(9 / 255f, 153 / 255f, 142 / 255f);
        contenidoActual.setLineWidth(1.5f);
        contenidoActual.addRect(xRecuadro, yRecuadro, anchoRecuadro, altoRecuadro);
        contenidoActual.stroke();

        String rucBox = "R.U.C. " + ruc;
        String tipoDoc = (venta.tipoComprobanteDescripcion() != null && !venta.tipoComprobanteDescripcion().isBlank())
                ? venta.tipoComprobanteDescripcion().toUpperCase()
                : "BOLETA DE VENTA";
        if (!tipoDoc.contains("ELECTRÓNICA") && !tipoDoc.contains("ELECTRONICA") && !tipoDoc.equals("NOTA DE VENTA")) {
            tipoDoc += " ELECTRÓNICA";
        }
        String numeroDoc = (venta.numeroComprobante() != null && !venta.numeroComprobante().isBlank())
                ? venta.numeroComprobante()
                : String.format("B001-%08d", venta.id() != null ? venta.id() : 1);

        float centroX = xRecuadro + (anchoRecuadro / 2);
        escribirTextoCentrado(rucBox, centroX, yActual - 18, fontBold, 11f, 30 / 255f, 41 / 255f, 59 / 255f);
        escribirTextoCentrado(tipoDoc, centroX, yActual - 38, fontBold, 10.5f, 9 / 255f, 153 / 255f, 142 / 255f);
        escribirTextoCentrado(numeroDoc, centroX, yActual - 58, fontBold, 12.5f, 15 / 255f, 23 / 255f, 42 / 255f);

        // --- Datos de la Empresa (Izquierda) ---
        float anchoIzq = ANCHO_UTIL - anchoRecuadro - 15f;
        float yIzq = yActual;

        escribirTexto(nombreComercial, MARGEN, yIzq - 12, fontBold, 14f, 15 / 255f, 23 / 255f, 42 / 255f);
        escribirTexto(nombreLegal, MARGEN, yIzq - 26, fontRegular, 8.5f, 71 / 255f, 85 / 255f, 105 / 255f);
        escribirTexto(truncarTexto(direccion, anchoIzq, fontRegular, 8.5f), MARGEN, yIzq - 38, fontRegular, 8.5f, 71 / 255f, 85 / 255f, 105 / 255f);
        escribirTexto("Tel: " + telefono + "  •  Email: " + correo, MARGEN, yIzq - 50, fontRegular, 8.5f, 71 / 255f, 85 / 255f, 105 / 255f);
        escribirTexto("Especialistas en Salud Visual & Contactología", MARGEN, yIzq - 62, fontRegular, 8f, 9 / 255f, 153 / 255f, 142 / 255f);

        yActual -= (altoRecuadro + 14);
    }

    private void dibujarDatosClienteYVenta(VentaResponseDTO venta) throws IOException {
        float altoCaja = 50f;

        contenidoActual.setNonStrokingColor(248 / 255f, 250 / 255f, 252 / 255f);
        contenidoActual.addRect(MARGEN, yActual - altoCaja, ANCHO_UTIL, altoCaja);
        contenidoActual.fill();

        contenidoActual.setStrokingColor(226 / 255f, 232 / 255f, 240 / 255f);
        contenidoActual.setLineWidth(0.8f);
        contenidoActual.addRect(MARGEN, yActual - altoCaja, ANCHO_UTIL, altoCaja);
        contenidoActual.stroke();

        float xCol1 = MARGEN + 10f;
        float xCol2 = MARGEN + (ANCHO_UTIL / 2) + 10f;

        String cliente = (venta.nombreCompletoPaciente() != null && !venta.nombreCompletoPaciente().isBlank())
                ? sanearTexto(venta.nombreCompletoPaciente())
                : "Público General / Cliente Varios";
        String empleado = (venta.nombreCompletoEmpleado() != null && !venta.nombreCompletoEmpleado().isBlank())
                ? sanearTexto(venta.nombreCompletoEmpleado())
                : "Personal Autorizado";
        String fecha = (venta.fechaRegistro() != null)
                ? venta.fechaRegistro().format(DTF)
                : "-";
        String estado = (venta.estadoDescripcion() != null && !venta.estadoDescripcion().isBlank())
                ? venta.estadoDescripcion().toUpperCase()
                : "EMITIDA";

        // Fila 1
        escribirTexto("CLIENTE / PACIENTE:", xCol1, yActual - 16, fontBold, 8f, 100 / 255f, 116 / 255f, 139 / 255f);
        escribirTexto(truncarTexto(cliente, 210f, fontBold, 9f), xCol1 + 95f, yActual - 16, fontBold, 9f, 15 / 255f, 23 / 255f, 42 / 255f);

        escribirTexto("FECHA & HORA:", xCol2, yActual - 16, fontBold, 8f, 100 / 255f, 116 / 255f, 139 / 255f);
        escribirTexto(fecha, xCol2 + 75f, yActual - 16, fontRegular, 9f, 15 / 255f, 23 / 255f, 42 / 255f);

        // Fila 2
        escribirTexto("ATENDIDO POR:", xCol1, yActual - 34, fontBold, 8f, 100 / 255f, 116 / 255f, 139 / 255f);
        escribirTexto(truncarTexto(empleado, 210f, fontRegular, 8.5f), xCol1 + 95f, yActual - 34, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);

        escribirTexto("ESTADO:", xCol2, yActual - 34, fontBold, 8f, 100 / 255f, 116 / 255f, 139 / 255f);
        if (estado.contains("PAGADA")) {
            escribirTexto(estado, xCol2 + 75f, yActual - 34, fontBold, 8.5f, 13 / 255f, 148 / 255f, 136 / 255f);
        } else {
            escribirTexto(estado, xCol2 + 75f, yActual - 34, fontBold, 8.5f, 180 / 255f, 83 / 255f, 9 / 255f);
        }

        yActual -= (altoCaja + 14);
    }

    private void dibujarTablaDetalles(VentaResponseDTO venta) throws IOException {
        float altoCabecera = 20f;
        float altoFila = 18f;

        // Anchos de columnas (Total = ANCHO_UTIL = ~515)
        float colCant = 45f;
        float colDesc = 280f;
        float colPrecioUnit = 95f;
        float colTotal = ANCHO_UTIL - colCant - colDesc - colPrecioUnit; // ~95f

        // Cabecera de la tabla
        contenidoActual.setNonStrokingColor(30 / 255f, 41 / 255f, 59 / 255f);
        contenidoActual.addRect(MARGEN, yActual - altoCabecera, ANCHO_UTIL, altoCabecera);
        contenidoActual.fill();

        float x = MARGEN;
        escribirTextoCentrado("CANT.", x + (colCant / 2), yActual - 13, fontBold, 8f, 1f, 1f, 1f);
        x += colCant;
        escribirTexto("DESCRIPCIÓN", x + 8, yActual - 13, fontBold, 8f, 1f, 1f, 1f);
        x += colDesc;
        escribirTextoDerecha("P. UNITARIO", x + colPrecioUnit - 8, yActual - 13, fontBold, 8f, 1f, 1f, 1f);
        x += colPrecioUnit;
        escribirTextoDerecha("TOTAL", x + colTotal - 8, yActual - 13, fontBold, 8f, 1f, 1f, 1f);

        yActual -= altoCabecera;

        int index = 0;

        // Renderizar Productos
        if (venta.productos() != null && !venta.productos().isEmpty()) {
            for (ProductoVendidoDTO p : venta.productos()) {
                asegurarEspacio(altoFila);

                if (index % 2 == 1) {
                    contenidoActual.setNonStrokingColor(248 / 255f, 250 / 255f, 252 / 255f);
                    contenidoActual.addRect(MARGEN, yActual - altoFila, ANCHO_UTIL, altoFila);
                    contenidoActual.fill();
                }

                // Línea inferior
                contenidoActual.setStrokingColor(241 / 255f, 245 / 255f, 249 / 255f);
                contenidoActual.setLineWidth(0.5f);
                contenidoActual.moveTo(MARGEN, yActual - altoFila);
                contenidoActual.lineTo(ANCHO_PAGINA - MARGEN, yActual - altoFila);
                contenidoActual.stroke();

                String cantStr = String.valueOf(p.cantidad() != null ? p.cantidad() : 1);
                String descStr = sanearTexto((p.codigoProducto() != null ? "[" + p.codigoProducto() + "] " : "") + (p.nombreProducto() != null ? p.nombreProducto() : "Producto Óptico"));
                String pUnitStr = "S/ " + DF.format(p.precioUnit() != null ? p.precioUnit() : BigDecimal.ZERO);
                String totalStr = "S/ " + DF.format(p.subTotal() != null ? p.subTotal() : BigDecimal.ZERO);

                x = MARGEN;
                escribirTextoCentrado(cantStr, x + (colCant / 2), yActual - 12, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);
                x += colCant;
                escribirTexto(truncarTexto(descStr, colDesc - 12, fontRegular, 8.5f), x + 8, yActual - 12, fontRegular, 8.5f, 30 / 255f, 41 / 255f, 59 / 255f);
                x += colDesc;
                escribirTextoDerecha(pUnitStr, x + colPrecioUnit - 8, yActual - 12, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);
                x += colPrecioUnit;
                escribirTextoDerecha(totalStr, x + colTotal - 8, yActual - 12, fontBold, 8.5f, 15 / 255f, 23 / 255f, 42 / 255f);

                yActual -= altoFila;
                index++;
            }
        }

        // Renderizar Servicios
        if (venta.servicios() != null && !venta.servicios().isEmpty()) {
            for (ServicioVendidoDTO s : venta.servicios()) {
                asegurarEspacio(altoFila);

                if (index % 2 == 1) {
                    contenidoActual.setNonStrokingColor(248 / 255f, 250 / 255f, 252 / 255f);
                    contenidoActual.addRect(MARGEN, yActual - altoFila, ANCHO_UTIL, altoFila);
                    contenidoActual.fill();
                }

                contenidoActual.setStrokingColor(241 / 255f, 245 / 255f, 249 / 255f);
                contenidoActual.setLineWidth(0.5f);
                contenidoActual.moveTo(MARGEN, yActual - altoFila);
                contenidoActual.lineTo(ANCHO_PAGINA - MARGEN, yActual - altoFila);
                contenidoActual.stroke();

                String cantStr = "1";
                String descStr = sanearTexto(s.nombreServicio() != null ? s.nombreServicio() : "Servicio Clínico / Especializado");
                String precioStr = "S/ " + DF.format(s.precio() != null ? s.precio() : BigDecimal.ZERO);

                x = MARGEN;
                escribirTextoCentrado(cantStr, x + (colCant / 2), yActual - 12, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);
                x += colCant;
                escribirTexto(truncarTexto(descStr, colDesc - 12, fontRegular, 8.5f), x + 8, yActual - 12, fontRegular, 8.5f, 30 / 255f, 41 / 255f, 59 / 255f);
                x += colDesc;
                escribirTextoDerecha(precioStr, x + colPrecioUnit - 8, yActual - 12, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);
                x += colPrecioUnit;
                escribirTextoDerecha(precioStr, x + colTotal - 8, yActual - 12, fontBold, 8.5f, 15 / 255f, 23 / 255f, 42 / 255f);

                yActual -= altoFila;
                index++;
            }
        }

        if (index == 0) {
            asegurarEspacio(altoFila);
            escribirTextoCentrado("No se registraron ítems de detalle", MARGEN + (ANCHO_UTIL / 2), yActual - 12, fontRegular, 8.5f, 100 / 255f, 116 / 255f, 139 / 255f);
            yActual -= altoFila;
        }

        yActual -= 10f;
    }

    private void dibujarTotalesYPagos(VentaResponseDTO venta) throws IOException {
        asegurarEspacio(95f);

        float anchoColPagos = 270f;
        float anchoColTotales = ANCHO_UTIL - anchoColPagos - 15f; // ~230f
        float xTotales = MARGEN + anchoColPagos + 15f;
        float yInicio = yActual;

        // --- Bloque Métodos de Pago (Izquierda) ---
        escribirTexto("DETALLE DE PAGOS / ABONOS", MARGEN, yInicio - 6, fontBold, 8f, 100 / 255f, 116 / 255f, 139 / 255f);

        float yPago = yInicio - 20;
        if (venta.pagos() != null && !venta.pagos().isEmpty()) {
            for (PagoResponseDTO pago : venta.pagos()) {
                String metodo = (pago.metodoPagoDescripcion() != null && !pago.metodoPagoDescripcion().isBlank())
                        ? pago.metodoPagoDescripcion()
                        : (pago.metodoPago() != null ? pago.metodoPago().name() : "EFECTIVO");
                String monto = "S/ " + DF.format(pago.monto() != null ? pago.monto() : BigDecimal.ZERO);
                String op = (pago.numeroOperacion() != null && !pago.numeroOperacion().isBlank())
                        ? " [Op: " + pago.numeroOperacion() + "]"
                        : "";

                escribirTexto("• " + metodo + op + ":", MARGEN + 5f, yPago, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);
                escribirTexto(monto, MARGEN + 190f, yPago, fontBold, 8.5f, 15 / 255f, 23 / 255f, 42 / 255f);
                yPago -= 14f;
            }
        } else {
            escribirTexto("• Sin pagos registrados al momento de emisión", MARGEN + 5f, yPago, fontRegular, 8f, 148 / 255f, 163 / 255f, 184 / 255f);
            yPago -= 14f;
        }

        // --- Bloque de Totales (Derecha) ---
        BigDecimal total = venta.total() != null ? venta.total() : BigDecimal.ZERO;
        BigDecimal pagado = venta.totalPagado() != null ? venta.totalPagado() : BigDecimal.ZERO;
        BigDecimal saldo = venta.saldoPendiente() != null ? venta.saldoPendiente() : total.subtract(pagado);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;

        BigDecimal subtotal = total.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);

        float yTot = yInicio;

        // Caja decorativa de totales
        float altoCajaTotales = 80f;
        contenidoActual.setNonStrokingColor(248 / 255f, 250 / 255f, 252 / 255f);
        contenidoActual.addRect(xTotales, yTot - altoCajaTotales, anchoColTotales, altoCajaTotales);
        contenidoActual.fill();

        contenidoActual.setStrokingColor(226 / 255f, 232 / 255f, 240 / 255f);
        contenidoActual.setLineWidth(0.8f);
        contenidoActual.addRect(xTotales, yTot - altoCajaTotales, anchoColTotales, altoCajaTotales);
        contenidoActual.stroke();

        float xLabel = xTotales + 10f;
        float xVal = xTotales + anchoColTotales - 10f;

        escribirTexto("OP. GRAVADA:", xLabel, yTot - 14, fontRegular, 8f, 100 / 255f, 116 / 255f, 139 / 255f);
        escribirTextoDerecha("S/ " + DF.format(subtotal), xVal, yTot - 14, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);

        escribirTexto("I.G.V. (18%):", xLabel, yTot - 27, fontRegular, 8f, 100 / 255f, 116 / 255f, 139 / 255f);
        escribirTextoDerecha("S/ " + DF.format(igv), xVal, yTot - 27, fontRegular, 8.5f, 51 / 255f, 65 / 255f, 85 / 255f);

        // Línea divisoria dentro de la caja de totales
        contenidoActual.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f);
        contenidoActual.setLineWidth(0.5f);
        contenidoActual.moveTo(xLabel, yTot - 33);
        contenidoActual.lineTo(xVal, yTot - 33);
        contenidoActual.stroke();

        escribirTexto("TOTAL VENTA:", xLabel, yTot - 47, fontBold, 9.5f, 15 / 255f, 23 / 255f, 42 / 255f);
        escribirTextoDerecha("S/ " + DF.format(total), xVal, yTot - 47, fontBold, 11f, 15 / 255f, 23 / 255f, 42 / 255f);

        escribirTexto("TOTAL COBRADO:", xLabel, yTot - 61, fontBold, 8.5f, 13 / 255f, 148 / 255f, 136 / 255f);
        escribirTextoDerecha("S/ " + DF.format(pagado), xVal, yTot - 61, fontBold, 9.5f, 13 / 255f, 148 / 255f, 136 / 255f);

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            escribirTexto("SALDO PENDIENTE:", xLabel, yTot - 74, fontBold, 8.5f, 220 / 255f, 38 / 255f, 38 / 255f);
            escribirTextoDerecha("S/ " + DF.format(saldo), xVal, yTot - 74, fontBold, 9.5f, 220 / 255f, 38 / 255f, 38 / 255f);
        }

        yActual = Math.min(yPago, yTot - altoCajaTotales) - 20f;
    }

    private void dibujarPieComprobante() throws IOException {
        asegurarEspacio(50f);

        // Línea divisoria
        contenidoActual.setStrokingColor(226 / 255f, 232 / 255f, 240 / 255f);
        contenidoActual.setLineWidth(1f);
        contenidoActual.moveTo(MARGEN, yActual);
        contenidoActual.lineTo(ANCHO_PAGINA - MARGEN, yActual);
        contenidoActual.stroke();

        yActual -= 14;

        String msg1 = "GeoVision Óptica & Laboratorio agradece su preferencia.";
        String msg2 = "Garantía de adaptación en lunas y monturas sujeta a control visual. Conserve este comprobante.";
        String msg3 = "Comprobante emitido electrónicamente por el Sistema de Gestión Óptica GeoVision";

        float centroX = ANCHO_PAGINA / 2;
        escribirTextoCentrado(msg1, centroX, yActual, fontBold, 8.5f, 15 / 255f, 23 / 255f, 42 / 255f);
        escribirTextoCentrado(msg2, centroX, yActual - 12, fontRegular, 7.5f, 100 / 255f, 116 / 255f, 139 / 255f);
        escribirTextoCentrado(msg3, centroX, yActual - 24, fontRegular, 7f, 148 / 255f, 163 / 255f, 184 / 255f);

        yActual -= 30;
    }

    // >>>> HELPERS
    private void escribirTexto(String texto, float x, float y, PDFont font, float size, float r, float g, float b) throws IOException {
        contenidoActual.beginText();
        contenidoActual.setFont(font, size);
        contenidoActual.setNonStrokingColor(r, g, b);
        contenidoActual.newLineAtOffset(x, y);
        contenidoActual.showText(texto != null ? texto : "");
        contenidoActual.endText();
    }

    private void escribirTextoCentrado(String texto, float centroX, float y, PDFont font, float size, float r, float g, float b) throws IOException {
        if (texto == null) texto = "";
        float ancho = calcularAnchoTexto(texto, font, size);
        escribirTexto(texto, centroX - (ancho / 2), y, font, size, r, g, b);
    }

    private void escribirTextoDerecha(String texto, float derechaX, float y, PDFont font, float size, float r, float g, float b) throws IOException {
        if (texto == null) texto = "";
        float ancho = calcularAnchoTexto(texto, font, size);
        escribirTexto(texto, derechaX - ancho, y, font, size, r, g, b);
    }

    private float calcularAnchoTexto(String texto, PDFont font, float size) throws IOException {
        if (texto == null || texto.isEmpty()) return 0f;
        return (font.getStringWidth(texto) / 1000f) * size;
    }

    private String truncarTexto(String texto, float anchoMaximo, PDFont font, float size) throws IOException {
        if (texto == null) return "";
        if (calcularAnchoTexto(texto, font, size) <= anchoMaximo) return texto;

        String elipsis = "...";
        float anchoElipsis = calcularAnchoTexto(elipsis, font, size);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (calcularAnchoTexto(sb.toString() + c, font, size) + anchoElipsis > anchoMaximo) {
                break;
            }
            sb.append(c);
        }
        return sb.toString() + elipsis;
    }

    private String sanearTexto(String texto) {
        if (texto == null) return "";
        return texto.replace("\uFFFD", "Ó")
                    .trim();
    }
}
