package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoOrdenTrabajoEnum {
    PENDIENTE(1, "Pendiente de Procesar"),
    ENVIADO_LABORATORIO(2, "Enviado a Laboratorio"),
    RECIBIDO_LABORATORIO(3, "Recibido de Laboratorio"),
    CONTROL_CALIDAD_APROBADO(4, "Control de Calidad Aprobado"),
    CONTROL_CALIDAD_RECHAZADO(5, "Control de Calidad Rechazado"),
    LISTO_ENTREGA(6, "Listo para Entrega"),
    ENTREGADO(7, "Entregado"),
    ANULADO(8, "Anulado");

    private final Integer codigo;
    private final String descripcion;

    public static EstadoOrdenTrabajoEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (EstadoOrdenTrabajoEnum e : values()) {
            if (e.codigo.equals(codigo)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Código de estado de orden de trabajo no válido: " + codigo);
    }
}
