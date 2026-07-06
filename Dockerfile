# Etapa 1: Compilación de la aplicación con Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Entorno de ejecución ligero y seguro con JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Requisito de Seguridad: Correr la app con un usuario sin privilegios root
RUN addgroup -S saludplusgroup && adduser -S saludplususer -G saludplusgroup
USER saludplususer

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]