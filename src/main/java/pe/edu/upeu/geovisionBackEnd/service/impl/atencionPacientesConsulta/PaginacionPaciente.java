package pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import java.util.Set;

public final class PaginacionPaciente {
    private PaginacionPaciente() {}

    public static PageRequest crear(int pagina, int tamanio, String campo, String direccion,
                                   String defecto, String id, Set<String> permitidos) {
        if (pagina < 0 || tamanio < 1 || tamanio > 100) {
            throw new ReglaNegocioException("Pagina debe ser >= 0 y tamanio debe estar entre 1 y 100");
        }
        String ordenar = campo == null || campo.isBlank() ? defecto : campo.trim();
        String sentido = direccion == null || direccion.isBlank() ? "asc" : direccion.trim();
        if (!permitidos.contains(ordenar) || !(sentido.equalsIgnoreCase("asc") || sentido.equalsIgnoreCase("desc"))) {
            throw new ReglaNegocioException("Ordenamiento no permitido");
        }
        var sort = Sort.by(Sort.Direction.fromString(sentido), ordenar);
        if (!ordenar.equals(id)) sort = sort.and(Sort.by(id));
        return PageRequest.of(pagina, tamanio, sort);
    }

    public static String termino(String valor) {
        // Oracle treats an empty string as NULL.
        if (valor == null || valor.isBlank()) return null;
        return "%" + valor.trim().toLowerCase(java.util.Locale.ROOT)
                .replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }

    public static String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

