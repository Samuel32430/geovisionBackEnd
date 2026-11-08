package pe.edu.upeu.geovision_backend.dto;

import java.util.List;

/** Contrato de paginación independiente de la serialización interna de Spring Data. */
public record PaginaResponseDTO<T>(List<T> content, int number, int size,
        long totalElements, int totalPages, boolean first, boolean last, boolean empty) {
}
