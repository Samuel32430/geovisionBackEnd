package pe.edu.upeu.geovisionBackEnd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.MercadoPagoQrRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MercadoPagoQrResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PagoMapper;
import pe.edu.upeu.geovisionBackEnd.repository.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.MercadoPagoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoServiceImpl implements MercadoPagoService {

    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;

    @Value("${mercadopago.access-token:TEST-MERCADOPAGO-ACCESS-TOKEN}")
    private String accessToken;

    @Value("${mercadopago.user-id:123456789}")
    private String userId;

    @Value("${mercadopago.pos-id:CAJA_PRINCIPAL_01}")
    private String posId;

    @Override
    @Transactional(readOnly = true)
    public MercadoPagoQrResponseDTO generarOrdenCobroQr(MercadoPagoQrRequestDTO request) {
        log.info("Generando solicitud de orden QR en Mercado Pago para venta ID: {}, monto: S/ {}",
                request.getIdVenta(), request.getMonto());

        Venta venta = ventaRepository.findById(request.getIdVenta())
                .orElseThrow(() -> new RecursosNoEncontradosException("Venta no encontrada con ID: " + request.getIdVenta()));

        if (venta.getEstado() == EstadoVentaEnum.ANULADA) {
            throw new ReglaNegocioException("No se puede generar cobro QR para una venta anulada");
        }
        if (venta.getEstado() == EstadoVentaEnum.PAGADA) {
            throw new ReglaNegocioException("La venta ya se encuentra totalmente cancelada");
        }

        BigDecimal totalPagado = pagoRepository.sumMontoPagadoByVentaId(venta.getIdVenta());
        if (totalPagado == null) {
            totalPagado = BigDecimal.ZERO;
        }
        BigDecimal saldoPendiente = venta.getTotal().subtract(totalPagado);

        if (request.getMonto().compareTo(saldoPendiente) > 0) {
            throw new ReglaNegocioException("El monto a cobrar mediante QR (S/ " + request.getMonto() +
                    ") excede el saldo pendiente (S/ " + saldoPendiente + ")");
        }

        String externalReference = "VENTA-" + venta.getIdVenta();
        String inStoreOrderId = "ORDER-MP-" + System.currentTimeMillis();

        // Estructura lista: Cuando se conecte a las credenciales reales de Mercado Pago Perú,
        // este método realizará un POST a:
        // https://api.mercadopago.com/instore/orders/qr/seller/collectors/{user_id}/pos/{pos_id}/qrs
        // y devolverá el qr_data oficial devuelto por la pasarela.
        String qrDataEmvCo = String.format("00020101021243650016COM.MERCADOPAGO%s540%s53036045802PE",
                externalReference, request.getMonto().toPlainString());

        return MercadoPagoQrResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .externalReference(externalReference)
                .monto(request.getMonto())
                .inStoreOrderId(inStoreOrderId)
                .qrData(qrDataEmvCo)
                .mensaje("Orden QR interoperable preparada con éxito para escaneo con Yape, Plin o Mercado Pago")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void procesarWebhook(Map<String, Object> payload) {
        log.info("Notificación Webhook recibida desde Mercado Pago: {}", payload);

        if (payload == null || payload.isEmpty()) {
            return;
        }

        // Extracción del ID de pago según la estructura estándar de notificaciones de Mercado Pago
        Object dataObj = payload.get("data");
        String paymentId = null;
        if (dataObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) dataObj;
            Object idVal = dataMap.get("id");
            if (idVal != null) {
                paymentId = idVal.toString();
            }
        } else if (payload.containsKey("id")) {
            paymentId = payload.get("id").toString();
        }

        log.info("Webhook analizado: paymentId={}. Listo para consultar estado a https://api.mercadopago.com/v1/payments/{}",
                paymentId, paymentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PagoResponseDTO confirmarPagoDigital(Long idVenta, String paymentId, BigDecimal monto) {
        log.info("Confirmando pago digital de Mercado Pago para venta ID: {}, paymentId: {}, monto: S/ {}",
                idVenta, paymentId, monto);

        Venta venta = ventaRepository.findByIdForUpdate(idVenta)
                .orElseThrow(() -> new RecursosNoEncontradosException("Venta no encontrada con ID: " + idVenta));

        Pago pago = Pago.builder()
                .venta(venta)
                .monto(monto)
                .metodoPago(MetodoPagoEnum.QR_YAPE_PLIN)
                .numeroOperacion("MP-" + paymentId)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Pago pagoGuardado = pagoRepository.save(pago);

        BigDecimal totalPagado = pagoRepository.sumMontoPagadoByVentaId(idVenta);
        if (totalPagado == null) {
            totalPagado = BigDecimal.ZERO;
        }

        if (totalPagado.compareTo(venta.getTotal()) >= 0) {
            venta.setEstado(EstadoVentaEnum.PAGADA);
            ventaRepository.save(venta);
        }

        return pagoMapper.toDTO(pagoGuardado);
    }
}
