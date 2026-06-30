package com.SaludPlus.gestion_pacientes.controller;

import com.SaludPlus.gestion_pacientes.model.Paciente;
import com.SaludPlus.gestion_pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "1. Gestión de Pacientes", description = "Endpoints para el mantenimiento de fichas clínicas y carga masiva de datos")
@SecurityRequirement(name = "SaludPlusSecurity") 
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Operation(summary = "Obtener catálogo completo", description = "Recupera la lista total de pacientes registrados.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping
    public List<Paciente> listar() {
        return this.pacienteService.findAll();
    }

    @Operation(summary = "Buscar por ID", description = "Localiza la ficha mediante su identificador numérico interno.")
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        return this.pacienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar por RUN", description = "Permite al médico localizar un paciente usando su RUT/RUN. Es el método preferido para la integración con Atenciones.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente localizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con el RUN proporcionado")
    })
    @GetMapping("/run/{run}")
    public ResponseEntity<Paciente> buscarPorRun(
            @Parameter(description = "RUN del paciente sin puntos y con guion", example = "12345678-9") 
            @PathVariable String run) {
        return this.pacienteService.findByRun(run)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar Paciente", description = "Crea un nuevo registro. El sistema valida que el RUN sea único.")
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Paciente paciente) {
        try {
            return ResponseEntity.status(201).body(this.pacienteService.save(paciente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Modificar Ficha Clínica", description = "Actualiza la información. Si el ID no existe, retorna 404.")
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        try {
            return ResponseEntity.ok(this.pacienteService.update(id, paciente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Importación Masiva (Batch)", description = "Procesa múltiples registros optimizando el rendimiento.")
    @PostMapping("/batch")
    public ResponseEntity<List<Paciente>> cargaMasiva(@RequestBody List<Paciente> pacientes) {
        return ResponseEntity.ok(this.pacienteService.saveAll(pacientes));
    }

    @Operation(summary = "Eliminar Registro", description = "Remueve físicamente el registro del sistema.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}