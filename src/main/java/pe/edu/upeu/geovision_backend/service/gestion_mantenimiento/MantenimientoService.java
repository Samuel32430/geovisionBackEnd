package pe.edu.upeu.geovision_backend.service.gestion_mantenimiento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.CategoriaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.LaboratorioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.ServicioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;

public interface MantenimientoService {

    // >>>> QUERIES
    Page<CategoriaDTO> listarCategoria(@Size(max = 200) String termino, @Min(0) @Max(1) Integer estado, @NotNull Pageable pageable);

    CategoriaDTO obtenerCategoria(@NotNull @Positive Long id);

    Page<MaterialCristalDTO> listarMaterialCristal(@Size(max = 200) String termino, @NotNull Pageable pageable);

    MaterialCristalDTO obtenerMaterialCristal(@NotNull @Positive Long id);

    Page<TratamientoCristalDTO> listarTratamientoCristal(@Size(max = 200) String termino, @NotNull Pageable pageable);

    TratamientoCristalDTO obtenerTratamientoCristal(@NotNull @Positive Long id);

    Page<ServicioDTO> listarServicio(@Size(max = 200) String termino, @Min(0) @Max(1) Integer estado, @NotNull Pageable pageable);

    ServicioDTO obtenerServicio(@NotNull @Positive Long id);

    Page<LaboratorioDTO> listarLaboratorio(@Size(max = 200) String termino, @Min(0) @Max(1) Integer estado, @NotNull Pageable pageable);

    LaboratorioDTO obtenerLaboratorio(@NotNull @Positive Long id);

    // >>>> CREATION
    CategoriaDTO registrarCategoria(@NotNull @Valid CategoriaDTO request);

    MaterialCristalDTO registrarMaterialCristal(@NotNull @Valid MaterialCristalDTO request);

    TratamientoCristalDTO registrarTratamientoCristal(@NotNull @Valid TratamientoCristalDTO request);

    ServicioDTO registrarServicio(@NotNull @Valid ServicioDTO request);

    LaboratorioDTO registrarLaboratorio(@NotNull @Valid LaboratorioDTO request);

    // >>>> UPDATES
    CategoriaDTO actualizarCategoria(@NotNull @Positive Long id, @NotNull @Valid CategoriaDTO request);

    MaterialCristalDTO actualizarMaterialCristal(@NotNull @Positive Long id, @NotNull @Valid MaterialCristalDTO request);

    TratamientoCristalDTO actualizarTratamientoCristal(@NotNull @Positive Long id, @NotNull @Valid TratamientoCristalDTO request);

    ServicioDTO actualizarServicio(@NotNull @Positive Long id, @NotNull @Valid ServicioDTO request);

    LaboratorioDTO actualizarLaboratorio(@NotNull @Positive Long id, @NotNull @Valid LaboratorioDTO request);

}
