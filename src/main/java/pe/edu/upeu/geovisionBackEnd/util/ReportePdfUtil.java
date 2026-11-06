package pe.edu.upeu.geovisionBackEnd.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.KpiCardDTO;
import pe.edu.upeu.geovisionBackEnd.entity.ConfiguracionEmpresa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor transversal de generación de reportes PDF para GeoVision con Apache PDFBox 3.x.
 * Provee maquetación automática de cabeceras institucionales, tarjetas de KPIs,
 * tablas estilizadas con paginación inteligente y pie de página numerado.
 */
@Slf4j
public class ReportePdfUtil {

    private static final float MARGEN = 40f;
    private static final float ANCHO_PAGINA = PDRectangle.A4.getWidth();
    private static final float ALTO_PAGINA = PDRectangle.A4.getHeight();
    private static final float ANCHO_UTIL = ANCHO_PAGINA - (MARGEN * 2);

    private final PDDocument documento;
    private final PDFont fontRegular;
    private final PDFont fontBold;

    private PDPageContentStream contenidoActual;
    private float yActual;
    private final List<PDPage> paginasCreadas = new ArrayList<>();

    public ReportePdfUtil() throws IOException {
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
        paginasCreadas.add(nueva);
        contenidoActual = new PDPageContentStream(documento, nueva);
        yActual = ALTO_PAGINA - MARGEN;
    }

    private void asegurarEspacio(float altoRequerido) throws IOException {
        if (yActual - altoRequerido < MARGEN + 25f) {
            nuevaPagina();
        }
    }

    /**
     * Dibuja el encabezado corporativo del reporte con datos de la empresa y título.
     */
    public void dibujarEncabezado(ConfiguracionEmpresa empresa, String tituloReporte, String periodoTexto) throws IOException {
        // Franja decorativa superior
        contenidoActual.setNonStrokingColor(26 / 255f, 54 / 255f, 93 / 255f); // Azul oscuro corporativo #1A365D
        contenidoActual.addRect(MARGEN, yActual - 3, ANCHO_UTIL, 4);
        contenidoActual.fill();
        yActual -= 16;

        String nombreEmpresa = (empresa != null && empresa.getNombreComercial() != null && !empresa.getNombreComercial().isBlank())
                ? empresa.getNombreComercial()
                : (empresa != null && empresa.getNombreLegal() != null ? empresa.getNombreLegal() : "ÓPTICA GEO VISIÓN");

        String rucTexto = (empresa != null && empresa.getRuc() != null) ? "RUC: " + empresa.getRuc() : "RUC: 20601234567";

        // Nombre de Empresa
        escribirTexto(nombreEmpresa, MARGEN, yActual, fontBold, 13, 26 / 255f, 54 / 255f, 93 / 255f);
        escribirTexto(rucTexto, MARGEN, yActual - 13, fontRegular, 8.5f, 100 / 255f, 116 / 255f, 139 / 255f);

        // Fecha y hora de generación alineada a la derecha
        String fechaHoraEmision = "Emitido: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        float anchoFecha = calcularAnchoTexto(fechaHoraEmision, fontRegular, 8.5f);
        escribirTexto(fechaHoraEmision, ANCHO_PAGINA - MARGEN - anchoFecha, yActual, fontRegular, 8.5f, 100 / 255f, 116 / 255f, 139 / 255f);

        yActual -= 30;

        // Título principal del reporte
        escribirTexto(tituloReporte, MARGEN, yActual, fontBold, 15, 15 / 255f, 23 / 255f, 42 / 255f);
        yActual -= 15;

        // Periodo o filtro aplicado
        if (periodoTexto != null && !periodoTexto.isBlank()) {
            escribirTexto(periodoTexto, MARGEN, yActual, fontRegular, 9.5f, 71 / 255f, 85 / 255f, 105 / 255f);
            yActual -= 16;
        }

        // Línea divisoria suave
        contenidoActual.setStrokingColor(226 / 255f, 232 / 255f, 240 / 255f);
        contenidoActual.setLineWidth(1f);
        contenidoActual.moveTo(MARGEN, yActual);
        contenidoActual.lineTo(ANCHO_PAGINA - MARGEN, yActual);
        contenidoActual.stroke();
        yActual -= 14;
    }

