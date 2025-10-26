# Deployment Guide - Lakgamana Train Reservation System

This guide provides comprehensive instructions for deploying the Lakgamana Train Reservation System backend to various environments.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional)
- MySQL 8.0+ or PostgreSQL 13+ (for production)

## 📋 Environment Setup

### 1. Development Environment

#### Local Development with H2 Database
```bash
# Clone repository
git clone <repository-url>
cd lakgamana-backend

# Run with H2 in-memory database
mvn spring-boot:run
```

The application will start on `http://localhost:8080` with H2 database.

#### Local Development with MySQL
```yaml
# Update application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lakgamana_dev
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

### 2. Production Environment

#### Environment Variables
```bash
# Database Configuration
DB_URL=jdbc:mysql://your-db-host:3306/lakgamana_prod
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET=your-super-secure-secret-key-here

# Server Configuration
SERVER_PORT=8080

# Application Profile
SPRING_PROFILES_ACTIVE=prod
```

#### Production application.yml
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: ${SERVER_PORT}

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
  refresh-expiration: 604800000

logging:
  level:
    com.lakgamana: INFO
    org.springframework.security: WARN
```

## 🐳 Docker Deployment

### 1. Create Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "target/lakgamana-backend-1.0.0.jar"]
```

### 2. Docker Compose Setup

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:mysql://db:3306/lakgamana_db
      - DB_USERNAME=lakgamana_user
      - DB_PASSWORD=lakgamana_password
      - JWT_SECRET=your-jwt-secret-key
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=root_password
      - MYSQL_DATABASE=lakgamana_db
      - MYSQL_USER=lakgamana_user
      - MYSQL_PASSWORD=lakgamana_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    restart: unless-stopped

volumes:
  mysql_data:
```

### 3. Deploy with Docker

```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## ☁️ Cloud Deployment

### 1. AWS Deployment

#### AWS EC2 with RDS
```bash
# Launch EC2 instance (t3.medium recommended)
# Install Java 17 and Maven
sudo yum install java-17-amazon-corretto-devel maven

# Deploy application
git clone <repository-url>
cd lakgamana-backend
mvn clean package
java -jar target/lakgamana-backend-1.0.0.jar
```

#### AWS Elastic Beanstalk
```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init lakgamana-backend

# Create environment
eb create lakgamana-prod

