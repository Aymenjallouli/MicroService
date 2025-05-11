# Utiliser une image de base Java
FROM openjdk:17-jdk-alpine

# Copier le fichier JAR du microservice dans l'image
ARG JAR_FILE=target/TaskSschService-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Exposer le port du microservice
EXPOSE 8085

# Commande pour lancer le microservice
ENTRYPOINT ["java", "-jar", "/app.jar"]