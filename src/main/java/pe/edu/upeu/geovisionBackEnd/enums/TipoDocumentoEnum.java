package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDocumentoEnum {
    DNI(1, "Documento Nacional de Identidad"),
    RUC(2, "Registro Único de Contribuyentes"),
    CARNET_EXTRANJERIA(3, "Carnet de Extranjería"),
    PASAPORTE(4, "Pasaporte");

    private final Integer codigo;
    private final String descripcion;

    public static TipoDocumentoEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoDocumentoEnum doc : values()) {
            if (doc.codigo.equals(codigo)) {
                return doc;
            }
        }
        throw new IllegalArgumentException("Código de tipo de documento no válido: " + codigo);
    }
}
