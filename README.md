<div align="center">


# Lakgamana Train Reservation System

A comprehensive full-stack train reservation system built with modern web technologies, featuring a React/Next.js frontend and Spring Boot backend with JWT authentication, role-based access control, and a complete admin dashboard.

![Next.js](https://img.shields.io/badge/Next.js-15.5.0-black)
![React](https://img.shields.io/badge/React-19.1.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.4.17-blue)

</div>

<p align="center">
  <img src="images/Screenshot 2025-10-26 135436.png" alt="Preview" width="100%" style="border-radius: 15px;" />
</p>

## Features

### Core Features
- **Easy Train Booking**: Intuitive search and booking interface with real-time availability
- **User Authentication**: Secure JWT-based authentication with role management
- **Payment Processing**: Multiple payment methods with secure transaction handling
- **Ticket Management**: Digital ticket system with QR codes and PDF generation
- **Admin Dashboard**: Comprehensive admin panel for system management
- **Responsive Design**: Mobile-first design that works on all devices
- **Real-time Updates**: Live train schedules and booking status updates

### User Features
- User registration and profile management
- Train search with filters (date, route, class)
- Seat selection and booking
- Payment processing with multiple options
- Digital ticket management
- Booking history and cancellation
- Feedback and rating system

### Admin Features
- Dashboard with analytics and statistics
- Train management (add, edit, delete trains)
- Route management and scheduling
- User management and role assignment
- Reservation monitoring and management
- Payment tracking and reports
- Feedback management and responses
- System configuration and settings

## Tech Stack

### Frontend
- **Next.js 15.5.0** - React framework with App Router
- **React 19.1.0** - Modern React with latest features
- **Tailwind CSS** - Utility-first CSS framework
- **Lucide React** - Beautiful icon library
- **Axios** - HTTP client for API calls
- **Vitest** - Testing framework

### Backend
- **Java 17** - Modern Java with latest features
- **Spring Boot 3.x** - Enterprise-grade framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **JWT** - Token-based authentication
- **H2/MySQL** - Database (H2 for dev, MySQL for production)
- **Flyway** - Database migration tool
- **Maven** - Dependency management
- **Swagger/OpenAPI** - API documentation

## Getting Started

### Prerequisites
- Node.js 18+ and pnpm (or npm/yarn)
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or PostgreSQL 13+ (for production)
- Git

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/lakgamana-frontend.git
cd lakgamana-frontend
```

**2. Install frontend dependencies**
```bash
pnpm install
# or
npm install
```

**3. Set up the backend**
```bash
cd lakgamana-backend
mvn clean install
```

**4. Configure environment variables**

Create `.env.local` in the root directory:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_APP_NAME=Lakgamana
```

**5. Start the development servers**

Backend (Terminal 1):
```bash
cd lakgamana-backend
mvn spring-boot:run
```

Frontend (Terminal 2):
```bash
pnpm dev
# or
npm run dev
```

**6. Access the application**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- API Documentation: http://localhost:8080/swagger-ui.html

## Project Structure

```
lakgamana-frontend/
├── src/
│   ├── app/                    # Next.js App Router pages
│   │   ├── admin/             # Admin dashboard pages
│   │   ├── booking/           # Train booking page
│   │   ├── login/             # Authentication pages
│   │   ├── tickets/           # User ticket management
│   │   └── payment/           # Payment processing
│   ├── components/            # Reusable React components
│   │   ├── ui/                # UI component library
│   │   ├── admin/             # Admin-specific components
│   │   ├── Hero.js            # Landing page hero section
│   │   ├── Features.js        # Features showcase
│   │   ├── Navbar.js          # Navigation component
│   │   └── Footer.js          # Footer component
│   └── styles/                # Global styles
├── lakgamana-backend/         # Spring Boot backend
│   ├── src/main/java/         # Java source code
│   ├── src/main/resources/    # Configuration files
│   └── src/test/              # Test files
├── public/                    # Static assets
└── docs/                      # Documentation
```

## Configuration

### Database Setup

**Development (H2 - Default)**

No additional setup required. The application uses H2 in-memory database.

**Production (MySQL/PostgreSQL)**

Update `lakgamana-backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lakgamana_db
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Environment Variables

**Frontend (.env.local)**
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_APP_NAME=Lakgamana
NEXT_PUBLIC_ENVIRONMENT=development
```

**Backend (application.yml)**
```yaml
jwt:
  secret: your-jwt-secret-key
  expiration: 86400000 # 24 hours
```

## Testing

### Frontend Testing
```bash
# Run tests
pnpm test

# Run tests in watch mode
pnpm test:watch

# Run linting
pnpm lint
```

### Backend Testing
```bash
cd lakgamana-backend
mvn test
```

## API Documentation

The backend provides comprehensive API documentation through Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Key API Endpoints

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/profile` - Get user profile

#### Trains & Booking
- `GET /api/trains` - Get all trains
- `GET /api/trains/{id}` - Get train details
- `POST /api/bookings` - Create booking
- `GET /api/bookings/user` - Get user bookings

#### Admin
- `GET /api/admin/dashboard` - Admin dashboard data
- `POST /api/admin/trains` - Add new train
- `PUT /api/admin/trains/{id}` - Update train
- `DELETE /api/admin/trains/{id}` - Delete train

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Access Control**: Admin and User roles
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Cross-origin request handling
- **Input Validation**: Server-side validation for all inputs
- **SQL Injection Protection**: Parameterized queries
- **XSS Protection**: Content Security Policy headers

## Deployment

### Frontend Deployment (Vercel)
1. Connect your GitHub repository to Vercel
2. Set environment variables in Vercel dashboard
3. Deploy automatically on push to main branch

### Backend Deployment (Railway/Heroku)
1. Create a new project on Railway/Heroku
2. Connect your GitHub repository
3. Set environment variables
4. Deploy automatically

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d
```

## Contributing

We welcome contributions. Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow the existing code style
- Write tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Team

- **Frontend Development**: Modern React/Next.js implementation
- **Backend Development**: Spring Boot with enterprise patterns
- **UI/UX Design**: Responsive design with Tailwind CSS
- **Database Design**: Optimized schema with proper relationships

## Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/yourusername/lakgamana-frontend/issues) page
2. Create a new issue with detailed description
3. Contact the development team

## Future Enhancements

- Mobile app (React Native)
- Real-time notifications
- Advanced analytics dashboard
- Multi-language support
- Social login integration
- Advanced search filters
- Loyalty program
- Group booking features

---