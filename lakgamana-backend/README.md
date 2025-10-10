# Lakgamana Train Reservation System - Backend

A comprehensive Spring Boot backend application for the Lakgamana Train Reservation System, providing RESTful APIs for train booking, user management, payment processing, and feedback management.

## 🚀 Features

- **User Management**: Registration, authentication, and profile management
- **Train Management**: Train schedules, routes, and availability
- **Booking System**: Seat reservation and booking management
- **Payment Processing**: Multiple payment methods support
- **Feedback System**: User feedback collection and admin response
- **Admin Dashboard**: Comprehensive admin panel for system management
- **JWT Authentication**: Secure token-based authentication
- **Role-based Access Control**: Admin and User roles
- **Database Migrations**: Flyway-based database versioning
- **API Documentation**: Swagger/OpenAPI documentation
- **Comprehensive Testing**: Unit and integration tests

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security** with JWT
- **Spring Data JPA** with Hibernate
- **Maven** for dependency management
- **H2 Database** (development)
- **MySQL/PostgreSQL** (production)
- **Flyway** for database migrations
- **Jakarta Bean Validation**
- **Lombok** for code generation
- **SpringDoc OpenAPI** for API documentation
- **JUnit 5** and **MockMvc** for testing

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or PostgreSQL 13+ (for production)
- Git

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd lakgamana-backend
```

### 2. Configure Database

#### For Development (H2 - In-memory)
The application is configured to use H2 in-memory database for development. No additional setup required.

#### For Production (MySQL/PostgreSQL)
Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lakgamana_db
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. Build and Run

```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access API Documentation

Once the application is running, access Swagger UI at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🗄️ Database Schema

The application uses Flyway migrations to manage database schema:

- **V1__Create_initial_schema.sql**: Creates all tables and indexes
- **V2__Insert_seed_data.sql**: Inserts sample data for development

### Key Tables:
- `users`: User accounts and profiles
- `trains`: Train schedules and details
- `bookings`: Reservation records
- `passengers`: Passenger details for bookings
- `payments`: Payment transaction records
- `feedback`: User feedback and admin responses
- `train_features`: Train amenities and features

## 🔐 Authentication & Security

### JWT Token Structure
```json
{
  "sub": "user@example.com",
  "iat": 1640995200,
  "exp": 1641081600,
  "role": "USER"
}
```

### API Endpoints Security

| Endpoint | Authentication Required | Roles Allowed |
|----------|------------------------|---------------|
| `/api/auth/*` | No | - |
| `/api/trains/*` | No | - |
| `/api/bookings/*` | Yes | USER, ADMIN |
| `/api/payments/*` | Yes | USER, ADMIN |
| `/api/feedback/*` | Yes | USER, ADMIN |
| `/api/admin/*` | Yes | ADMIN |

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/profile` - Get current user profile

### Trains
- `GET /api/trains` - Get all trains
- `GET /api/trains/{id}` - Get train by ID
- `POST /api/trains/search` - Search trains

### Bookings
- `GET /api/bookings` - Get all bookings (Admin)
- `GET /api/bookings/{id}` - Get booking by ID
- `GET /api/bookings/user/{userId}` - Get user bookings
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/{id}/status` - Update booking status
- `PUT /api/bookings/{id}/reschedule` - Reschedule booking

### Payments
- `POST /api/payments/process` - Process payment
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments` - Get all payments (Admin)

### Feedback
- `POST /api/feedback/submit` - Submit feedback
- `GET /api/feedback` - Get all feedback (Admin)
- `GET /api/feedback/user/{userId}` - Get user feedback
- `DELETE /api/feedback/{id}` - Delete feedback (Admin)

### Admin
- `GET /api/admin/dashboard` - Get dashboard statistics
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{id}/status` - Update user status
- `GET /api/admin/trains` - Get all trains (Admin)
- `POST /api/admin/trains` - Create train
- `PUT /api/admin/trains/{id}` - Update train
- `DELETE /api/admin/trains/{id}` - Delete train

## 🧪 Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Coverage
The project includes comprehensive tests for:
- Controller layer (REST endpoints)
- Service layer (business logic)
- Security configuration
- JWT token handling
- Database operations

## 🚀 Deployment

### Docker Deployment

```bash
# Build Docker image
docker build -t lakgamana-backend .

# Run container
docker run -p 8080:8080 lakgamana-backend
```

### Production Configuration

1. Update `application.yml` with production database settings
2. Set JWT secret key as environment variable
3. Configure CORS settings for your frontend domain
4. Enable Flyway migrations
5. Set up proper logging configuration

## 📝 Configuration

### Environment Variables

```bash
# JWT Configuration
JWT_SECRET=your-secret-key-here

# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/lakgamana_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Server Configuration
SERVER_PORT=8080
```

### Application Properties

Key configuration options in `application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

jwt:
  secret: your-secret-key-here
  expiration: 86400000
  refresh-expiration: 604800000
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the API documentation at `/swagger-ui.html`

## 🔄 Version History

- **v1.0.0** - Initial release with core functionality
- **v1.1.0** - Added comprehensive testing
- **v1.2.0** - Enhanced security and validation
- **v1.3.0** - Added admin dashboard features

---

**Lakgamana Train Reservation System** - Making train travel convenient and accessible for everyone! 🚂✨
