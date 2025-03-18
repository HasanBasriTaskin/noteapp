# Note Application

A simple note-taking application with a JavaFX client frontend and Java backend, utilizing MySQL for data storage and Docker for containerization.

## Project Overview

This project is a full-stack note application that allows users to create, read, update, and delete notes. The application consists of a JavaFX client for the user interface and a Java backend that communicates with a MySQL database for data persistence.

### Features (Planned)

- User authentication and authorization
- Create, view, edit, and delete notes
- Categorize notes with tags
- Search functionality
- Export notes to different formats (PDF, TXT, etc.)
- Sync across devices

## Technology Stack

- **Frontend**: JavaFX for desktop client application
- **Backend**: Java
- **Database**: MySQL
- **Containerization**: Docker
- **Build Tool**: Maven

## Project Structure

```
note-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── hasan/
│   │   │           └── note/
│   │   │               ├── client/         # JavaFX client code
│   │   │               ├── server/         # Backend server code
│   │   │               ├── model/          # Data models
│   │   │               ├── database/       # Database connection and operations
│   │   │               ├── service/        # Business logic
│   │   │               ├── util/           # Utility classes
│   │   │               ├── Note.java       # Note entity
│   │   │               └── NoteApp.java    # Main application entry point
│   │   └── resources/
│   │       ├── fxml/                       # JavaFX FXML layouts
│   │       ├── css/                        # Stylesheets
│   │       ├── images/                     # Application images
│   │       └── logback.xml                 # Logging configuration
│   └── test/
│       └── java/
│           └── com/
│               └── hasan/
│                   └── note/
│                       ├── client/         # Client tests
│                       ├── server/         # Server tests
│                       ├── database/       # Database tests
│                       ├── NoteAppTest.java
│                       └── NoteTest.java
├── docker/                                 # Docker configuration files
│   ├── docker-compose.yml
│   ├── Dockerfile.client
│   └── Dockerfile.server
├── sql/                                    # SQL scripts for database setup
│   └── init.sql
├── .gitignore
├── pom.xml                                # Maven configuration
└── README.md                              # This file
```

## Development Roadmap

### Phase 1: Setup and Basic Structure

- [x] Initialize Maven project
- [x] Set up basic project structure
- [x] Create initial model classes
- [ ] Configure logback for logging
- [ ] Setup Docker environment

### Phase 2: Database Implementation

- [ ] Design database schema
- [ ] Create SQL initialization scripts
- [ ] Implement database connection layer
- [ ] Create data access objects (DAOs)
- [ ] Write unit tests for database operations

### Phase 3: Backend Implementation

- [ ] Develop RESTful API for notes
- [ ] Implement service layer
- [ ] Add authentication and authorization
- [ ] Write unit tests for backend
- [ ] Implement error handling

### Phase 4: Frontend Implementation

- [ ] Create JavaFX views (FXML)
- [ ] Design CSS for the application
- [ ] Implement controllers
- [ ] Connect frontend to backend
- [ ] Add input validation
- [ ] Write unit tests for frontend

### Phase 5: Integration and Testing

- [ ] Integrate all components
- [ ] Perform integration testing
- [ ] Implement end-to-end testing
- [ ] Fix bugs and optimize performance

### Phase 6: Deployment

- [ ] Finalize Docker configuration
- [ ] Prepare for production
- [ ] Document deployment process
- [ ] Create user manual

## Getting Started (TBD)

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- MySQL (or Docker MySQL image)

### Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/note-app.git
   cd note-app
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Start the MySQL database and backend server using Docker:
   ```bash
   cd docker
   docker-compose up -d
   ```

4. Run the client application:
   ```bash
   mvn javafx:run
   ```

## Testing

To run the tests:

```bash
mvn test
```

## Documentation

Additional documentation can be found in the `docs` directory.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgements

- [JavaFX](https://openjfx.io/)
- [MySQL](https://www.mysql.com/)
- [Docker](https://www.docker.com/)
- [Maven](https://maven.apache.org/)