    /**
     * Dibuja tarjetas horizontales de KPIs ejecutivos.
     */
    public void dibujarTarjetasKpi(List<KpiCardDTO> kpis) throws IOException {
        if (kpis == null || kpis.isEmpty()) return;

        asegurarEspacio(50);
        int totalKpis = Math.min(kpis.size(), 4);
        float espacioEntreTarjetas = 10f;
        float anchoTarjeta = (ANCHO_UTIL - (espacioEntreTarjetas * (totalKpis - 1))) / totalKpis;
        float altoTarjeta = 42f;

        float x = MARGEN;
        for (int i = 0; i < totalKpis; i++) {
            KpiCardDTO kpi = kpis.get(i);

            // Fondo de la tarjeta
            contenidoActual.setNonStrokingColor(248 / 255f, 250 / 255f, 252 / 255f); // #F8FAFC
            contenidoActual.addRect(x, yActual - altoTarjeta, anchoTarjeta, altoTarjeta);
            contenidoActual.fill();

            // Borde suave
            contenidoActual.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f); // #CBD5E1
            contenidoActual.setLineWidth(0.8f);
            contenidoActual.addRect(x, yActual - altoTarjeta, anchoTarjeta, altoTarjeta);
            contenidoActual.stroke();

            // Título KPI
            String titulo = truncarTexto(kpi.getTitulo(), anchoTarjeta - 12, fontRegular, 7.5f);
            escribirTexto(titulo, x + 6, yActual - 12, fontBold, 7.5f, 100 / 255f, 116 / 255f, 139 / 255f);

            // Valor principal
            String valor = truncarTexto(kpi.getValor(), anchoTarjeta - 12, fontBold, 11f);
            escribirTexto(valor, x + 6, yActual - 25, fontBold, 11f, 15 / 255f, 23 / 255f, 42 / 255f);

            // Subtítulo
            if (kpi.getSubtitulo() != null && !kpi.getSubtitulo().isBlank()) {
                String sub = truncarTexto(kpi.getSubtitulo(), anchoTarjeta - 12, fontRegular, 6.5f);
                escribirTexto(sub, x + 6, yActual - 35, fontRegular, 6.5f, 100 / 255f, 116 / 255f, 139 / 255f);
            }

            x += anchoTarjeta + espacioEntreTarjetas;
        }

