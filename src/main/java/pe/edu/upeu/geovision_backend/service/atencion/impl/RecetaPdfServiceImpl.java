package pe.edu.upeu.geovision_backend.service.atencion.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaOjoDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaRepository;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaPdfService;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecetaPdfServiceImpl implements RecetaPdfService {

    private final RecetaService recetaService;
    private final ConfiguracionEmpresaRepository configuracionEmpresaRepository;

    public RecetaPdfServiceImpl(RecetaService recetaService,
                                ConfiguracionEmpresaRepository configuracionEmpresaRepository) {
        this.recetaService = recetaService;
        this.configuracionEmpresaRepository = configuracionEmpresaRepository;
    }

    @Override
    public byte[] generarRecetaPdf(Long idReceta) {
        RecetaResponseDTO receta = recetaService.obtenerRecetaPorId(idReceta);
        ConfiguracionEmpresa empresa = configuracionEmpresaRepository.findFirstByOrderByIdConfiguracionEmpresaAsc()
                .orElse(null);
        try (PDDocument documento = new PDDocument(); ByteArrayOutputStream salida = new ByteArrayOutputStream();
             PaginaPdf pagina = new PaginaPdf(documento)) {
            documento.getDocumentInformation().setTitle("Receta oftálmica #" + receta.idReceta());
            escribirCabecera(pagina, empresa);
            pagina.seccion("RECETA OFTÁLMICA #" + receta.idReceta());
            pagina.parrafo("Tipo: " + (Boolean.TRUE.equals(receta.esRecetaExterna())
                    ? "Prescripción externa" : "Examen visual interno"));
            pagina.parrafo("Fecha: " + (receta.fechaRegistro() == null ? "Sin registrar"
                    : receta.fechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            pagina.parrafo("Paciente: " + texto(receta.nombreCompletoPaciente()));
            pagina.parrafo("Documento: " + texto(receta.numeroDocumentoPaciente()));
            pagina.parrafo("Empleado responsable del registro: " + texto(receta.nombreCompletoEmpleado()));
            pagina.seccion("Medidas ópticas");
            if (receta.medidas().isEmpty()) {
                pagina.parrafo("No hay medidas registradas.");
            }
            for (MedidaResponseDTO medida : receta.medidas()) {
                escribirMedida(pagina, medida);
            }
            if (!receta.lentes().isEmpty()) {
                pagina.seccion("Lentes configurados");
                for (LenteResponseDTO lente : receta.lentes()) {
                    pagina.espacio(100);
                    pagina.parrafo("Lente #" + lente.idLente() + " - Material: " + texto(lente.nombreMaterialCristal()));
                    pagina.parrafo("Montura: " + (lente.idProductoMontura() == null ? "Propia del paciente"
                            : texto(lente.codigoMontura()) + " - " + texto(lente.nombreMontura())));
                    pagina.parrafo("Tratamientos: " + (lente.tratamientos().isEmpty() ? "Sin tratamientos"
                            : lente.tratamientos().stream().map(t -> t.nombre()).collect(Collectors.joining(", "))));
                    pagina.parrafo("Medidas asociadas: " + lente.medidas().stream()
                            .map(m -> "#" + m.idMedida() + " (" + tipoMedida(m) + ")").collect(Collectors.joining(", ")));
                }
            }
            pagina.terminar(receta.idReceta());
            documento.save(salida);
            return salida.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el PDF de la receta", ex);
        }
    }

    private void escribirCabecera(PaginaPdf pagina, ConfiguracionEmpresa empresa) throws IOException {
        if (empresa == null) {
            pagina.titulo("Óptica Geo Visión");
            return;
        }
        String nombre = empresa.getNombreComercial();
        pagina.titulo(nombre == null || nombre.isBlank() ? empresa.getNombreLegal() : nombre);
        if (nombre != null && !nombre.isBlank() && !nombre.equals(empresa.getNombreLegal())) {
            pagina.parrafo(empresa.getNombreLegal());
        }
        pagina.parrafo("RUC: " + texto(empresa.getRuc()));
        if (empresa.getDireccion() != null && !empresa.getDireccion().isBlank()) pagina.parrafo(empresa.getDireccion());
        if (empresa.getTelefono() != null && !empresa.getTelefono().isBlank()) pagina.parrafo("Teléfono: " + empresa.getTelefono());
        if (empresa.getCorreo() != null && !empresa.getCorreo().isBlank()) pagina.parrafo(empresa.getCorreo());
    }

    private void escribirMedida(PaginaPdf pagina, MedidaResponseDTO medida) throws IOException {
        String[] derecha = filaOjo("OD", medida.ojoDerecho());
        String[] izquierda = filaOjo("OI", medida.ojoIzquierdo());
        int lineasObservacion = pagina.lineas("Observaciones: " + texto(medida.observaciones()), pagina.regular,
                10, PaginaPdf.ANCHO).size();
        pagina.espacio(95 + pagina.altoFila(derecha) + pagina.altoFila(izquierda) + lineasObservacion * 14);
        pagina.parrafo(tipoMedida(medida) + " - Medida #" + medida.idMedida());
        pagina.fila(new String[]{"Ojo", "Agudeza visual", "Esfera", "Cilindro", "Eje", "Adición"}, true);
        pagina.fila(derecha, false);
        pagina.fila(izquierda, false);
        pagina.parrafo("Distancia pupilar: " + decimal(medida.distanciaPupilar())
                + "    Prisma: " + decimal(medida.prisma()));
        if (medida.observaciones() != null && !medida.observaciones().isBlank()) {
            pagina.parrafo("Observaciones: " + medida.observaciones());
        }
    }

    private String[] filaOjo(String lado, MedidaOjoDTO ojo) {
        if (ojo == null) return new String[]{lado, "Sin registrar", "-", "-", "-", "-"};
        return new String[]{lado, texto(ojo.agudezaVisual()), decimal(ojo.esfera()), decimal(ojo.cilindro()),
                ojo.eje() == null ? "-" : ojo.eje().toString(), decimal(ojo.adicion())};
    }

    private String tipoMedida(MedidaResponseDTO medida) {
        return medida.tipoMedida() == null ? "Sin tipo registrado" : medida.tipoMedida().getDescripcion();
    }

    private String decimal(BigDecimal valor) {
        return valor == null ? "-" : valor.toPlainString();
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    // Estado exclusivo de una generación: fuentes, posición y páginas nunca se comparten entre peticiones.
    private static class PaginaPdf implements AutoCloseable {
        private static final float MARGEN = 42;
        private static final float ANCHO = PDRectangle.A4.getWidth() - MARGEN * 2;
        private static final float[] COLUMNAS = {36, 139, 80, 80, 66, ANCHO - 401};
        private final PDDocument documento;
        private final PDFont regular;
        private final PDFont negrita;
        private PDPageContentStream contenido;
        private float y;

        PaginaPdf(PDDocument documento) throws IOException {
            this.documento = documento;
            try (InputStream normal = new ClassPathResource("fonts/NotoSans-Regular.ttf").getInputStream();
                 InputStream bold = new ClassPathResource("fonts/NotoSans-Bold.ttf").getInputStream()) {
                regular = PDType0Font.load(documento, normal);
                negrita = PDType0Font.load(documento, bold);
            }
            nuevaPagina();
        }

        private void nuevaPagina() throws IOException {
            close();
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            contenido = new PDPageContentStream(documento, pagina);
            y = PDRectangle.A4.getHeight() - MARGEN;
        }

        void espacio(float alto) throws IOException {
            if (y - alto < 65) nuevaPagina();
        }

        void titulo(String texto) throws IOException {
            for (String linea : lineas(texto, negrita, 17, ANCHO)) {
                espacio(25);
                escribir(linea, MARGEN, y, negrita, 17);
                y -= 24;
            }
            y -= 4;
        }

        void seccion(String titulo) throws IOException {
            espacio(70);
            y -= 12;
            contenido.setNonStrokingColor(0.08f, 0.26f, 0.34f);
            escribir(titulo, MARGEN, y, negrita, 12);
            contenido.setNonStrokingColor(0f, 0f, 0f);
            y -= 22;
        }

        void parrafo(String texto) throws IOException {
            for (String linea : lineas(texto, regular, 10, ANCHO)) {
                espacio(16);
                escribir(linea, MARGEN, y, regular, 10);
                y -= 14;
            }
            y -= 4;
        }

        float altoFila(String[] celdas) throws IOException {
            int cantidadLineas = 1;
            for (int i = 0; i < celdas.length; i++) {
                cantidadLineas = Math.max(cantidadLineas, lineas(celdas[i], regular, 9, COLUMNAS[i] - 12).size());
            }
            return cantidadLineas * 13 + 12;
        }

        void fila(String[] celdas, boolean cabecera) throws IOException {
            float alto = altoFila(celdas);
            espacio(alto);
            float x = MARGEN;
            for (int i = 0; i < celdas.length; i++) {
                contenido.setNonStrokingColor(cabecera ? 0.88f : 0.97f, cabecera ? 0.94f : 0.98f, cabecera ? 0.96f : 0.98f);
                contenido.addRect(x, y - alto, COLUMNAS[i], alto);
                contenido.fill();
                contenido.setNonStrokingColor(0f, 0f, 0f);
                float baseline = y - 16;
                for (String linea : lineas(celdas[i], cabecera ? negrita : regular, 9, COLUMNAS[i] - 12)) {
                    escribir(linea, x + 6, baseline, cabecera ? negrita : regular, 9);
                    baseline -= 13;
                }
                x += COLUMNAS[i];
            }
            y -= alto;
            if (!cabecera && "OI".equals(celdas[0])) y -= 14;
        }

        List<String> lineas(String texto, PDFont fuente, float tamanio, float ancho) throws IOException {
            // Representa los glifos no disponibles por su código Unicode, sin descartarlos.
            StringBuilder seguro = new StringBuilder();
            for (int code : texto.codePoints().toArray()) {
                if (code == '\n') { seguro.append('\n'); continue; }
                if (Character.isISOControl(code)) { seguro.append(' '); continue; }
                String caracter = new String(Character.toChars(code));
                try {
                    fuente.getStringWidth(caracter);
                    seguro.append(caracter);
                } catch (IllegalArgumentException ex) {
                    seguro.append(String.format("[U+%04X]", code));
                }
            }
            List<String> resultado = new ArrayList<>();
            for (String parrafo : seguro.toString().split("\\R", -1)) {
                StringBuilder linea = new StringBuilder();
                for (String palabra : parrafo.split("\\s+")) {
                    String candidato = linea.isEmpty() ? palabra : linea + " " + palabra;
                    if (fuente.getStringWidth(candidato) * tamanio / 1000 > ancho && !linea.isEmpty()) {
                        resultado.add(linea.toString());
                        linea.setLength(0);
                    }
                    if (!linea.isEmpty()) linea.append(' ');
                    for (int code : palabra.codePoints().toArray()) {
                        String caracter = new String(Character.toChars(code));
                        if (fuente.getStringWidth(linea + caracter) * tamanio / 1000 > ancho && !linea.isEmpty()) {
                            resultado.add(linea.toString());
                            linea.setLength(0);
                        }
                        linea.append(caracter);
                    }
                }
                resultado.add(linea.toString());
            }
            return resultado;
        }

        private void escribir(String texto, float x, float y, PDFont fuente, float tamanio) throws IOException {
            contenido.beginText();
            contenido.setFont(fuente, tamanio);
            contenido.newLineAtOffset(x, y);
            contenido.showText(texto);
            contenido.endText();
        }

        void terminar(Long idReceta) throws IOException {
            close();
            int total = documento.getNumberOfPages();
            for (int i = 0; i < total; i++) {
                try (PDPageContentStream pie = new PDPageContentStream(documento, documento.getPage(i),
                        PDPageContentStream.AppendMode.APPEND, true, true)) {
                    pie.beginText();
                    pie.setFont(regular, 8);
                    pie.newLineAtOffset(MARGEN, 34);
                    pie.showText("Receta #" + idReceta + " | Página " + (i + 1) + " de " + total);
                    pie.endText();
                }
            }
        }

        @Override
        public void close() throws IOException {
            if (contenido != null) {
                contenido.close();
                contenido = null;
            }
        }
    }
}
