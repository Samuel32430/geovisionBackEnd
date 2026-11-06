package pe.edu.upeu.geovisionBackEnd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.KpiCardDTO;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.*;
import pe.edu.upeu.geovisionBackEnd.entity.*;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.repository.*;
import pe.edu.upeu.geovisionBackEnd.service.service.ReporteVentaService;
import pe.edu.upeu.geovisionBackEnd.util.ReportePdfUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReporteVentaServiceImpl implements ReporteVentaService {

    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final ProductoVendidoRepository productoVendidoRepository;
    private final ServicioVendidoRepository servicioVendidoRepository;
    private final ConfiguracionEmpresaRepository configuracionEmpresaRepository;

    @Override
    public ReporteVentasPeriodoDTO obtenerReporteVentasPeriodo(FiltroReporteVentasDTO filtro) {
        log.info("Generando reporte de ventas por periodo con filtros: {}", filtro);

        LocalDateTime inicio = (filtro != null && filtro.getFechaInicio() != null)
                ? filtro.getFechaInicio().atStartOfDay()
                : null;
        LocalDateTime fin = (filtro != null && filtro.getFechaFin() != null)
                ? filtro.getFechaFin().atTime(LocalTime.MAX)
                : null;

        List<Venta> todasLasVentas = ventaRepository.findAll();

        // Aplicar filtros en memoria (soporta pruebas unitarias y producción)
        List<Venta> ventasFiltradas = todasLasVentas.stream()
                .filter(v -> inicio == null || !v.getFechaRegistro().isBefore(inicio))
                .filter(v -> fin == null || !v.getFechaRegistro().isAfter(fin))
                .filter(v -> filtro == null || filtro.getIdEmpleado() == null ||
                        (v.getEmpleado() != null && Objects.equals(v.getEmpleado().getIdPersona(), filtro.getIdEmpleado())))
                .filter(v -> filtro == null || filtro.getTipoComprobante() == null ||
                        v.getTipoComprobante() == filtro.getTipoComprobante())
                .filter(v -> filtro == null || filtro.getEstado() == null ||
                        v.getEstado() == filtro.getEstado())
                .sorted(Comparator.comparing(Venta::getFechaRegistro).reversed())
                .collect(Collectors.toList());

        BigDecimal totalVendido = BigDecimal.ZERO;
        BigDecimal totalCobrado = BigDecimal.ZERO;
        BigDecimal totalSaldo = BigDecimal.ZERO;
        Map<String, BigDecimal> totalPorMetodo = new LinkedHashMap<>();

        // Inicializar métodos de pago comunes
        for (MetodoPagoEnum metodo : MetodoPagoEnum.values()) {
            totalPorMetodo.put(metodo.getDescripcion(), BigDecimal.ZERO);
        }

        List<ItemVentaReporteDTO> itemsVenta = new ArrayList<>();

        for (Venta v : ventasFiltradas) {
            BigDecimal ventaTotal = v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO;
            if (v.getEstado() != EstadoVentaEnum.ANULADA) {
                totalVendido = totalVendido.add(ventaTotal);
            }

            BigDecimal pagadoVenta = pagoRepository.sumMontoPagadoByVentaId(v.getIdVenta());
            if (pagadoVenta == null) pagadoVenta = BigDecimal.ZERO;

            if (v.getEstado() != EstadoVentaEnum.ANULADA) {
                totalCobrado = totalCobrado.add(pagadoVenta);
                BigDecimal saldo = ventaTotal.subtract(pagadoVenta);
                if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                    totalSaldo = totalSaldo.add(saldo);
                }
            }

            // Desglose por método de pago de los pagos asociados
            List<Pago> pagos = pagoRepository.findByVentaIdVenta(v.getIdVenta());
            for (Pago p : pagos) {
                if (p.getMetodoPago() != null && p.getMonto() != null) {
                    String nombreMetodo = p.getMetodoPago().getDescripcion();
                    totalPorMetodo.put(nombreMetodo, totalPorMetodo.getOrDefault(nombreMetodo, BigDecimal.ZERO).add(p.getMonto()));
                }
            }

            // Mapeo del ítem
            BigDecimal saldoVenta = (v.getEstado() == EstadoVentaEnum.ANULADA)
                    ? BigDecimal.ZERO
                    : ventaTotal.subtract(pagadoVenta).max(BigDecimal.ZERO);

            String condicionPago = (v.getEstado() == EstadoVentaEnum.ANULADA) ? "ANULADA"
                    : (saldoVenta.compareTo(BigDecimal.ZERO) == 0 ? "CANCELADO" : (pagadoVenta.compareTo(BigDecimal.ZERO) > 0 ? "ADELANTO" : "SIN PAGO"));

            String nombreCliente = "-";
            String docCliente = "-";
            if (v.getPaciente() != null && v.getPaciente().getPersona() != null) {
                Persona p = v.getPaciente().getPersona();
                nombreCliente = p.getNombres() + " " + p.getApellidos();
                docCliente = p.getNumeroDocumento();
            }

            String nombreVendedor = "-";
            if (v.getEmpleado() != null && v.getEmpleado().getPersona() != null) {
                Persona e = v.getEmpleado().getPersona();
                nombreVendedor = e.getNombres() + " " + e.getApellidos();
            }

            int items = productoVendidoRepository.findByVentaIdVenta(v.getIdVenta()).size() +
                    servicioVendidoRepository.findByVentaIdVenta(v.getIdVenta()).size();

            itemsVenta.add(ItemVentaReporteDTO.builder()
                    .idVenta(v.getIdVenta())
                    .fechaRegistro(v.getFechaRegistro())
                    .numeroComprobante(v.getNumeroComprobante() != null ? v.getNumeroComprobante() : "-")
                    .tipoComprobante(v.getTipoComprobante() != null ? v.getTipoComprobante().getDescripcion() : "-")
                    .estado(v.getEstado() != null ? v.getEstado().getDescripcion() : "-")
                    .total(ventaTotal)
                    .totalPagado(pagadoVenta)
                    .saldoPendiente(saldoVenta)
                    .condicionPago(condicionPago)
                    .docCliente(docCliente)
                    .nombreCliente(nombreCliente)
                    .nombreVendedor(nombreVendedor)
                    .cantidadItems(items)
                    .build());
        }

        int cantidadVentas = ventasFiltradas.size();
        BigDecimal ticketPromedio = cantidadVentas > 0
                ? totalVendido.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Tarjetas KPIs
        List<KpiCardDTO> kpis = List.of(
                KpiCardDTO.builder()
                        .titulo("TOTAL FACTURADO")
                        .valor("S/ " + totalVendido.setScale(2, RoundingMode.HALF_UP))
                        .subtitulo(cantidadVentas + " transacciones")
                        .build(),
                KpiCardDTO.builder()
                        .titulo("TOTAL COBRADO")
                        .valor("S/ " + totalCobrado.setScale(2, RoundingMode.HALF_UP))
                        .subtitulo("En caja y bancos")
                        .build(),
                KpiCardDTO.builder()
                        .titulo("SALDOS POR COBRAR")
                        .valor("S/ " + totalSaldo.setScale(2, RoundingMode.HALF_UP))
                        .subtitulo("Cuentas pendientes")
                        .build(),
                KpiCardDTO.builder()
                        .titulo("TICKET PROMEDIO")
                        .valor("S/ " + ticketPromedio.setScale(2, RoundingMode.HALF_UP))
                        .subtitulo("Por comprobante")
                        .build()
        );

        return ReporteVentasPeriodoDTO.builder()
                .kpis(kpis)
                .totalVendido(totalVendido.setScale(2, RoundingMode.HALF_UP))
                .totalCobrado(totalCobrado.setScale(2, RoundingMode.HALF_UP))
                .totalSaldoPendiente(totalSaldo.setScale(2, RoundingMode.HALF_UP))
                .totalVentas(cantidadVentas)
                .ticketPromedio(ticketPromedio)
                .totalPorMetodoPago(totalPorMetodo)
                .ventas(itemsVenta)
                .build();
    }

    @Override
    public byte[] exportarReporteVentasPeriodoPdf(FiltroReporteVentasDTO filtro) {
        ReporteVentasPeriodoDTO datos = obtenerReporteVentasPeriodo(filtro);
        ConfiguracionEmpresa empresa = configuracionEmpresaRepository.findFirstByOrderByIdConfiguracionEmpresaAsc().orElse(null);

        try {
            ReportePdfUtil pdf = new ReportePdfUtil();

            String periodoTexto = construirTextoPeriodo(filtro);
            pdf.dibujarEncabezado(empresa, "REPORTE DE VENTAS Y RECAUDACIÓN", periodoTexto);
            pdf.dibujarTarjetasKpi(datos.getKpis());

            List<String> encabezados = List.of("Comprobante", "Fecha", "Cliente", "Vendedor", "Total (S/)", "Cobrado", "Saldo", "Estado");
            float[] anchos = new float[]{80f, 65f, 105f, 95f, 55f, 50f, 50f, 60f};

            List<List<String>> filas = new ArrayList<>();
            for (ItemVentaReporteDTO item : datos.getVentas()) {
                filas.add(List.of(
                        item.getNumeroComprobante(),
                        item.getFechaRegistro() != null ? item.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")) : "-",
                        item.getNombreCliente(),
                        item.getNombreVendedor(),
                        item.getTotal().setScale(2, RoundingMode.HALF_UP).toString(),
                        item.getTotalPagado().setScale(2, RoundingMode.HALF_UP).toString(),
                        item.getSaldoPendiente().setScale(2, RoundingMode.HALF_UP).toString(),
                        item.getCondicionPago()
                ));
            }

            pdf.dibujarTabla(encabezados, anchos, filas);
            return pdf.exportarPdf();
        } catch (IOException ex) {
            log.error("Error al exportar reporte de ventas a PDF", ex);
            throw new IllegalStateException("Error al generar el archivo PDF de ventas", ex);
        }
    }

    @Override
    public ReporteRankingComercialDTO obtenerReporteRankingComercial(FiltroReporteVentasDTO filtro) {
        log.info("Generando reporte de ranking comercial con filtros: {}", filtro);

        LocalDateTime inicio = (filtro != null && filtro.getFechaInicio() != null)
                ? filtro.getFechaInicio().atStartOfDay() : null;
        LocalDateTime fin = (filtro != null && filtro.getFechaFin() != null)
                ? filtro.getFechaFin().atTime(LocalTime.MAX) : null;

        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getEstado() != EstadoVentaEnum.ANULADA)
                .filter(v -> inicio == null || !v.getFechaRegistro().isBefore(inicio))
                .filter(v -> fin == null || !v.getFechaRegistro().isAfter(fin))
                .collect(Collectors.toList());

        Map<Long, ItemRankingComercialDTO> productosMap = new HashMap<>();
        Map<Long, ItemRankingComercialDTO> serviciosMap = new HashMap<>();

        for (Venta v : ventas) {
            // Productos vendidos
            List<ProductoVendido> pvs = productoVendidoRepository.findByVentaIdVenta(v.getIdVenta());
            for (ProductoVendido pv : pvs) {
                if (pv.getProducto() != null) {
                    Producto p = pv.getProducto();
                    ItemRankingComercialDTO item = productosMap.computeIfAbsent(p.getIdProducto(), id ->
                            ItemRankingComercialDTO.builder()
                                    .tipoItem("PRODUCTO")
                                    .idItem(p.getIdProducto())
                                    .codigo(p.getCodigo() != null ? p.getCodigo() : "PROD-" + p.getIdProducto())
                                    .nombre(p.getNombre())
                                    .categoria(p.getCategoria() != null ? p.getCategoria().getNombre() : "GENERAL")
                                    .precioUnitario(p.getPrecio())
                                    .unidadesVendidas(0L)
                                    .totalRecaudado(BigDecimal.ZERO)
                                    .build());

                    long cant = pv.getCantidad() != null ? pv.getCantidad() : 1L;
                    BigDecimal sub = pv.getSubTotal() != null ? pv.getSubTotal() : p.getPrecio().multiply(BigDecimal.valueOf(cant));
                    item.setUnidadesVendidas(item.getUnidadesVendidas() + cant);
                    item.setTotalRecaudado(item.getTotalRecaudado().add(sub));
                }
            }

            // Servicios vendidos
            List<ServicioVendido> svs = servicioVendidoRepository.findByVentaIdVenta(v.getIdVenta());
            for (ServicioVendido sv : svs) {
                if (sv.getServicio() != null) {
                    Servicio s = sv.getServicio();
                    ItemRankingComercialDTO item = serviciosMap.computeIfAbsent(s.getIdServicio(), id ->
                            ItemRankingComercialDTO.builder()
                                    .tipoItem("SERVICIO")
                                    .idItem(s.getIdServicio())
                                    .codigo("SERV-" + s.getIdServicio())
                                    .nombre(s.getNombre())
                                    .categoria("SERVICIO PROFESIONAL")
                                    .precioUnitario(s.getPrecio())
                                    .unidadesVendidas(0L)
                                    .totalRecaudado(BigDecimal.ZERO)
                                    .build());

                    BigDecimal precio = sv.getPrecio() != null ? sv.getPrecio() : s.getPrecio();
                    item.setUnidadesVendidas(item.getUnidadesVendidas() + 1);
                    item.setTotalRecaudado(item.getTotalRecaudado().add(precio));
                }
            }
        }

        List<ItemRankingComercialDTO> todosLosItems = new ArrayList<>();
        todosLosItems.addAll(productosMap.values());
        todosLosItems.addAll(serviciosMap.values());

        // Ordenar por total recaudado descendente
        todosLosItems.sort(Comparator.comparing(ItemRankingComercialDTO::getTotalRecaudado).reversed());

        long totalUnidades = 0;
        BigDecimal totalGeneral = BigDecimal.ZERO;
        int rank = 1;
        for (ItemRankingComercialDTO item : todosLosItems) {
            item.setRanking(rank++);
            totalUnidades += item.getUnidadesVendidas();
            totalGeneral = totalGeneral.add(item.getTotalRecaudado());
        }

        List<KpiCardDTO> kpis = List.of(
                KpiCardDTO.builder()
                        .titulo("TOTAL RECAUDADO (ITEMS)")
                        .valor("S/ " + totalGeneral.setScale(2, RoundingMode.HALF_UP))
                        .subtitulo("Venta de productos y servicios")
                        .build(),
                KpiCardDTO.builder()
                        .titulo("UNIDADES VENDIDAS")
                        .valor(String.valueOf(totalUnidades))
                        .subtitulo("Productos y atenciones")
                        .build(),
                KpiCardDTO.builder()
                        .titulo("ÍTEMS CON ROTACIÓN")
                        .valor(String.valueOf(todosLosItems.size()))
                        .subtitulo("En el catálogo comercial")
                        .build()
        );

        return ReporteRankingComercialDTO.builder()
                .kpis(kpis)
                .totalUnidadesVendidas(totalUnidades)
                .totalRecaudado(totalGeneral.setScale(2, RoundingMode.HALF_UP))
                .items(todosLosItems)
                .build();
    }

    @Override
    public byte[] exportarReporteRankingComercialPdf(FiltroReporteVentasDTO filtro) {
        ReporteRankingComercialDTO datos = obtenerReporteRankingComercial(filtro);
        ConfiguracionEmpresa empresa = configuracionEmpresaRepository.findFirstByOrderByIdConfiguracionEmpresaAsc().orElse(null);

        try {
            ReportePdfUtil pdf = new ReportePdfUtil();
            String periodoTexto = construirTextoPeriodo(filtro);
            pdf.dibujarEncabezado(empresa, "RANKING DE PRODUCTOS Y SERVICIOS MÁS VENDIDOS", periodoTexto);
            pdf.dibujarTarjetasKpi(datos.getKpis());

            List<String> encabezados = List.of("Rank", "Tipo", "Código", "Descripción", "Categoría", "Precio Unit.", "Cant.", "Total (S/)");
            float[] anchos = new float[]{35f, 65f, 65f, 130f, 90f, 60f, 40f, 65f};

            List<List<String>> filas = new ArrayList<>();
            for (ItemRankingComercialDTO item : datos.getItems()) {
                filas.add(List.of(
                        "#" + item.getRanking(),
                        item.getTipoItem(),
                        item.getCodigo(),
                        item.getNombre(),
                        item.getCategoria(),
                        "S/ " + item.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP),
                        item.getUnidadesVendidas().toString(),
                        "S/ " + item.getTotalRecaudado().setScale(2, RoundingMode.HALF_UP)
                ));
            }

            pdf.dibujarTabla(encabezados, anchos, filas);
            return pdf.exportarPdf();
        } catch (IOException ex) {
            log.error("Error al exportar ranking comercial a PDF", ex);
            throw new IllegalStateException("Error al generar el archivo PDF de ranking comercial", ex);
        }
    }

    private String construirTextoPeriodo(FiltroReporteVentasDTO filtro) {
        if (filtro == null || (filtro.getFechaInicio() == null && filtro.getFechaFin() == null)) {
            return "Periodo: Histórico Completo";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String desde = filtro.getFechaInicio() != null ? filtro.getFechaInicio().format(fmt) : "Inicio";
        String hasta = filtro.getFechaFin() != null ? filtro.getFechaFin().format(fmt) : "Actualidad";
        return "Periodo filtrado: Del " + desde + " al " + hasta;
    }
}
