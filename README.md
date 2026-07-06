# Examen Final - Gestión de Pacientes
# Jhoander Briceño - José Cuevas

Este repositorio contiene la solución definitiva para el examen final del curso. El proyecto fue migrado exitosamente a Java 21 y robustecido estructuralmente para su correcta orquestación en alta disponibilidad utilizando **Kubernetes**.

A lo largo del desarrollo se resolvieron diversas problemáticas de control de versiones, políticas de seguridad corporativas y sincronización de ramas, logrando un flujo de integración continua (CI/CD) completamente limpio y funcional con check verde en la rama principal (`main`).

---

Historial de Comandos, Sincronización y Seguridad de Ramas (Git & GitHub)

Durante el examen se presentaron divergencias críticas entre el historial de las ramas `develop` y `main` (errores de historias no relacionadas y conflictos en los flujos de GitHub Actions). 

#Gestión de la Rama Protegida (Protected Branch)
La rama `main` se encontraba configurada bajo reglas de protección estrictas en GitHub (candado de seguridad remoto), lo que provocó que el servidor rechazara de inmediato los intentos de sobreescritura forzada con el error:  
`remote: error: GH006: Protected branch update failed... Cannot force-push to this branch`.

Para cumplir con las buenas prácticas de desarrollo sin desactivar la seguridad ni desproteger la rama principal, se optó por resolver la divergencia directamente mediante la unificación de historias locales, ejecutando la siguiente secuencia de comandos:

```bash
# 1. Nos cambiamos a la rama de desarrollo donde teníamos el código correcto
git checkout develop

# 2. Integramos el historial remoto de main hacia develop para resolver los conflictos de base
git pull origin main --allow-unrelated-histories

# 3. Subimos de manera limpia la rama develop actualizada con los cambios ya unificados
git push origin develop

# 4. Realizamos el despliegue directo de los commits unificados hacia main mediante un push regular (no forzado)
git push origin develop:main

Este procedimiento garantizó que la carpeta de manifiestos de Kubernetes (k8s/) se integrara correctamente en la rama main, activando con éxito el pipeline automático de GitHub Actions y obteniendo la validación correcta de compilación.

Docker
Siguiendo las instrucciones de la pauta, se modificó la configuración de Docker para utilizar la imagen base eclipse-temurin:21-jre-jammy. Esto asegura soporte nativo para las características de Java 21 y minimiza el peso de la imagen final.
Pipeline de CI/CD: El archivo .github/workflows/main.yml compila el proyecto mediante Maven, construye la imagen Docker de forma automatizada ante cada cambio en main y la publica directamente en el registro seguro de GitHub Package.

Kubernetes
El archivo de configuración se encuentra en k8s/deplyment.yaml e implementa las directrices clave exigidas en la evaluación para asegurar la resiliencia del sistema:
Se configuraron 3 réplicas simultáneas (replicas: 3) para el Pod del microservicio. Esto cumple con el requisito de tolerancia a fallos: si un contenedor experimenta un error crítico o el nodo se satura, Kubernetes balancea la carga automáticamente hacia los Pods restantes, manteniendo el sistema 100% disponible
Control y Límites de Recursos:
 Requests (Mínimo pragmático): 256Mi de Memoria / 250m de CPU (un cuarto de núcleo).
 Limits (Máximo permitido): 512Mi de Memoria / 500m de CPU (medio núcleo).
Para aplicar estos manifiestos y levantar el entorno completo con las réplicas y políticas descritas, se ejecuta el siguiente comando en el entorno de Kubernetes:
kubectl apply -f k8s/deplyment.yaml
