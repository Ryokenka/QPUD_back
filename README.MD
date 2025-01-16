# 2-Player Quiz App

This project is a real-time multiplayer quiz game application. Players can join a lobby, participate in a quiz, and view their game history. The application uses WebSocket communication for real-time interactions and is built using a Spring Boot backend and a React frontend.

## Prerequisites

To run the application locally, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
- **Node.js** and **npm**
- **PostgreSQL** database
- **Maven** (for building the Spring Boot application)

## Getting Started

### Backend Setup

1. **Clone the Backend Repository**:
    ```bash
    git clone https://github.com/Ryokenka/QPUD_back
    ```

2. **Clone the Frontend Repository**:
    ```bash
    git clone https://github.com/Ryokenka/QPUD_front
    ```

2. **Configure the Database**:
   
   Create a `.env` file in the root directory and add the following variables:
   ```env
   DATABASE_USER=Admin
   DATABASE_PASSWORD=Password
   DATABASE_NAME=database1
   ```

   Then run `docker-compose up`

3. **Run the SQL Script**:
   
   Navigate to the `initdb` directory and run the SQL script to create and populate the database tables.

3. **Run the Backend**:
    ```bash
    mvn spring-boot:run
    ```

    By default, the backend server will run on `http://localhost:8080`.

### Frontend Setup

1. **Navigate to the Frontend Directory**:
    ```bash
    cd frontend
    ```

2. **Install Dependencies**:
    ```bash
    npm install
    ```

3. **Start the Frontend**:
    ```bash
    npm start
    ```

    The React application will be accessible at `http://localhost:3000`.

### WebSocket Configuration

The application uses WebSockets for real-time communication. Ensure the backend WebSocket endpoint (`/game`) is properly configured. In the frontend, the WebSocket is initialized in `WebSocketContext.js`:

```javascript
const ws = new WebSocket("ws://localhost:8080/game");
```

If the backend is running on a different URL or port, update the WebSocket URL accordingly.

## How to Play

1. Open the application in your browser (`http://localhost:3000`).
2. Navigate to the Lobby.
3. Join or create a game session.
4. Start the quiz and answer questions in real-time.

## Project Structure

### Backend
- **`src/main/java`**: Contains all Java source files.
- **`service`**: Contains business logic, including WebSocket utilities and game management.
- **`repository`**: Handles database operations.
- **`model`**: Defines data entities such as `Question`, `Player`, and `GameHistory`.
- **`handler`**: Manages WebSocket connections and message dispatching.
- **`config`**: Configuration files for WebSocket and application settings.

### Frontend
- **`src/components`**: Reusable React components like `Lobby` and `Quiz`.
- **`src/pages`**: Main pages such as `Home`, `History`, and `Quiz`.
- **`services`**: Includes WebSocket initialization and context.

## Useful Commands

### Backend
- Run the backend:
  ```bash
  mvn spring-boot:run
  ```
- Build the backend:
  ```bash
  mvn clean package
  ```

### Frontend
- Start the frontend:
  ```bash
  npm start
  ```
- Build the frontend:
  ```bash
  npm run build
  ```

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running and the credentials in `application.properties` are correct.
- Verify the database `quizapp` exists.

### WebSocket Connection Issues
- Check the backend WebSocket endpoint (`/game`) is accessible.
- Verify the frontend WebSocket URL matches the backend.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Note

Some features are still in development and may not work as expected. For example:
- Displaying the correct question count during the game.
- Reconnecting to a lobby after disconnection.

---

Enjoy the quiz game!