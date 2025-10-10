# Frontend-Backend API Mapping

This document maps the frontend components and their API calls to the corresponding backend endpoints, ensuring seamless integration between the React frontend and Spring Boot backend.

## 🔗 Authentication Flow

### Frontend Components → Backend APIs

| Frontend Component | API Call | Backend Endpoint | Method | Description |
|-------------------|----------|------------------|---------|-------------|
| `LoginPage.js` | `POST /api/auth/login` | `/api/auth/login` | POST | User authentication |
| `RegisterPage.js` | `POST /api/auth/register` | `/api/auth/register` | POST | User registration |
| `ForgotPasswordPage.js` | `POST /api/auth/forgot-password` | `/api/auth/forgot-password` | POST | Password reset request |
| `AuthContext` | `GET /api/auth/profile` | `/api/auth/profile` | GET | Get current user profile |
| `AuthContext` | `POST /api/auth/refresh` | `/api/auth/refresh` | POST | Refresh JWT token |

### Request/Response Mapping

#### Login Request
```javascript
// Frontend (LoginPage.js)
{
  email: "user@example.com",
  password: "password123"
}

// Backend (LoginRequest.java)
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Auth Response
```javascript
// Frontend expects
{
  success: true,
  data: {
    token: "jwt-token",
    refreshToken: "refresh-token",
    user: {
      id: 1,
      firstName: "John",
      lastName: "Doe",
      email: "john@example.com",
      role: "USER"
    }
  }
}

// Backend returns (AuthResponse.java)
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "jwt-token",
    "refreshToken": "refresh-token",
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com",
      "role": "USER"
    }
  }
}
```

## 🚂 Train Management

### Frontend Components → Backend APIs

| Frontend Component | API Call | Backend Endpoint | Method | Description |
|-------------------|----------|------------------|---------|-------------|
| `HomePage.js` | `GET /api/trains` | `/api/trains` | GET | Get all trains |
| `BookingPage.js` | `POST /api/trains/search` | `/api/trains/search` | GET | Search trains by route/date |
| `PopularRoutes.js` | `GET /api/trains` | `/api/trains` | GET | Get featured trains |
| `AdminTrainsPage.js` | `GET /api/admin/trains` | `/api/admin/trains` | GET | Get all trains (Admin) |
| `AdminTrainsPage.js` | `POST /api/admin/trains` | `/api/admin/trains` | POST | Create new train |
| `AdminTrainsPage.js` | `PUT /api/admin/trains/{id}` | `/api/admin/trains/{id}` | PUT | Update train |
| `AdminTrainsPage.js` | `DELETE /api/admin/trains/{id}` | `/api/admin/trains/{id}` | DELETE | Delete train |

### Train Data Mapping

#### Train Search Request
```javascript
// Frontend (BookingPage.js)
{
  from: "Colombo Fort",
  to: "Kandy",
  date: "2024-12-15",
  adults: 2,
  children: 1
}

// Backend (TrainSearchRequest.java)
{
  "from": "Colombo Fort",
  "to": "Kandy",
  "date": "2024-12-15",
  "adults": 2,
  "children": 1
}
```

#### Train Response
```javascript
// Frontend expects
{
  id: 1,
  name: "Intercity Express",
  type: "EXPRESS",
  route: "Colombo Fort - Kandy",
  fromStation: "Colombo Fort",
  toStation: "Kandy",
  departureTime: "08:30",
  arrivalTime: "11:45",
  duration: "3h 15m",
  distance: "120 km",
  price: {
    ECONOMY: 1200,
    BUSINESS: 2500,
    FIRST: 3500
  },
  seats: {
    ECONOMY: 150,
    BUSINESS: 30,
    FIRST: 20
  },
  status: "ACTIVE",
  features: ["WiFi", "AC", "Food Service"]
}

