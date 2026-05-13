# Gestión de Pacientes - Clinica SaludPlus

Este repositorio contiene el sistema de gestión de pacientes, diseñado para demostrar la implementación de un ciclo de vida de desarrollo de software automatizado y seguro bajo estándares DevOps.

# Conclusiones

# Jhoander Salvador Briceño Amarista
Mi punto de vista,es que el mayor valor de este proyecto radica en la transición hacia una cultura de DevSecOps. No solo automatizamos la construcción del software con GitHub Actions, sino que integramos la seguridad como un paso obligatorio mediante Snyk. Esto cambia el punto de desarrollo, permitiendonos detectar fallos antes de que lleguen a un entorno real. Considero que el uso de Java y Maven garantiza que nuestra infraestructura sea moderna y escalable.

# José Cuevas Vivanco
En mi opinión, la clave de este examen fue la resolución de la interoperabilidad entre el entorno local y el cloud. Logramos estandarizar un pipeline que soluciona los conflictos de versiones de JDK mediante la configuración lógica de los pasos en GitHub Actions. La automatización del flujo de trabajo reduce el error humano y asegura que cada incremento de código sea validado de forma consistente. Esta experiencia demuestra cómo la infraestructura como código facilita la entrega continua de valor.



# Pipeline

Se ha implementado un flujo de trabajo Workflow que automatiza la integración continua para garantizar la estabilidad del sistema.

La trazabilidad se garantiza mediante la vinculación directa entre el control de versiones y la ejecución de acciones:
* Identificación Única:** Cada ejecución del pipeline está amarrada a un ID de Commit, permitiendo auditar qué cambios específicos activaron el build.
* Historial de Ejecución: GitHub Actions mantiene un registro detallado de los logs, permitiendo identificar el punto exacto de fallo en cualquier etapa del proceso.

Garantizamos la calidad del software mediante:
* Estandarización del Entorno: Se utiliza Java 21 (JDK) en contenedores de GitHub Actions para eliminar el problema de en mi máquina funciona.
* Gestión de Dependencias: Usamos Maven para asegurar una compilación limpia y estructurada.
* Análisis de Vulnerabilidades: Integración con Snyk para detectar librerías inseguras en el archivo `pom.xml`, implementando seguridad preventiva (Shift-Left Security).
# Herramientas
* Lenguaje: Java 21
* Gestor de Proyectos: Maven
* Automatización: GitHub Actions
* Seguridad: Snyk Open Source
# Integrantes
 Jhoander Briceñoy Jose Cuevas
 Ing DevOps
