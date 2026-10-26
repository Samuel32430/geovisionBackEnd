package pe.edu.upeu.geovisionBackEnd.dto.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginaResponseDTO<T> {

    private List<T> contenido;
    private int paginaActual;
    private int tamanoPagina;
    private long totalElementos;
    private int totalPaginas;
    private boolean primera;
    private boolean ultima;

    public static <T> PaginaResponseDTO<T> de(Page<T> page) {
        if (page == null) {
            return PaginaResponseDTO.<T>builder()
                    .contenido(Collections.emptyList())
                    .paginaActual(0)
                    .tamanoPagina(0)
                    .totalElementos(0L)
                    .totalPaginas(0)
                    .primera(true)
                    .ultima(true)
                    .build();
        }

        return PaginaResponseDTO.<T>builder()
                .contenido(page.getContent() != null ? page.getContent() : Collections.emptyList())
                .paginaActual(page.getNumber())
                .tamanoPagina(page.getSize())
                .totalElementos(page.getTotalElements())
                .totalPaginas(page.getTotalPages())
                .primera(page.isFirst())
                .ultima(page.isLast())
                .build();
    }
}