// Backend returns (TrainResponse.java)
{
  "id": 1,
  "name": "Intercity Express",
  "type": "EXPRESS",
  "route": "Colombo Fort - Kandy",
  "fromStation": "Colombo Fort",
  "toStation": "Kandy",
  "departureTime": "08:30:00",
  "arrivalTime": "11:45:00",
  "duration": "3h 15m",
  "distance": "120 km",
  "price": {
    "ECONOMY": 1200,
    "BUSINESS": 2500,
    "FIRST": 3500
  },
  "seats": {
    "ECONOMY": 150,
    "BUSINESS": 30,
    "FIRST": 20
  },
  "status": "ACTIVE",
  "features": ["WiFi", "AC", "Food Service"]
}
```

## 🎫 Booking Management

### Frontend Components → Backend APIs

| Frontend Component | API Call | Backend Endpoint | Method | Description |
|-------------------|----------|------------------|---------|-------------|
| `BookingPage.js` | `POST /api/bookings` | `/api/bookings` | POST | Create new booking |
| `MyTicketsPage.js` | `GET /api/bookings/user/{userId}` | `/api/bookings/user/{userId}` | GET | Get user bookings |
| `MyTicketsPage.js` | `PUT /api/bookings/{id}/cancel` | `/api/bookings/{id}/status` | PUT | Cancel booking |
| `AdminReservationsPage.js` | `GET /api/admin/bookings` | `/api/admin/bookings` | GET | Get all bookings |
| `AdminReservationsPage.js` | `PUT /api/admin/bookings/{id}/status` | `/api/admin/bookings/{id}/status` | PUT | Update booking status |

### Booking Data Mapping

#### Booking Request
```javascript
// Frontend (BookingPage.js)
{
  trainId: 1,
  departureDate: "2024-12-15",
  seatClass: "ECONOMY",
  passengers: [
    {
      name: "John Doe",
      age: 35,
      gender: "MALE",
      idType: "PASSPORT",
      idNumber: "P1234567"
    }
  ]
}

// Backend (BookingRequest.java)
{
  "trainId": 1,
  "departureDate": "2024-12-15",
  "seatClass": "ECONOMY",
  "passengers": [
    {
      "name": "John Doe",
      "age": 35,
      "gender": "MALE",
      "idType": "PASSPORT",
      "idNumber": "P1234567"
    }
  ]
}
```

#### Booking Response
```javascript
// Frontend expects
{
  id: 1,
  userId: 1,
  userName: "John Doe",
  trainId: 1,
  trainName: "Intercity Express",
  route: "Colombo Fort - Kandy",
  departureDate: "2024-12-15",
  departureTime: "08:30",
  arrivalTime: "11:45",
  seatClass: "ECONOMY",
  seatNumber: "E-15",
  passengers: [...],
  totalAmount: 1200.00,
  status: "CONFIRMED",
  bookingDate: "2024-12-10T10:30:00",
  paymentMethod: "CREDIT_CARD",
  paymentStatus: "COMPLETED"
}

// Backend returns (BookingResponse.java)
{
  "id": 1,
  "userId": 1,
  "userName": "John Doe",
  "trainId": 1,
  "trainName": "Intercity Express",
  "route": "Colombo Fort - Kandy",
  "departureDate": "2024-12-15",
  "departureTime": "08:30:00",
  "arrivalTime": "11:45:00",
  "seatClass": "ECONOMY",
  "seatNumber": "E-15",
  "passengers": [...],
  "totalAmount": 1200.00,
  "status": "CONFIRMED",
  "bookingDate": "2024-12-10T10:30:00",
  "paymentMethod": "CREDIT_CARD",
  "paymentStatus": "COMPLETED"
}
```

## 💳 Payment Processing

### Frontend Components → Backend APIs

| Frontend Component | API Call | Backend Endpoint | Method | Description |
|-------------------|----------|------------------|---------|-------------|
| `PaymentPage.js` | `POST /api/payments/process` | `/api/payments/process` | POST | Process payment |
| `PaymentPage.js` | `GET /api/payments/{id}` | `/api/payments/{id}` | GET | Get payment details |
| `AdminPaymentsPage.js` | `GET /api/admin/payments` | `/api/admin/payments` | GET | Get all payments |

### Payment Data Mapping

#### Payment Request
```javascript
// Frontend (PaymentPage.js)
{
  bookingId: 1,
  amount: 1200.00,
  currency: "LKR",
  method: "CREDIT_CARD",
  cardNumber: "1234567890123456",
  expiryMonth: "12",
  expiryYear: "2025",
  cvv: "123"
}

