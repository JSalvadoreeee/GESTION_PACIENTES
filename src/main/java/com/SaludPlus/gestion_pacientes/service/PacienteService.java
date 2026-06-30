package com.SaludPlus.gestion_pacientes.service;

import com.SaludPlus.gestion_pacientes.model.Paciente;
import com.SaludPlus.gestion_pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private BackupService backupService;

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> findById(Long id) {
        return pacienteRepository.findById(id);
    }

    // --- NUEVO MÉTODO PARA BÚSQUEDA POR RUN ---
    public Optional<Paciente> findByRun(String run) {
        return pacienteRepository.findByRun(run);
    }

    public Paciente save(Paciente paciente) {
        // Validación Pro: Evitar duplicados por RUN antes de guardar
        if (pacienteRepository.existsByRun(paciente.getRun())) {
            throw new RuntimeException("Error: El RUN " + paciente.getRun() + " ya está registrado en SaludPlus.");
        }
        
        Paciente guardado = pacienteRepository.save(paciente);
        backupService.realizarRespaldo(guardado); 
        return guardado;
    }

    public Paciente update(Long id, Paciente datosNuevos) {
        return pacienteRepository.findById(id).map(paciente -> {
            paciente.setNombre(datosNuevos.getNombre());
            paciente.setApellido(datosNuevos.getApellido());
            paciente.setRun(datosNuevos.getRun());
            paciente.setCorreo(datosNuevos.getCorreo());
            paciente.setTelefono(datosNuevos.getTelefono());
            paciente.setDireccion(datosNuevos.getDireccion());
            paciente.setPrevision(datosNuevos.getPrevision());
            paciente.setGenero(datosNuevos.getGenero());
            paciente.setFechaNacimiento(datosNuevos.getFechaNacimiento());
            
            Paciente actualizado = pacienteRepository.save(paciente);
            backupService.realizarRespaldo(actualizado);
            return actualizado;
        }).orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + id));
    }

    public List<Paciente> saveAll(List<Paciente> pacientes) {
        List<Paciente> guardados = pacienteRepository.saveAll(pacientes);
        // Usamos el backup service para asegurar la persistencia en la base espejo
        guardados.forEach(paciente -> backupService.insertarEnEspejo(paciente));
        backupService.createBackup();
        return guardados;
    }

    public void delete(Long id) {
        pacienteRepository.deleteById(id);
    }
}