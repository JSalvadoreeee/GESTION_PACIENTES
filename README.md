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
```

Este procedimiento garantizó que la carpeta de manifiestos de Kubernetes (`k8s/`) se integrara correctamente en la rama `main`, activando con éxito el pipeline automático de GitHub Actions y obteniendo la validación correcta de compilación.

Posteriormente, mejoras adicionales (activación de pruebas automatizadas, Actuator, monitoreo) se integraron siguiendo el flujo estándar de Pull Requests descrito en la sección anterior, respetando en todo momento la protección de la rama `main`.

---

## Pipeline de CI/CD

El archivo `.github/workflows/main.yml` automatiza el ciclo completo de integración continua:

1. **Compilación:** compila el proyecto mediante Maven con Java 21.
2. **Pruebas automatizadas:** ejecuta las pruebas unitarias del proyecto contra una instancia real de MySQL 8, levantada como servicio temporal dentro del propio pipeline de GitHub Actions — validando la integración completa (JPA, Flyway) antes de continuar.
3. **Análisis de calidad y seguridad:** SonarCloud analiza el código en cada Pull Request, verificando Quality Gate, cobertura y ausencia de vulnerabilidades críticas.
4. **Empaquetado y publicación:** construye la imagen Docker de forma automatizada ante cada cambio en `main` y la publica en el registro seguro de GitHub Container Registry (GHCR), etiquetada tanto con `latest` como con el **SHA del commit**, garantizando trazabilidad completa desde el código fuente hasta el artefacto desplegado.
5. **Gestión de dependencias:** Dependabot escanea semanalmente las dependencias de Maven y las GitHub Actions utilizadas, abriendo Pull Requests automáticos ante actualizaciones o vulnerabilidades detectadas.

## Docker

Siguiendo las instrucciones de la pauta, se modificó la configuración de Docker para utilizar la imagen base `eclipse-temurin:21-jre-jammy`. Esto asegura soporte nativo para las características de Java 21 y minimiza el peso de la imagen final.

## Kubernetes

El archivo de configuración se encuentra en `k8s/deplyment.yaml` e implementa las directrices clave exigidas en la evaluación para asegurar la resiliencia del sistema:

- Se configuraron **3 réplicas simultáneas** (`replicas: 3`) para el Pod del microservicio. Esto cumple con el requisito de tolerancia a fallos: si un contenedor experimenta un error crítico o el nodo se satura, Kubernetes balancea la carga automáticamente hacia los Pods restantes, manteniendo el sistema 100% disponible.
- **Control y Límites de Recursos:**
  - Requests (mínimo pragmático): 256Mi de Memoria / 250m de CPU (un cuarto de núcleo).
  - Limits (máximo permitido): 512Mi de Memoria / 500m de CPU (medio núcleo).
- **Sondas de salud (liveness y readiness probes):** configuradas contra el endpoint `/actuator/health`, expuesto por Spring Boot Actuator. Esto permite que Kubernetes detecte automáticamente si un Pod no está respondiendo correctamente y lo reinicie o lo excluya del balanceo de carga sin intervención manual.

Para aplicar estos manifiestos y levantar el entorno completo con las réplicas y políticas descritas, se ejecuta el siguiente comando en el entorno de Kubernetes:

```bash
kubectl apply -f k8s/deplyment.yaml
```

## Monitoreo y Observabilidad

Se implementó un stack de observabilidad completo apoyado en Spring Boot Actuator, Prometheus y Grafana:

- **Spring Boot Actuator** expone los endpoints `/actuator/health`, `/actuator/metrics` y `/actuator/prometheus`, permitiendo consultar en tiempo real el estado de salud de la aplicación y métricas de desempeño (memoria, tiempo de respuesta, conexiones a base de datos).
- **Logging estructurado:** la aplicación registra logs tanto en consola como en archivo (`logs/gestion-pacientes.log`), facilitando la detección de anomalías y el debugging en entornos de desarrollo y pruebas.
- **Prometheus** (`monitoring/prometheus.yml`) recolecta las métricas expuestas por Actuator cada 15 segundos.
- **Grafana** permite construir dashboards personalizados a partir de las métricas recolectadas por Prometheus, apoyando la toma de decisiones técnicas sobre el desempeño del sistema.

Para levantar el stack de monitoreo localmente:

```bash
docker-compose -f docker-compose.monitoring.yml up -d
```

Luego se accede a Grafana en `http://localhost:3000` (usuario y contraseña: `admin`), configurando Prometheus (`http://prometheus:9090`) como fuente de datos.

## Herramientas

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.4.2
- **Gestor de Proyectos:** Maven
- **Automatización:** GitHub Actions
- **Contenedores:** Docker (publicación en GitHub Container Registry - GHCR)
- **Orquestación:** Kubernetes
- **Base de Datos:** MySQL 8
- **Calidad de Código:** SonarCloud
- **Gestión de Dependencias:** Dependabot
- **Observabilidad:** Spring Boot Actuator, Prometheus, Grafana

## Conclusiones

**Jhoander Salvador Briceño Amarista**

Mi punto de vista es que el mayor valor de este proyecto radica en la transición hacia una cultura de DevSecOps. No solo automatizamos la construcción del software con GitHub Actions, sino que integramos la seguridad y las pruebas automatizadas como pasos obligatorios del pipeline. Esto cambia el punto de detección de fallos, permitiéndonos identificarlos antes de que lleguen a un entorno real. Considero que el uso de Java, Maven y Kubernetes garantiza que nuestra infraestructura sea moderna, resiliente y escalable.

**José Cuevas Vivanco**

En mi opinión, la clave de este examen fue la resolución de la interoperabilidad entre el entorno local y el cloud. Logramos estandarizar un pipeline que soluciona los conflictos de versiones de JDK y de bases de datos mediante la configuración lógica de los pasos en GitHub Actions, incluyendo un servicio de MySQL temporal para validar las pruebas de integración. La automatización del flujo de trabajo, sumada a la observabilidad con Prometheus y Grafana, reduce el error humano y asegura que cada incremento de código sea validado y monitoreado de forma consistente. Esta experiencia demuestra cómo la infraestructura como código facilita la entrega continua de valor.

## Integrantes

- Jhoander Briceño
- José Cuevas

*Ingeniería DevOps*