// Backend (PaymentRequest.java)
{
  "bookingId": 1,
  "amount": 1200.00,
  "currency": "LKR",
  "method": "CREDIT_CARD",
  "cardNumber": "1234567890123456",
  "expiryMonth": "12",
  "expiryYear": "2025",
  "cvv": "123"
}
```

## 💬 Feedback System

### Frontend Components → Backend APIs

| Frontend Component | API Call | Backend Endpoint | Method | Description |
|-------------------|----------|------------------|---------|-------------|
| `MyTicketsPage.js` | `POST /api/feedback/submit` | `/api/feedback/submit` | POST | Submit feedback |
| `AdminFeedbackPage.js` | `GET /api/admin/feedback` | `/api/admin/feedback` | GET | Get all feedback |
| `AdminFeedbackPage.js` | `PUT /api/admin/feedback/{id}/response` | `/api/admin/feedback/{id}/response` | PUT | Admin response |
| `AdminFeedbackPage.js` | `DELETE /api/admin/feedback/{id}` | `/api/admin/feedback/{id}` | DELETE | Delete feedback |

### Feedback Data Mapping

#### Feedback Request
```javascript
// Frontend (MyTicketsPage.js)
{
  bookingId: 1,
  trainId: 1,
  rating: 5,
  title: "Excellent Service!",
  comment: "The train was clean and comfortable.",
  category: "SERVICE"
}

// Backend (FeedbackRequest.java)
{
  "bookingId": 1,
  "trainId": 1,
  "rating": 5,
  "title": "Excellent Service!",
  "comment": "The train was clean and comfortable.",
  "category": "SERVICE"
}
```

## 👨‍💼 Admin Dashboard

### Frontend Components → Backend APIs

| Frontend Component | API Call | Backend Endpoint | Method | Description |
|-------------------|----------|------------------|---------|-------------|
| `AdminDashboard.js` | `GET /api/admin/dashboard` | `/api/admin/dashboard` | GET | Get dashboard stats |
| `AdminUsersPage.js` | `GET /api/admin/users` | `/api/admin/users` | GET | Get all users |
| `AdminUsersPage.js` | `PUT /api/admin/users/{id}/status` | `/api/admin/users/{id}/status` | PUT | Update user status |

### Dashboard Data Mapping

#### Dashboard Response
```javascript
// Frontend expects
{
  totalTrains: 6,
  totalReservations: 5,
  totalUsers: 4,
  totalFeedback: 5,
  totalRevenue: 12000.00,
  confirmedBookings: 3,
  cancelledBookings: 0,
  pendingPayments: 1,
  recentBookings: [...],
  recentFeedback: [...]
}

// Backend returns (DashboardResponse.java)
{
  "totalTrains": 6,
  "totalReservations": 5,
  "totalUsers": 4,
  "totalFeedback": 5,
  "totalRevenue": 12000.00,
  "confirmedBookings": 3,
  "cancelledBookings": 0,
  "pendingPayments": 1,
  "recentBookings": [...],
  "recentFeedback": [...]
}
```

## 🔒 Security & Authentication

### JWT Token Handling

| Frontend Action | Backend Validation |
|----------------|-------------------|
| Store JWT token in localStorage | Validate token signature and expiration |
| Include token in Authorization header | Extract user from token claims |
| Refresh token on expiry | Generate new access token |
| Clear token on logout | Invalidate token (optional) |

### CORS Configuration

```java
// Backend (SecurityConfig.java)
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "https://your-frontend-domain.com"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    return new UrlBasedCorsConfigurationSource();
}
```

## 📊 Error Handling

### Frontend Error Handling
```javascript
// Frontend expects consistent error format
{
  success: false,
  message: "Error description",
  status: 400,
  timestamp: "2024-12-10T10:30:00Z",
  path: "/api/bookings"
}
```

### Backend Error Response
```java
// Backend returns (ApiResponse.java)
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "status": "BAD_REQUEST",
  "timestamp": "2024-12-10T10:30:00Z",
  "path": "/api/bookings"
}
```

## 🧪 Testing Integration

### API Testing Endpoints

| Test Type | Frontend Mock | Backend Endpoint |
|-----------|---------------|------------------|
| Unit Tests | Mock API calls | Controller tests |
| Integration Tests | Real API calls | Service tests |
| E2E Tests | Full user flow | MockMvc tests |

## 📝 Notes

1. **Date Format**: Frontend uses ISO date strings, backend uses `LocalDate`/`LocalDateTime`
2. **Time Format**: Frontend uses "HH:mm", backend uses `LocalTime`
3. **Currency**: All amounts are in LKR (Sri Lankan Rupees)
4. **Authentication**: JWT tokens are included in Authorization header
5. **Validation**: Both frontend and backend validate input data
6. **Error Handling**: Consistent error response format across all endpoints

This mapping ensures that the frontend and backend are perfectly synchronized, providing a seamless user experience and robust API integration.