        yActual -= (altoTarjeta + 14);
    }

    /**
     * Dibuja una tabla estilizada con columnas ajustadas y soporte de salto de página.
     */
    public void dibujarTabla(List<String> encabezados, float[] anchosColumnas, List<List<String>> filas) throws IOException {
        if (encabezados == null || filas == null) return;

        float altoCabecera = 20f;
        float altoFila = 18f;

        asegurarEspacio(altoCabecera + altoFila * 2);
        dibujarCabeceraTabla(encabezados, anchosColumnas, altoCabecera);

        int indexFila = 0;
        for (List<String> fila : filas) {
            asegurarEspacio(altoFila);

            // Si se creó nueva página, re-dibujar la cabecera de la tabla
            if (yActual == ALTO_PAGINA - MARGEN) {
                dibujarCabeceraTabla(encabezados, anchosColumnas, altoCabecera);
            }

            // Fondo alternado (cebra)
            if (indexFila % 2 == 1) {
                contenidoActual.setNonStrokingColor(248 / 255f, 250 / 255f, 252 / 255f);
                contenidoActual.addRect(MARGEN, yActual - altoFila, ANCHO_UTIL, altoFila);
                contenidoActual.fill();
            }

            // Borde inferior sutil
            contenidoActual.setStrokingColor(241 / 255f, 245 / 255f, 249 / 255f);
            contenidoActual.setLineWidth(0.5f);
            contenidoActual.moveTo(MARGEN, yActual - altoFila);
            contenidoActual.lineTo(ANCHO_PAGINA - MARGEN, yActual - altoFila);
            contenidoActual.stroke();

            // Celdas de datos
            float xCelda = MARGEN;
            for (int col = 0; col < encabezados.size(); col++) {
                float anchoCol = (col < anchosColumnas.length) ? anchosColumnas[col] : 60f;
                String textoCelda = (col < fila.size() && fila.get(col) != null) ? fila.get(col) : "-";
                String textoAjustado = truncarTexto(textoCelda, anchoCol - 6, fontRegular, 8f);

                escribirTexto(textoAjustado, xCelda + 4, yActual - 12, fontRegular, 8f, 51 / 255f, 65 / 255f, 85 / 255f);
                xCelda += anchoCol;
            }

            yActual -= altoFila;
            indexFila++;
        }

        yActual -= 10;
    }

    private void dibujarCabeceraTabla(List<String> encabezados, float[] anchosColumnas, float altoCabecera) throws IOException {
        // Fondo de cabecera
        contenidoActual.setNonStrokingColor(30 / 255f, 41 / 255f, 59 / 255f); // #1E293B
        contenidoActual.addRect(MARGEN, yActual - altoCabecera, ANCHO_UTIL, altoCabecera);
        contenidoActual.fill();

        float xCelda = MARGEN;
        for (int i = 0; i < encabezados.size(); i++) {
            float anchoCol = (i < anchosColumnas.length) ? anchosColumnas[i] : 60f;
            String texto = truncarTexto(encabezados.get(i), anchoCol - 6, fontBold, 8f);
            escribirTexto(texto, xCelda + 4, yActual - 13, fontBold, 8f, 255 / 255f, 255 / 255f, 255 / 255f);
            xCelda += anchoCol;
        }

        yActual -= altoCabecera;
    }

    /**
     * Dibuja los números de página finales "Página X de Y" en cada página creada.
     */
    private void dibujarPiesDePagina() throws IOException {
        if (contenidoActual != null) {
            contenidoActual.close();
        }
        int totalPaginas = paginasCreadas.size();
        for (int i = 0; i < totalPaginas; i++) {
            PDPage pagina = paginasCreadas.get(i);
            try (PDPageContentStream footerStream = new PDPageContentStream(documento, pagina,
                    PDPageContentStream.AppendMode.APPEND, true, true)) {
                // Línea separadora inferior
                footerStream.setStrokingColor(226 / 255f, 232 / 255f, 240 / 255f);
                footerStream.setLineWidth(0.8f);
                footerStream.moveTo(MARGEN, MARGEN + 12);
                footerStream.lineTo(ANCHO_PAGINA - MARGEN, MARGEN + 12);
                footerStream.stroke();

                String textoPieIzquierdo = "GeoVision - Sistema Integral de Óptica";
                footerStream.beginText();
                footerStream.setFont(fontRegular, 8f);
                footerStream.setNonStrokingColor(148 / 255f, 163 / 255f, 184 / 255f);
                footerStream.newLineAtOffset(MARGEN, MARGEN + 2);
                footerStream.showText(textoPieIzquierdo);
                footerStream.endText();

                String textoPieDerecho = "Página " + (i + 1) + " de " + totalPaginas;
                float anchoPie = (fontRegular.getStringWidth(textoPieDerecho) / 1000f) * 8f;
                footerStream.beginText();
                footerStream.setFont(fontRegular, 8f);
                footerStream.setNonStrokingColor(148 / 255f, 163 / 255f, 184 / 255f);
                footerStream.newLineAtOffset(ANCHO_PAGINA - MARGEN - anchoPie, MARGEN + 2);
                footerStream.showText(textoPieDerecho);
                footerStream.endText();
            }
        }
    }

    /**
     * Genera y retorna los bytes finales del PDF.
     */
    public byte[] exportarPdf() throws IOException {
        dibujarPiesDePagina();
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            documento.save(salida);
            documento.close();
            return salida.toByteArray();
        }
    }

    private void escribirTexto(String texto, float x, float y, PDFont font, float size, float r, float g, float b) throws IOException {
        contenidoActual.beginText();
        contenidoActual.setFont(font, size);
        contenidoActual.setNonStrokingColor(r, g, b);
        contenidoActual.newLineAtOffset(x, y);
        contenidoActual.showText(texto);
        contenidoActual.endText();
    }

    private float calcularAnchoTexto(String texto, PDFont font, float size) throws IOException {
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
}
