package pe.edu.upeu.geovisionBackEnd.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.entity.ConfiguracionEmpresa;
import pe.edu.upeu.geovisionBackEnd.enums.TipoMedidaEnum;
import pe.edu.upeu.geovisionBackEnd.repository.ConfiguracionEmpresaRepository;
import pe.edu.upeu.geovisionBackEnd.service.impl.RecetaPdfServiceImpl;
import pe.edu.upeu.geovisionBackEnd.service.service.RecetaService;

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
        when(companies.findFirstByOrderByIdConfiguracionEmpresaAsc()).thenReturn(Optional.empty());
    }

    RecetaResponseDTO recipe(long id) {
        var far = MedidaResponseDTO.builder().idMedida(20L).tipoMedida(TipoMedidaEnum.LEJOS)
                .distanciaPupilar(new BigDecimal("62.50")).prisma(new BigDecimal("0.30"))
                .observaciones("Uso permanente. Material recomendado: policarbonato.")
                .ojoDerecho(MedidaOjoDTO.builder().agudezaVisual("20/20").esfera(new BigDecimal("-1.25"))
                        .cilindro(new BigDecimal("-0.50")).eje(90).adicion(new BigDecimal("1.00")).build())
                .ojoIzquierdo(MedidaOjoDTO.builder().agudezaVisual("20/25").esfera(new BigDecimal("2.50"))
                        .cilindro(BigDecimal.ZERO).eje(80).build()).build();
        var near = MedidaResponseDTO.builder().idMedida(21L).tipoMedida(TipoMedidaEnum.CERCA)
                .distanciaPupilar(new BigDecimal("62.50")).ojoDerecho(far.getOjoDerecho())
                .ojoIzquierdo(far.getOjoIzquierdo()).observaciones("Lectura y trabajo de cerca.").build();
        return RecetaResponseDTO.builder().idReceta(id).fechaRegistro(LocalDateTime.of(2026, 11, 1, 10, 15))
                .esExterno(0).nombreCompletoPaciente("Ana María Muñoz Peña")
                .numeroDocumentoPaciente("72458912").nombreCompletoEmpleado("José Núñez")
                .medidas(new ArrayList<>(List.of(far, near)))
                .lentes(List.of(LenteResponseDTO.builder().idLente(7L).nombreMaterialCristal("Policarbonato")
                        .medidas(List.of(far)).tratamientos(List.of(TratamientoCristalDTO.builder()
                                .nombre("Antirreflejo").build())).build())).build();
    }

    @Test
    void generaPdfConFuentesIncluidasYContenidoClinicoConAcentos() throws Exception {
        when(prescriptions.obtenerRecetaPorId(105L)).thenReturn(recipe(105));
        when(companies.findFirstByOrderByIdConfiguracionEmpresaAsc()).thenReturn(Optional.of(
                ConfiguracionEmpresa.builder().nombreComercial("Óptica Geo Visión")
                        .nombreLegal("Geo Visión - Datos de prueba").ruc("20123456789")
                        .direccion("Av. Las Palmeras 450, Lima").telefono("987654321")
                        .correo("prueba@example.invalid").build()));
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
        recipe.setEsExterno(1);
        recipe.setMedidas(List.of(MedidaResponseDTO.builder().idMedida(1L).build()));
        recipe.setLentes(List.of());
        when(prescriptions.obtenerRecetaPorId(106L)).thenReturn(recipe);
        try (PDDocument document = Loader.loadPDF(service.generarRecetaPdf(106L))) {
            assertThat(new PDFTextStripper().getText(document)).contains("Prescripción externa", "Sin registrar",
                    "Sin tipo registrado", "Óptica Geo Visión").doesNotContain("RUC:", "null");
        }
    }

    @Test
    void textoLargoYSinEspaciosSeAjustaYSeConservaEnVariasPaginas() throws Exception {
        var recipe = recipe(107);
        recipe.setNombreCompletoPaciente("Nombre de prueba " + "Á".repeat(150));
        List<MedidaResponseDTO> measurements = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            var measure = recipe.getMedidas().getFirst();
            measurements.add(MedidaResponseDTO.builder().idMedida((long) i).tipoMedida(TipoMedidaEnum.MEDIO)
                    .ojoDerecho(measure.getOjoDerecho()).ojoIzquierdo(measure.getOjoIzquierdo())
                    .observaciones("Observación " + i + ": " + "Texto clínico extenso con acentos. ".repeat(6))
                    .build());
        }
        recipe.setMedidas(measurements);
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
        recipe.getMedidas().getFirst().setObservaciones("Control Ω y símbolo 😀");
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
