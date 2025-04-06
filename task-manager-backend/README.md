# Task Manager Backend

A Spring Boot application that provides a RESTful API for managing tasks. The application includes user authentication using JWT tokens and CRUD operations for tasks.

## Features

- User registration and authentication with JWT
- Create, read, update, and delete tasks
- Filter tasks by status and priority
- Secure API endpoints
- MySQL database integration

## Prerequisites

- Java 17 or higher
- Maven
- MySQL 8.0 or higher

## Setup & Running

1. **Database Setup**
   
   Create a MySQL database named `task_manager_db`:
   ```sql
   CREATE DATABASE task_manager_db;
   ```

2. **Configure Application**
   
   Update the database configuration in `src/main/resources/application.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/task_manager_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Build & Run**
   
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the API**
   
   The API will be available at `http://localhost:8080`

## API Endpoints

### Authentication

- **Register User**
  - `POST /api/auth/register`
  - Request Body:
    ```json
    {
      "username": "user1",
      "password": "password123",
      "email": "user1@example.com"
    }
    ```

- **Login**
  - `POST /api/auth/login`
  - Request Body:
    ```json
    {
      "username": "user1",
      "password": "password123"
    }
    ```
  - Returns a JWT token for authentication

- **Validate Token**
  - `GET /api/auth/validate`
  - Headers: `Authorization: Bearer {token}`

### Tasks

All task endpoints require authentication via JWT token in the Authorization header: `Authorization: Bearer {token}`

- **Get All Tasks**
  - `GET /api/tasks`

- **Get Task by ID**
  - `GET /api/tasks/{id}`

- **Create Task**
  - `POST /api/tasks`
  - Request Body:
    ```json
    {
      "title": "Complete project",
      "description": "Finish the task manager project",
      "dueDate": "2023-07-20",
      "priority": "HIGH",
      "status": "TODO"
    }
    ```

- **Update Task**
  - `PUT /api/tasks/{id}`
  - Request Body: Same as Create Task

- **Delete Task**
  - `DELETE /api/tasks/{id}`

- **Get Tasks by Status**
  - `GET /api/tasks/status/{status}`
  - Where status is one of: TODO, IN_PROGRESS, DONE

- **Get Tasks by Priority**
  - `GET /api/tasks/priority/{priority}`
  - Where priority is one of: HIGH, MEDIUM, LOW

## Task Model

```json
{
  "id": 1,
  "title": "Complete project",
  "description": "Finish the task manager project",
  "dueDate": "2023-07-20",
  "priority": "HIGH",
  "status": "TODO",
  "userId": 1
}
```

Valid Status values: TODO, IN_PROGRESS, DONE
Valid Priority values: HIGH, MEDIUM, LOW
