package pe.edu.upeu.geovision_backend.service.atencion;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaOjoDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaRepository;
import pe.edu.upeu.geovision_backend.service.atencion.impl.RecetaPdfServiceImpl;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaService;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecetaPdfServiceTest {

    RecetaService prescriptions;
    ConfiguracionEmpresaRepository companies;
    RecetaPdfServiceImpl service;

    @BeforeEach
    void setup() {
        prescriptions = mock(RecetaService.class);
        companies = mock(ConfiguracionEmpresaRepository.class);
        service = new RecetaPdfServiceImpl(prescriptions, companies);
        when(companies.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
    }

    RecetaResponseDTO recipe(long id) {
        var far = new MedidaResponseDTO(20L, TipoMedidaEnum.LEJOS, new BigDecimal("62.50"), new BigDecimal("0.30"), "Uso permanente. Material recomendado: policarbonato.", null, new MedidaOjoDTO(null, "20/20", new BigDecimal("-1.25"), new BigDecimal("-0.50"), 90, new BigDecimal("1.00")), new MedidaOjoDTO(null, "20/25", new BigDecimal("2.50"), BigDecimal.ZERO, 80, null), null);
        var near = new MedidaResponseDTO(21L, TipoMedidaEnum.CERCA, new BigDecimal("62.50"), null, "Lectura y trabajo de cerca.", null, far.ojoDerecho(), far.ojoIzquierdo(), null);
        return new RecetaResponseDTO(id, LocalDateTime.of(2026, 11, 1, 10, 15), 0, null, null, "Ana María Muñoz Peña", "72458912", null, "José Núñez", new ArrayList<>(List.of(far, near)), List.of(new LenteResponseDTO(7L, null, null, null, "Policarbonato", null, null, null, List.of(new TratamientoCristalDTO(null, "Antirreflejo")), List.of(far))));
    }

    @Test
    void generaPdfConFuentesIncluidasYContenidoClinicoConAcentos() throws Exception {
        when(prescriptions.obtenerRecetaPorId(105L)).thenReturn(recipe(105));
        when(companies.findFirstByOrderByIdAsc()).thenReturn(Optional.of(
                new ConfiguracionEmpresa(null, "Geo Visión - Datos de prueba", "Óptica Geo Visión", "20123456789", "Av. Las Palmeras 450, Lima", "987654321", "prueba@example.invalid", null)));
        byte[] bytes = service.generarRecetaPdf(105L);
        Files.write(Path.of("target/stage6-preview.pdf"), bytes);
        try (PDDocument document = Loader.loadPDF(bytes)) {
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("Óptica Geo Visión", "20123456789", "RECETA OFTÁLMICA #105",
                    "Ana María Muñoz Peña", "José Núñez", "72458912", "01/11/2026 10:15",
                    "Lejos", "Cerca", "OD", "OI", "-1.25", "-0.50", "62.50", "0.30",
                    "Material recomendado: policarbonato", "Antirreflejo", "Propia del paciente", "Página 1 de");
            for (var page : document.getPages()) {
                for (var name : page.getResources().getFontNames()) {
                    assertThat(page.getResources().getFont(name).isEmbedded()).isTrue();
                }
            }
        }
    }

    @Test
    void datosOpcionalesAusentesNoSeInventanYTipoExternoSeDistingue() throws Exception {
        var recipe = recipe(106);
        recipe = new RecetaResponseDTO(recipe.id(), recipe.fechaRegistro(), 1, null, recipe.idPaciente(), recipe.nombreCompletoPaciente(), recipe.numeroDocumentoPaciente(), recipe.idEmpleado(), recipe.nombreCompletoEmpleado(), recipe.medidas(), recipe.lentes());
        recipe = new RecetaResponseDTO(recipe.id(), recipe.fechaRegistro(), recipe.esExterno(), recipe.esRecetaExterna(), recipe.idPaciente(), recipe.nombreCompletoPaciente(), recipe.numeroDocumentoPaciente(), recipe.idEmpleado(), recipe.nombreCompletoEmpleado(), List.of(new MedidaResponseDTO(1L, null, null, null, null, null, null, null, null)), recipe.lentes());
        recipe = new RecetaResponseDTO(recipe.id(), recipe.fechaRegistro(), recipe.esExterno(), recipe.esRecetaExterna(), recipe.idPaciente(), recipe.nombreCompletoPaciente(), recipe.numeroDocumentoPaciente(), recipe.idEmpleado(), recipe.nombreCompletoEmpleado(), recipe.medidas(), List.of());
        when(prescriptions.obtenerRecetaPorId(106L)).thenReturn(recipe);
        try (PDDocument document = Loader.loadPDF(service.generarRecetaPdf(106L))) {
            assertThat(new PDFTextStripper().getText(document)).contains("Prescripción externa", "Sin registrar",
                    "Sin tipo registrado", "Óptica Geo Visión").doesNotContain("RUC:", "null");
        }
    }

    @Test
    void textoLargoYSinEspaciosSeAjustaYSeConservaEnVariasPaginas() throws Exception {
        var recipe = recipe(107);
        recipe = new RecetaResponseDTO(recipe.id(), recipe.fechaRegistro(), recipe.esExterno(), recipe.esRecetaExterna(), recipe.idPaciente(), "Nombre de prueba " + "Á".repeat(150), recipe.numeroDocumentoPaciente(), recipe.idEmpleado(), recipe.nombreCompletoEmpleado(), recipe.medidas(), recipe.lentes());
        List<MedidaResponseDTO> measurements = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            var measure = recipe.medidas().getFirst();
            measurements.add(new MedidaResponseDTO((long) i, TipoMedidaEnum.MEDIO, null, null, "Observación " + i + ": " + "Texto clínico extenso con acentos. ".repeat(6), null, measure.ojoDerecho(), measure.ojoIzquierdo(), null));
        }
        recipe = new RecetaResponseDTO(recipe.id(), recipe.fechaRegistro(), recipe.esExterno(), recipe.esRecetaExterna(), recipe.idPaciente(), recipe.nombreCompletoPaciente(), recipe.numeroDocumentoPaciente(), recipe.idEmpleado(), recipe.nombreCompletoEmpleado(), measurements, recipe.lentes());
        when(prescriptions.obtenerRecetaPorId(107L)).thenReturn(recipe);
        byte[] bytes = service.generarRecetaPdf(107L);
        Files.write(Path.of("target/stage6-long-preview.pdf"), bytes);
        try (PDDocument document = Loader.loadPDF(bytes)) {
            assertThat(document.getNumberOfPages()).isGreaterThan(1);
            String text = new PDFTextStripper().getText(document);
            for (int i = 0; i < 12; i++) assertThat(text).contains("Observación " + i + ":");
            assertThat(text).contains("Página " + document.getNumberOfPages() + " de " + document.getNumberOfPages());
            var bounds = new PDFTextStripper() {
                @Override
                protected void processTextPosition(org.apache.pdfbox.text.TextPosition position) {
                    assertThat(position.getXDirAdj()).isGreaterThanOrEqualTo(41f);
                    assertThat(position.getXDirAdj() + position.getWidthDirAdj()).isLessThanOrEqualTo(554f);
                    assertThat(position.getYDirAdj()).isBetween(35f, 812f);
                    super.processTextPosition(position);
                }
            };
            bounds.getText(document);
        }
    }

    @Test
    void caracteresSinGlifoNoSePierdenNiRompenGeneracion() throws Exception {
        var recipe = recipe(108);
        recipe.medidas().set(0, new MedidaResponseDTO(recipe.medidas().getFirst().id(), recipe.medidas().getFirst().tipoMedida(), recipe.medidas().getFirst().distanciaPupilar(), recipe.medidas().getFirst().prisma(), "Control Ω y símbolo 😀", recipe.medidas().getFirst().idReceta(), recipe.medidas().getFirst().ojoDerecho(), recipe.medidas().getFirst().ojoIzquierdo(), recipe.medidas().getFirst().idEnvioLaboratorio()));
        when(prescriptions.obtenerRecetaPorId(108L)).thenReturn(recipe);
        try (PDDocument document = Loader.loadPDF(service.generarRecetaPdf(108L))) {
            assertThat(new PDFTextStripper().getText(document)).contains("Control Ω", "[U+1F600]");
        }
    }

    @Test
    void peticionesSimultaneasNoCompartenEstadoNiDatos() throws Exception {
        for (long id = 201; id <= 204; id++) when(prescriptions.obtenerRecetaPorId(id)).thenReturn(recipe(id));
        try (var executor = Executors.newFixedThreadPool(4)) {
            List<Callable<byte[]>> tasks = new ArrayList<>();
            for (long id = 201; id <= 204; id++) {
                long current = id;
                tasks.add(() -> service.generarRecetaPdf(current));
            }
            var results = executor.invokeAll(tasks);
            for (int i = 0; i < results.size(); i++) {
                try (PDDocument document = Loader.loadPDF(results.get(i).get())) {
                    String text = new PDFTextStripper().getText(document);
                    assertThat(text).contains("RECETA OFTÁLMICA #" + (201 + i));
                    for (int j = 0; j < results.size(); j++) {
                        if (j != i) assertThat(text).doesNotContain("RECETA OFTÁLMICA #" + (201 + j));
                    }
                }
            }
        }
    }
}
