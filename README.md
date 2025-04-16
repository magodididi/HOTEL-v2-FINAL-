# **Hotel Booking (HOTEL-v2-FINAL-)**

**HOTEL-v2-FINAL-** is a hotel booking application that allows users to search for hotels, book rooms, manage their reservations, and handle their accounts. The application provides a REST API for interacting with hotel, room, and booking data.

## **Key Features**

### **Hotel and Room Management:**
- View a list of hotels with filtering by city and category.
- Retrieve details of a specific hotel.
- View available rooms in a hotel.

### **Room Booking:**
- Create, edit, and delete bookings.
- Filter bookings by user and date.
- Bulk booking of multiple rooms.

### **Users and Accounts:**
- User registration and authentication.
- Manage user profiles.

### **Analytics and Logging:**
- Track booking statistics.
- Log all operations.
- Retrieve logs for a specified period.

### **REST API:**
- Documented using Swagger.
- Supports integration with external services.

## **Technologies Used**

- **Programming Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **API Documentation**: Swagger
- **Containerization**: Docker

## **Installation and Setup**

### **Requirements**
- Installed **Java 17** or later.
- Installed **Maven**.
- Installed **PostgreSQL**.
- Installed **Docker** (optional, for containerized deployment).

### **Steps to Run the Application**
```bash
git clone https://github.com/magodididi/HOTEL-v2-FINAL-.git
cd hotelbookingv2
mvn clean install
mvn spring-boot:run
