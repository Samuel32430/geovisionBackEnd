package pe.edu.upeu.geovision_backend.dto;

import org.springframework.data.domain.Page;
import java.util.List;

/** Contrato de paginación independiente de la serialización interna de Spring Data. */
public record PaginaResponseDTO<T>(List<T> content, int number, int size,
        long totalElements, int totalPages, boolean first, boolean last, boolean empty) {
    public static <T> PaginaResponseDTO<T> de(Page<T> pagina) {
        return new PaginaResponseDTO<>(pagina.getContent(), pagina.getNumber(), pagina.getSize(),
                pagina.getTotalElements(), pagina.getTotalPages(), pagina.isFirst(), pagina.isLast(), pagina.isEmpty());
    }
}
