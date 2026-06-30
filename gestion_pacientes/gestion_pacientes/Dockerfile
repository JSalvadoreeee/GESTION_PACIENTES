# Usar una imagen base oficial de Java 21 para correr la app
FROM eclipse-temurin:21-jre-jammy

# Crear directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo .jar generado por Maven desde la subcarpeta target
COPY target/*.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]