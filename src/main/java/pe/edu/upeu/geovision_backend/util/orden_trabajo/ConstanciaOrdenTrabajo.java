package pe.edu.upeu.geovision_backend.util.orden_trabajo;

import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

public final class ConstanciaOrdenTrabajo {
    private ConstanciaOrdenTrabajo() {}
    public static String agregar(String anterior,String nueva) {
        if(nueva==null || nueva.isBlank()) return anterior;
        String resultado=anterior==null || anterior.isBlank() ? nueva : anterior+"\n"+nueva;
        if(resultado.length()>500) throw new ReglaNegocioException("La constancia supera los 500 caracteres disponibles; reduzca la observacion nueva sin borrar el historial");
        return resultado;
    }
}