# Deploy
eb deploy
```

#### AWS ECS with Fargate
```json
{
  "family": "lakgamana-backend",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::account:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "lakgamana-backend",
      "image": "your-account.dkr.ecr.region.amazonaws.com/lakgamana-backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "DB_URL",
          "value": "jdbc:mysql://your-rds-endpoint:3306/lakgamana_db"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/lakgamana-backend",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### 2. Google Cloud Platform

#### Google Cloud Run
```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/PROJECT-ID/lakgamana-backend

# Deploy to Cloud Run
gcloud run deploy lakgamana-backend \
  --image gcr.io/PROJECT-ID/lakgamana-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars DB_URL=jdbc:mysql://your-cloud-sql-ip:3306/lakgamana_db
```

### 3. Azure Deployment

#### Azure App Service
```bash
# Install Azure CLI
# Login to Azure
az login

# Create resource group
az group create --name lakgamana-rg --location eastus

# Create App Service plan
az appservice plan create --name lakgamana-plan --resource-group lakgamana-rg --sku B1

# Create web app
az webapp create --resource-group lakgamana-rg --plan lakgamana-plan --name lakgamana-backend

# Deploy from GitHub
az webapp deployment source config --name lakgamana-backend --resource-group lakgamana-rg --repo-url <your-repo-url> --branch main --manual-integration
```

## 🔧 Configuration Management

### 1. Environment-Specific Configuration

#### application-dev.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    show-sql: true
  flyway:
    enabled: true

logging:
  level:
    com.lakgamana: DEBUG
```

#### application-prod.yml
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
  flyway:
    enabled: true

logging:
  level:
    com.lakgamana: INFO
    root: WARN
```

### 2. Secrets Management

#### AWS Secrets Manager
```java
@Value("${aws.secretsmanager.secret-name}")
private String secretName;

// Retrieve database credentials from AWS Secrets Manager
```

#### HashiCorp Vault
```yaml
spring:
  cloud:
    vault:
      host: vault.example.com
      port: 8200
      scheme: https
      authentication: TOKEN
      token: ${VAULT_TOKEN}
      kv:
        enabled: true
        backend: secret
        application-name: lakgamana-backend
```

## 📊 Monitoring & Logging

### 1. Application Monitoring

#### Actuator Endpoints
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

#### Prometheus Metrics
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 2. Logging Configuration

#### Logback Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/lakgamana-backend.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/lakgamana-backend.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

## 🔒 Security Configuration

### 1. SSL/TLS Configuration

#### application.yml
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: tomcat
```

### 2. CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "https://lakgamana.com",
        "https://www.lakgamana.com",
        "https://admin.lakgamana.com"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    return new UrlBasedCorsConfigurationSource();
}
```

## 🚀 CI/CD Pipeline

### 1. GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    
    - name: Cache Maven packages
      uses: actions/cache@v2
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run tests
      run: mvn test
    
    - name: Build application
      run: mvn clean package -DskipTests
    
    - name: Deploy to AWS
      run: |
        aws s3 cp target/lakgamana-backend-1.0.0.jar s3://your-deployment-bucket/
        aws ecs update-service --cluster lakgamana-cluster --service lakgamana-backend --force-new-deployment
```

### 2. Jenkins Pipeline

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo/lakgamana-backend.git'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Deploy') {
            steps {
                sh 'docker build -t lakgamana-backend .'
                sh 'docker tag lakgamana-backend your-registry/lakgamana-backend:latest'
                sh 'docker push your-registry/lakgamana-backend:latest'
                sh 'kubectl set image deployment/lakgamana-backend lakgamana-backend=your-registry/lakgamana-backend:latest'
            }
        }
    }
}
```

## 📋 Health Checks

### 1. Application Health Check

```bash
# Health check endpoint
curl http://localhost:8080/actuator/health

# Expected response
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

### 2. Load Balancer Health Check

```bash
# Configure health check for load balancer
curl http://localhost:8080/actuator/health/readiness

# Expected response
{
  "status": "UP"
}
```

## 🔄 Database Migration

### 1. Flyway Migration

```bash
# Check migration status
mvn flyway:info

# Apply migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate
```

### 2. Manual Database Setup

```sql
-- Create database
CREATE DATABASE lakgamana_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'lakgamana_user'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON lakgamana_db.* TO 'lakgamana_user'@'%';
FLUSH PRIVILEGES;
```

## 📞 Troubleshooting

### Common Issues

1. **Database Connection Issues**
   ```bash
   # Check database connectivity
   telnet your-db-host 3306
   
   # Verify credentials
   mysql -h your-db-host -u your-username -p
   ```

2. **JWT Token Issues**
   ```bash
   # Verify JWT secret is set
   echo $JWT_SECRET
   
   # Check token format
   jwt decode your-jwt-token
   ```

3. **Memory Issues**
   ```bash
   # Increase heap size
   java -Xmx2g -Xms1g -jar lakgamana-backend-1.0.0.jar
   ```

### Log Analysis

```bash
# View application logs
tail -f logs/lakgamana-backend.log

# Search for errors
grep "ERROR" logs/lakgamana-backend.log

# Monitor performance
grep "Duration" logs/lakgamana-backend.log
```

## 📚 Additional Resources

- [Spring Boot Production Ready Features](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [AWS Deployment Guide](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/java-se-platform.html)
- [Security Best Practices](https://owasp.org/www-project-top-ten/)

---

**Happy Deploying! 🚀**
