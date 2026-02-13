# JavaCup2 Frontend

A modern React and Three.js-based frontend for the JavaCup2 project.

## Architecture Overview

This frontend application is built with the following technologies:

### Core Technologies
- **React 19**: Modern UI library with latest features
- **Three.js**: 3D graphics library for WebGL rendering
- **React Three Fiber**: React renderer for Three.js, enabling declarative 3D scenes
- **React Three Drei**: Helper library providing useful abstractions for React Three Fiber
- **Vite**: Next-generation frontend build tool for fast development

### Project Structure

```
frontend/
├── public/           # Static assets
├── src/
│   ├── assets/      # Images, fonts, and other assets
│   ├── components/  # React components
│   ├── services/    # API services for backend integration
│   ├── utils/       # Utility functions
│   ├── App.jsx      # Main application component
│   ├── App.css      # Application styles
│   ├── main.jsx     # Application entry point
│   └── index.css    # Global styles
├── .env             # Environment variables (not committed)
├── .env.example     # Example environment configuration
├── index.html       # HTML entry point
├── package.json     # Dependencies and scripts
└── vite.config.js   # Vite configuration
```

## Backend Configuration

The frontend communicates with the JavaCup backend API. You can configure the backend URL using environment variables.

### Setting up the Backend URL

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and set the backend URL:
   ```
   VITE_API_BASE_URL=http://localhost:8080
   ```

3. If deploying to production, set the environment variable to your backend server URL:
   ```
   VITE_API_BASE_URL=https://your-backend-server.com
   ```

**Note:** The `.env` file is not committed to git. Always use `.env.example` as a template.

### Backend API Integration

The frontend integrates with the following backend endpoints:

- **GET /api/tactics** - Fetches available tactics for team selection
- **POST /api/match/run** - Runs a match between two tactics and returns the result
- **GET /api/health** - Health check to verify backend availability

All API calls are handled through the `src/services/api.js` module.

## Getting Started

### Prerequisites
- Node.js 18+ 
- npm or yarn

### Installation

```bash
cd frontend
npm install
```

### Development

Start the development server with hot module replacement:

```bash
npm run dev
```

The application will be available at `http://localhost:5173`

### Building for Production

Build the application for production:

```bash
npm run build
```

The production-ready files will be in the `dist/` directory.

### Preview Production Build

Preview the production build locally:

```bash
npm run preview
```

### Linting

Run ESLint to check code quality:

```bash
npm run lint
```

## How to Make Changes

### Adding New Components

1. Create a new file in `src/` directory (e.g., `src/MyComponent.jsx`)
2. Import and use it in `App.jsx` or other components
3. Follow the existing code structure and naming conventions

### Working with 3D Graphics

The application uses React Three Fiber for 3D rendering. Key concepts:

- **Canvas**: The root component that sets up the Three.js renderer
- **Mesh**: 3D objects in the scene
- **Geometry**: Defines the shape (e.g., boxGeometry, sphereGeometry)
- **Material**: Defines appearance (color, texture, reflectivity)
- **Lights**: Illuminates the scene (ambientLight, pointLight, etc.)
- **Controls**: User interaction (OrbitControls for camera movement)

Example:
```jsx
<Canvas>
  <ambientLight intensity={0.5} />
  <mesh>
    <boxGeometry args={[1, 1, 1]} />
    <meshStandardMaterial color="hotpink" />
  </mesh>
</Canvas>
```

### Adding New Dependencies

```bash
npm install <package-name>
```

Always verify compatibility with React 19 and the existing tech stack.

### Styling

- Component-specific styles: Create a `.css` file alongside your component
- Global styles: Modify `src/index.css`
- CSS Modules: Supported by Vite (use `.module.css` extension)

## Development Best Practices

1. **Component Structure**: Keep components small and focused on a single responsibility
2. **State Management**: Use React hooks (useState, useEffect) for local state
3. **3D Performance**: Be mindful of the number of 3D objects and polygons for smooth rendering
4. **Code Style**: Follow the ESLint configuration for consistent code style
5. **Hot Module Replacement**: Vite provides instant updates; take advantage of it during development

## Testing

Currently, the project uses Vite's built-in build validation. To verify your changes work:

1. Run `npm run build` - ensures the code compiles without errors
2. Run `npm run preview` - test the production build locally
3. Check for console errors in the browser developer tools

## Deployment

The frontend can be deployed to any static hosting service:

- Vercel
- Netlify
- GitHub Pages
- AWS S3 + CloudFront
- Any web server (serve the `dist/` folder)

## Troubleshooting

### Development server not starting
- Ensure Node.js 18+ is installed
- Delete `node_modules` and `package-lock.json`, then run `npm install` again
- Check if port 5173 is already in use

### Build failures
- Run `npm run lint` to check for code issues
- Ensure all imports are correct
- Check browser console for runtime errors

### 3D scene not rendering
- Verify Canvas component is properly set up
- Check browser WebGL support (most modern browsers support it)
- Look for Three.js errors in the console

## Resources

- [React Documentation](https://react.dev)
- [Three.js Documentation](https://threejs.org/docs/)
- [React Three Fiber Documentation](https://docs.pmnd.rs/react-three-fiber)
- [React Three Drei Documentation](https://github.com/pmndrs/drei)
- [Vite Documentation](https://vitejs.dev)
