package pe.edu.upeu.geovision_backend.service.orden_trabajo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EntregaPedidoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.LaboratorioDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PendienteOrdenTrabajoDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.RecepcionLaboratorioRequestDTO;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;

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
