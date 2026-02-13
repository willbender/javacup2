# javacup2
Based on javacup from javahispano, a football matches experience for programmers, or not?

## Project Structure

This project consists of three main components:

- **backend/** - Spring Boot REST API service for running matches
- **frontend/** - React-based web viewer for matches
- **javacup2013/** - Original JavaCup core engine and tactics

## Quick Start

### Backend

The backend provides REST API endpoints for tactics and match execution.

**Requirements:**
- Java 21
- Maven 3.9+

**Running the backend:**
```bash
cd backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

For more details, see [backend/README.md](backend/README.md)

### Frontend

The frontend is a web-based match viewer that integrates with the backend.

**Requirements:**
- Node.js 18+
- npm

**Setup:**
```bash
cd frontend
cp .env.example .env
# Edit .env to set VITE_API_BASE_URL if needed (defaults to http://localhost:8080)
npm install
```

**Running the frontend:**
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

For more details, see [frontend/README.md](frontend/README.md)

## Using the Application

1. Start the backend server (ensure it's running on port 8080)
2. Start the frontend development server
3. Open your browser to `http://localhost:5173`
4. Select tactics for home and away teams from the dropdowns
5. Click "Create Match" to run a match
6. View the match results and replay animation

## Backend API Integration

The frontend communicates with the backend using these endpoints:

- **GET /api/tactics** - Retrieves list of available tactics
- **POST /api/match/run** - Runs a match between two tactics
- **GET /api/health** - Health check endpoint

The backend URL is configurable via the `VITE_API_BASE_URL` environment variable in the frontend.

## Development

- Frontend linting: `cd frontend && npm run lint`
- Frontend build: `cd frontend && npm run build`
- Backend tests: `cd backend && mvn test`
- Backend build: `cd backend && mvn package`

## License

See [LICENSE](LICENSE) file for details.
