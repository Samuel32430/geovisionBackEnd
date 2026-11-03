package pe.edu.upeu.geovisionBackEnd.util;

import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;

/** Constancias legibles en las columnas de texto existentes; nunca trunca el historial. */
public final class ConstanciaOrdenTrabajo {
    private ConstanciaOrdenTrabajo() {}
    public static String agregar(String anterior,String nueva) {
        if(nueva==null || nueva.isBlank()) return anterior;
        String resultado=anterior==null || anterior.isBlank() ? nueva : anterior+"\n"+nueva;
        if(resultado.length()>500) throw new ReglaNegocioException("La constancia supera los 500 caracteres disponibles; reduzca la observacion nueva sin borrar el historial");
        return resultado;
    }
}
