# Usar OpenJDK 17 como imagen base
FROM openjdk:17-jdk-slim

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR ya construido
COPY build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
