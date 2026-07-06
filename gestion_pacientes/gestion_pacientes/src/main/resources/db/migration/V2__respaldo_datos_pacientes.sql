-- Crear la tabla espejo para el respaldo automático
CREATE TABLE respaldo_pacientes (
    id_respaldo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_original BIGINT,
    rut VARCHAR(15),
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    fecha_respaldo TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

