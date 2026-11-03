package pe.edu.upeu.geovisionBackEnd.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoOrdenTrabajoEnum;

public interface OrdenTrabajoService {
    Page<PendienteOrdenTrabajoDTO> pendientes(String termino, Pageable pageable);

    Page<OrdenTrabajoResponseDTO> listar(EstadoOrdenTrabajoEnum estado, String termino, Pageable pageable);

    OrdenTrabajoResponseDTO obtener(Long id);

    OrdenTrabajoResponseDTO generar(OrdenTrabajoRequestDTO request, String usuario);

    EnvioLaboratorioResponseDTO enviar(EnvioLaboratorioRequestDTO request, String usuario);

    EnvioLaboratorioResponseDTO recibir(Long idEnvio, RecepcionLaboratorioRequestDTO request, String usuario);

    OrdenTrabajoResponseDTO entregar(EntregaPedidoRequestDTO request, String usuario);

    List<LaboratorioDTO> laboratorios();
}
