FROM maven:3.8.6-openjdk-11

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8081

# Run the application (find the JAR file automatically)
CMD java -jar target/*.jar
