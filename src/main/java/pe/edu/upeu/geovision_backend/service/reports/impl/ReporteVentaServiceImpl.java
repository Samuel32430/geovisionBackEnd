package pe.edu.upeu.geovision_backend.service.reports.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.reports.FiltroReporteVentasDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ItemRankingComercialDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ItemVentaReporteDTO;
import pe.edu.upeu.geovision_backend.dto.reports.KpiCardDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteRankingComercialDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteVentasPeriodoDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Servicio;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.ventas.ProductoVendido;
import pe.edu.upeu.geovision_backend.entity.ventas.ServicioVendido;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ProductoVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ServicioVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.reports.ReporteVentaService;
import pe.edu.upeu.geovision_backend.util.reports.ReportePdfUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteVentaServiceImpl implements ReporteVentaService {

    // >>>> CONSTANTS
    private static final Logger log = LoggerFactory.getLogger(ReporteVentaServiceImpl.class);

    // >>>> DEPENDENCIES
    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final ProductoVendidoRepository productoVendidoRepository;
    private final ServicioVendidoRepository servicioVendidoRepository;
    private final ConfiguracionEmpresaRepository configuracionEmpresaRepository;

    // >>>> QUERIES
    @Override
    @Transactional(readOnly = true)
    public ReporteVentasPeriodoDTO obtenerReporteVentasPeriodo(FiltroReporteVentasDTO filtro) {
        log.info("Generando reporte de ventas por periodo con filtros: {}", filtro);

        LocalDateTime inicio = (filtro != null && filtro.fechaInicio() != null)
                ? filtro.fechaInicio().atStartOfDay()
                : null;
        LocalDateTime fin = (filtro != null && filtro.fechaFin() != null)
                ? filtro.fechaFin().atTime(LocalTime.MAX)
                : null;

        List<Venta> todasLasVentas = ventaRepository.findAll();

        List<Venta> ventasFiltradas = todasLasVentas.stream()
                .filter(v -> inicio == null || !v.getFechaRegistro().isBefore(inicio))
                .filter(v -> fin == null || !v.getFechaRegistro().isAfter(fin))
                .filter(v -> filtro == null || filtro.idEmpleado() == null ||
                        (v.getEmpleado() != null && Objects.equals(v.getEmpleado().getId(), filtro.idEmpleado())))
                .filter(v -> filtro == null || filtro.tipoComprobante() == null ||
                        v.getTipoComprobante() == filtro.tipoComprobante())
                .filter(v -> filtro == null || filtro.estado() == null ||
                        v.getEstado() == filtro.estado())
                .sorted(Comparator.comparing(Venta::getFechaRegistro).reversed())
                .collect(Collectors.toList());

        BigDecimal totalVendido = BigDecimal.ZERO;
        BigDecimal totalCobrado = BigDecimal.ZERO;
        BigDecimal totalSaldo = BigDecimal.ZERO;
        Map<String, BigDecimal> totalPorMetodo = new LinkedHashMap<>();

        for (MetodoPagoEnum metodo : MetodoPagoEnum.values()) {
            totalPorMetodo.put(metodo.getDescripcion(), BigDecimal.ZERO);
        }

        List<ItemVentaReporteDTO> itemsVenta = new ArrayList<>();

        for (Venta v : ventasFiltradas) {
            BigDecimal ventaTotal = v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO;
            if (v.getEstado() != EstadoVentaEnum.ANULADA) {
                totalVendido = totalVendido.add(ventaTotal);
            }

            BigDecimal pagadoVenta = pagoRepository.sumMontoPagadoByVentaId(v.getId());
            if (pagadoVenta == null) pagadoVenta = BigDecimal.ZERO;

            if (v.getEstado() != EstadoVentaEnum.ANULADA) {
                totalCobrado = totalCobrado.add(pagadoVenta);
                BigDecimal saldo = ventaTotal.subtract(pagadoVenta);
                if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                    totalSaldo = totalSaldo.add(saldo);
                }
            }

            List<Pago> pagos = pagoRepository.findByVentaId(v.getId());
            for (Pago p : pagos) {
                if (p.getMetodoPago() != null && p.getMonto() != null) {
                    String nombreMetodo = p.getMetodoPago().getDescripcion();
                    totalPorMetodo.put(nombreMetodo, totalPorMetodo.getOrDefault(nombreMetodo, BigDecimal.ZERO).add(p.getMonto()));
                }
            }

            BigDecimal saldoVenta = (v.getEstado() == EstadoVentaEnum.ANULADA)
                    ? BigDecimal.ZERO
                    : (ventaTotal.subtract(pagadoVenta).max(BigDecimal.ZERO));

            String condicionPago = (v.getEstado() == EstadoVentaEnum.PAGADA || saldoVenta.compareTo(BigDecimal.ZERO) == 0)
                    ? "CONTADO"
                    : "CREDITO";

            String docCliente = null;
            String nombreCliente = null;
            if (v.getPaciente() != null && v.getPaciente().getPersona() != null) {
                docCliente = v.getPaciente().getPersona().getNumeroDocumento();
                nombreCliente = v.getPaciente().getPersona().getNombres() + " " + v.getPaciente().getPersona().getApellidos();
            }

            String nombreVendedor = (v.getEmpleado() != null && v.getEmpleado().getPersona() != null)
                    ? v.getEmpleado().getPersona().getNombres() + " " + v.getEmpleado().getPersona().getApellidos()
                    : "SISTEMA";

            int cantidadProductos = productoVendidoRepository.findByVentaId(v.getId()).size();
            int cantidadServicios = servicioVendidoRepository.findByVentaId(v.getId()).size();

            itemsVenta.add(new ItemVentaReporteDTO(
                    v.getId(),
                    v.getFechaRegistro(),
                    v.getNumeroComprobante(),
                    v.getTipoComprobante(),
                    v.getEstado(),
                    ventaTotal,
                    pagadoVenta,
                    saldoVenta,
                    condicionPago,
                    docCliente,
                    nombreCliente,
                    nombreVendedor,
                    cantidadProductos + cantidadServicios
            ));
        }

        long totalVentas = ventasFiltradas.stream().filter(v -> v.getEstado() != EstadoVentaEnum.ANULADA).count();
        BigDecimal ticketPromedio = totalVentas > 0
                ? totalVendido.divide(BigDecimal.valueOf(totalVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<KpiCardDTO> kpis = List.of(
                new KpiCardDTO("TOTAL VENDIDO", "S/ " + totalVendido.setScale(2, RoundingMode.HALF_UP), "Facturación neta acumulada"),
                new KpiCardDTO("TOTAL RECAUDADO", "S/ " + totalCobrado.setScale(2, RoundingMode.HALF_UP), "Cobranza efectiva de caja"),
                new KpiCardDTO("SALDO PENDIENTE", "S/ " + totalSaldo.setScale(2, RoundingMode.HALF_UP), "Por cobrar a clientes"),
                new KpiCardDTO("COMPROBANTES", String.valueOf(totalVentas), "Ventas emitidas"),
                new KpiCardDTO("TICKET PROMEDIO", "S/ " + ticketPromedio, "Promedio por transacción")
        );

        return new ReporteVentasPeriodoDTO(
                kpis,
                totalVendido.setScale(2, RoundingMode.HALF_UP),
                totalCobrado.setScale(2, RoundingMode.HALF_UP),
                totalSaldo.setScale(2, RoundingMode.HALF_UP),
                totalVentas,
                ticketPromedio,
                totalPorMetodo,
                itemsVenta
        );
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarReporteVentasPeriodoPdf(FiltroReporteVentasDTO filtro) {
        ReporteVentasPeriodoDTO datos = obtenerReporteVentasPeriodo(filtro);
        ConfiguracionEmpresa empresa = configuracionEmpresaRepository.findFirstByOrderByIdAsc().orElse(null);

        try {
            ReportePdfUtil pdf = new ReportePdfUtil();
            String periodoTexto = construirTextoPeriodo(filtro);
            pdf.dibujarEncabezado(empresa, "REPORTE DE VENTAS Y RECAUDACIÓN POR PERIODO", periodoTexto);
            pdf.dibujarTarjetasKpi(datos.kpis());

            List<String> encabezados = List.of("Comprobante", "Fecha", "Cliente", "Condición", "Total (S/)", "Cobrado (S/)", "Saldo (S/)", "Estado");
            float[] anchos = new float[]{80f, 65f, 130f, 55f, 50f, 50f, 50f, 55f};

            List<List<String>> filas = new ArrayList<>();
            for (ItemVentaReporteDTO v : datos.ventas()) {
                filas.add(List.of(
                        v.numeroComprobante() != null ? v.numeroComprobante() : "-",
                        v.fechaRegistro() != null ? v.fechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-",
                        v.nombreCliente() != null ? v.nombreCliente() : "PÚBLICO GENERAL",
                        v.condicionPago(),
                        "S/ " + v.total().setScale(2, RoundingMode.HALF_UP),
                        "S/ " + v.totalPagado().setScale(2, RoundingMode.HALF_UP),
                        "S/ " + v.saldoPendiente().setScale(2, RoundingMode.HALF_UP),
                        v.estado().name()
                ));
            }

            pdf.dibujarTabla(encabezados, anchos, filas);
            return pdf.exportarPdf();
        } catch (IOException ex) {
            log.error("Error al exportar reporte de ventas por periodo a PDF", ex);
            throw new IllegalStateException("Error al generar el archivo PDF de ventas", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteRankingComercialDTO obtenerReporteRankingComercial(FiltroReporteVentasDTO filtro) {
        log.info("Generando reporte de ranking comercial de productos y servicios con filtros: {}", filtro);

        LocalDateTime inicio = (filtro != null && filtro.fechaInicio() != null)
                ? filtro.fechaInicio().atStartOfDay()
                : null;
        LocalDateTime fin = (filtro != null && filtro.fechaFin() != null)
                ? filtro.fechaFin().atTime(LocalTime.MAX)
                : null;

        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getEstado() != EstadoVentaEnum.ANULADA)
                .filter(v -> inicio == null || !v.getFechaRegistro().isBefore(inicio))
                .filter(v -> fin == null || !v.getFechaRegistro().isAfter(fin))
                .toList();

        Map<Long, ItemRankingComercialMutable> productosMap = new LinkedHashMap<>();
        Map<Long, ItemRankingComercialMutable> serviciosMap = new LinkedHashMap<>();

        for (Venta v : ventas) {
            List<ProductoVendido> prods = productoVendidoRepository.findByVentaId(v.getId());
            for (ProductoVendido pv : prods) {
                Producto p = pv.getProducto();
                if (p != null) {
                    ItemRankingComercialMutable item = productosMap.computeIfAbsent(p.getId(), id ->
                            new ItemRankingComercialMutable(
                                    "PRODUCTO",
                                    p.getId(),
                                    p.getCodigo(),
                                    p.getNombre(),
                                    p.getCategoria() != null ? p.getCategoria().getNombre() : "SIN CATEGORÍA",
                                    p.getPrecio(),
                                    0L,
                                    BigDecimal.ZERO
                            ));

                    long cantidad = pv.getCantidad() != null ? pv.getCantidad() : 1L;
                    BigDecimal totalItem = pv.getSubTotal() != null ? pv.getSubTotal() : p.getPrecio().multiply(BigDecimal.valueOf(cantidad));
                    item.unidadesVendidas += cantidad;
                    item.totalRecaudado = item.totalRecaudado.add(totalItem);
                }
            }

            List<ServicioVendido> servs = servicioVendidoRepository.findByVentaId(v.getId());
            for (ServicioVendido sv : servs) {
                Servicio s = sv.getServicio();
                if (s != null) {
                    ItemRankingComercialMutable item = serviciosMap.computeIfAbsent(s.getId(), id ->
                            new ItemRankingComercialMutable(
                                    "SERVICIO",
                                    s.getId(),
                                    "SERV-" + s.getId(),
                                    s.getNombre(),
                                    "SERVICIO PROFESIONAL",
                                    s.getPrecio(),
                                    0L,
                                    BigDecimal.ZERO
                            ));

                    BigDecimal precio = sv.getPrecio() != null ? sv.getPrecio() : s.getPrecio();
                    item.unidadesVendidas += 1L;
                    item.totalRecaudado = item.totalRecaudado.add(precio);
                }
            }
        }

        List<ItemRankingComercialMutable> todosLosItems = new ArrayList<>();
        todosLosItems.addAll(productosMap.values());
        todosLosItems.addAll(serviciosMap.values());

        todosLosItems.sort(Comparator.comparing(ItemRankingComercialMutable::getTotalRecaudado).reversed());

        long totalUnidades = 0;
        BigDecimal totalGeneral = BigDecimal.ZERO;
        List<ItemRankingComercialDTO> itemsDTO = new ArrayList<>();
        int rank = 1;
        for (ItemRankingComercialMutable item : todosLosItems) {
            totalUnidades += item.unidadesVendidas;
            totalGeneral = totalGeneral.add(item.totalRecaudado);
            itemsDTO.add(new ItemRankingComercialDTO(
                    rank++,
                    item.tipoItem,
                    item.idItem,
                    item.codigo,
                    item.nombre,
                    item.categoria,
                    item.precioUnitario,
                    item.unidadesVendidas,
                    item.totalRecaudado.setScale(2, RoundingMode.HALF_UP)
            ));
        }

        List<KpiCardDTO> kpis = List.of(
                new KpiCardDTO("TOTAL RECAUDADO (ITEMS)", "S/ " + totalGeneral.setScale(2, RoundingMode.HALF_UP), "Venta de productos y servicios"),
                new KpiCardDTO("UNIDADES VENDIDAS", String.valueOf(totalUnidades), "Productos y atenciones"),
                new KpiCardDTO("ÍTEMS CON ROTACIÓN", String.valueOf(todosLosItems.size()), "En el catálogo comercial")
        );

        return new ReporteRankingComercialDTO(
                kpis,
                totalUnidades,
                totalGeneral.setScale(2, RoundingMode.HALF_UP),
                itemsDTO
        );
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarReporteRankingComercialPdf(FiltroReporteVentasDTO filtro) {
        ReporteRankingComercialDTO datos = obtenerReporteRankingComercial(filtro);
        ConfiguracionEmpresa empresa = configuracionEmpresaRepository.findFirstByOrderByIdAsc().orElse(null);

        try {
            ReportePdfUtil pdf = new ReportePdfUtil();
            String periodoTexto = construirTextoPeriodo(filtro);
            pdf.dibujarEncabezado(empresa, "RANKING DE PRODUCTOS Y SERVICIOS MÁS VENDIDOS", periodoTexto);
            pdf.dibujarTarjetasKpi(datos.kpis());

            List<String> encabezados = List.of("Rank", "Tipo", "Código", "Descripción", "Categoría", "Precio Unit.", "Cant.", "Total (S/)");
            float[] anchos = new float[]{35f, 65f, 65f, 130f, 90f, 60f, 40f, 65f};

            List<List<String>> filas = new ArrayList<>();
            for (ItemRankingComercialDTO item : datos.items()) {
                filas.add(List.of(
                        "#" + item.ranking(),
                        item.tipoItem(),
                        item.codigo(),
                        item.nombre(),
                        item.categoria(),
                        "S/ " + item.precioUnitario().setScale(2, RoundingMode.HALF_UP),
                        String.valueOf(item.unidadesVendidas()),
                        "S/ " + item.totalRecaudado().setScale(2, RoundingMode.HALF_UP)
                ));
            }

            pdf.dibujarTabla(encabezados, anchos, filas);
            return pdf.exportarPdf();
        } catch (IOException ex) {
            log.error("Error al exportar ranking comercial a PDF", ex);
            throw new IllegalStateException("Error al generar el archivo PDF de ranking comercial", ex);
        }
    }

    // >>>> HELPERS
    private String construirTextoPeriodo(FiltroReporteVentasDTO filtro) {
        if (filtro == null || (filtro.fechaInicio() == null && filtro.fechaFin() == null)) {
            return "Periodo: Histórico Completo";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String desde = filtro.fechaInicio() != null ? filtro.fechaInicio().format(fmt) : "Inicio";
        String hasta = filtro.fechaFin() != null ? filtro.fechaFin().format(fmt) : "Actualidad";
        return "Periodo filtrado: Del " + desde + " al " + hasta;
    }

    private static class ItemRankingComercialMutable {
        String tipoItem;
        Long idItem;
        String codigo;
        String nombre;
        String categoria;
        BigDecimal precioUnitario;
        long unidadesVendidas;
        BigDecimal totalRecaudado;

        ItemRankingComercialMutable(String tipoItem, Long idItem, String codigo, String nombre,
                                   String categoria, BigDecimal precioUnitario,
                                   long unidadesVendidas, BigDecimal totalRecaudado) {
            this.tipoItem = tipoItem;
            this.idItem = idItem;
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.precioUnitario = precioUnitario;
            this.unidadesVendidas = unidadesVendidas;
            this.totalRecaudado = totalRecaudado;
        }

        BigDecimal getTotalRecaudado() {
            return totalRecaudado;
        }
    }

}
