package pe.edu.upeu.geovisionBackEnd.enums.controlAccesos;

import lombok.Getter;

@Getter
public enum EstadoUsuarioEnum {
    INACTIVO(0),
    ACTIVO(1);

    private final int valor;

    EstadoUsuarioEnum(int valor) {
        this.valor = valor;
    }
}
