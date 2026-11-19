package pe.edu.upeu.geovision_backend.util.reports;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ProductoVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ServicioVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComprobantePdfUtilTest {

    @Test
    @DisplayName("Generar comprobante PDF con datos completos retorna bytes de PDF válidos (%PDF-1.x)")
    void generarComprobante_conDatosCompletos_retornaBytesValidos() throws IOException {
        ComprobantePdfUtil util = new ComprobantePdfUtil();

        ConfiguracionEmpresaDTO empresa = new ConfiguracionEmpresaDTO(
                1L,
                "GEOVISION OPTICA INTEGRAL S.A.C.",
                "GeoVision Óptica & Laboratorio",
                "20608945123",
                "Av. Javier Prado Este 2450, San Borja, Lima",
                "+51 1 445-8920",
                "contacto@geovision.pe",
                "/assets/images/logo_geovision.png"
        );

        List<ProductoVendidoDTO> productos = List.of(
                new ProductoVendidoDTO(1L, 10L, "MON-RAY-001", "Montura Ray-Ban Aviador Metal", 1, new BigDecimal("320.00"), new BigDecimal("320.00"))
        );

        List<ServicioVendidoDTO> servicios = List.of(
                new ServicioVendidoDTO(1L, 5L, "Examen de Refracción Computarizada", new BigDecimal("50.00"))
        );

        List<PagoResponseDTO> pagos = List.of(
                new PagoResponseDTO(1L, 1L, new BigDecimal("370.00"), MetodoPagoEnum.QR_YAPE_PLIN, "Yape / Plin", "OP-987654", LocalDateTime.now())
        );

        VentaResponseDTO venta = new VentaResponseDTO(
                1L,
                LocalDateTime.now(),
                new BigDecimal("370.00"),
                new BigDecimal("370.00"),
                BigDecimal.ZERO,
                EstadoVentaEnum.PAGADA,
                "Pagada Totalmente",
                TipoComprobanteEnum.BOLETA,
                "Boleta de Venta",
                "B001-09367415",
                2L,
                "Samuel Vilca Salinas",
                10L,
                "Carlos Mendoza Quispe",
                3L,
                productos,
                servicios,
                pagos,
                empresa
        );

        byte[] pdfBytes = util.generarComprobante(venta);

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);

        String cabeceraPdf = new String(pdfBytes, 0, 4);
        assertThat(cabeceraPdf).isEqualTo("%PDF");
    }
}
