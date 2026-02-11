# JavaCup 2013 - Football Simulation Framework

## Table of Contents
- [General Description](#general-description)
- [Backend Architecture](./BACKEND.md) - Detailed backend documentation
- [Frontend Architecture](./FRONTEND.md) - Detailed frontend documentation
- [Setup and Running Instructions](./SETUP_AND_RUNNING.md) - Complete setup guide

---

## General Description

### Overview

**JavaCup 2013** is a comprehensive football (soccer) simulation framework written in Java. It provides a complete environment for programming AI-controlled football teams that compete against each other in simulated matches. The project was designed as a programming competition where participants develop custom tactical AI strategies to control their teams during matches.

### What is JavaCup?

JavaCup is a virtual football tournament where each participant programs the behavior of a football team in Java. The submitted tactics compete in a tournament to determine the best AI implementation. The framework simulates complete 11-vs-11 football matches with realistic physics, player movements, ball trajectories, and game rules.

### Key Features

1. **Complete Match Simulation Engine**
   - Full 11-vs-11 football matches with 3600 iterations per match
   - Realistic physics engine for ball trajectories (air and ground)
   - Player energy and acceleration systems
   - Offside detection and foul management
   - Match events (goals, rebounds, whistles, corner kicks, throw-ins)

2. **AI Tactical Framework**
   - Participants implement custom AI by creating classes that implement the `Tactic` interface
   - Tactics receive real-time game state information and issue commands to players
   - Players can be commanded to move to positions or kick the ball
   - Support for different formations and strategies

3. **Dual Visualization System**
   - **OpenGL 3D Viewer** - Advanced 3D rendering using Slick2D/LWJGL libraries
   - **Basic 2D Viewer** - Simple Swing-based 2D visualization
   - Real-time match playback with audio feedback
   - Multiple stadium backgrounds and customizable uniforms

4. **Graphical Tactic Editor**
   - Visual tool for configuring team details (name, colors, uniforms)
   - Player configuration interface (names, numbers, attributes)
   - Formation editor for positioning players
   - Kick simulation tool for testing ball trajectories
   - Automatic code generation for tactics

5. **Tournament System**
   - Support for running multiple matches
   - Match recording and replay functionality
   - Results tracking and winner determination

6. **Security Features**
   - Code scanning (Sherlock) to prevent cheating
   - Sandbox restrictions on file I/O and network access
   - Validation of tactic implementations

### Project Structure

```
javacup2013/
├── src/                          # Source code
│   ├── JavaCup.java             # Main application entry point
│   ├── Torneo.java              # Tournament/match executor
│   ├── Sherlock.java            # Security code scanner
│   ├── JavaCupCodeGenerator.java # Tactic editor GUI
│   └── org/javahispano/javacup/
│       ├── model/               # Backend - Game logic
│       │   ├── Tactic.java      # Main tactic interface
│       │   ├── TacticDetail.java # Team configuration interface
│       │   ├── PlayerDetail.java # Player configuration interface
│       │   ├── engine/          # Match simulation engine
│       │   ├── command/         # Player command system
│       │   ├── trajectory/      # Physics and ball trajectories
│       │   └── util/            # Utilities and constants
│       ├── render/              # Frontend - Visualization
│       │   ├── VisorOpenGl.java # 3D OpenGL viewer
│       │   ├── VisorBasico.java # 2D basic viewer
│       │   ├── PintaCancha.java # Field rendering
│       │   ├── PintaJugador.java # Player rendering
│       │   ├── PintaBalon.java  # Ball rendering
│       │   └── PintaMarcador.java # Scoreboard rendering
│       └── gui/                 # User interface
│           ├── principal/       # Main application window
│           └── asistente/       # Tactic editor assistant
├── tacticas/                    # Example tactics implementations
├── libs/                        # External libraries (Slick2D, LWJGL)
├── recursos/                    # Resources (images, sounds)
├── config.xml                   # Application configuration
└── tutorial2012.pdf            # Original tutorial (Spanish)
```

### Core Concepts

#### 1. Tactics (AI Strategy)
A **Tactic** is a Java class that implements the `org.javahispano.javacup.model.Tactic` interface. It defines:
- **Team Configuration** - Team name, colors, player roster and attributes
- **Formations** - Player positions at match start (when kicking off vs receiving)
- **AI Logic** - The `execute()` method that receives game state and returns player commands

#### 2. Match Simulation
Matches are simulated in discrete iterations:
- **3600 iterations per match** = 180 seconds of simulated game time
- **20 FPS** (frames per second) = 50ms per iteration in real-time
- Each iteration, tactics receive current game state via `GameSituations` object
- Tactics return a list of `Command` objects specifying what each player should do
- The match engine processes commands and updates game state (positions, ball trajectory, scores)

#### 3. Player Commands
Two types of commands control player actions:
- **CommandMoveTo** - Move player to a target position (with optional sprint)
- **CommandHitBall** - Kick the ball (by coordinate, angle, or dribble forward)

#### 4. Game State Information
The `GameSituations` object provides tactics with:
- Positions of all players (own team and opponents)
- Ball position, velocity, and altitude
- Which players can kick the ball
- Score, time remaining, possession
- Player energy levels and acceleration
- Ball trajectory predictions

#### 5. Player Attributes
Each player has three configurable attributes (0.0 to 1.0):
- **Speed** - How fast the player moves
- **Power** - Strength of kicks
- **Precision** - Accuracy of kicks (lower error = higher precision)

Teams have limited "credits" to distribute among players and attributes, requiring strategic choices.

### Use Cases

1. **AI Programming Competition** - Participants develop tactical AI to compete in tournaments
2. **Educational Tool** - Learn AI programming, game theory, and Java development
3. **Testing Ground** - Experiment with different tactical approaches and formations
4. **Entertainment** - Watch AI teams compete with visual and audio feedback
5. **Code Migration Reference** - This documentation serves as a complete reference for migrating the project to modern technologies

### Technology Stack

- **Language**: Java (originally Java 6/7 era, compatible with Java 21)
- **Graphics**: Slick2D, LWJGL (Lightweight Java Game Library) for OpenGL
- **UI**: Java Swing for GUI components
- **Audio**: OpenAL via LWJGL
- **Configuration**: XStream for XML serialization
- **Build**: Eclipse project structure with .classpath

### Competition Format

The JavaCup competition typically follows this format:
1. Participants develop and test their tactics locally
2. Tactics are validated using the Sherlock security scanner
3. All valid tactics compete in a tournament
4. Matches are simulated automatically
5. Winners are determined by goals scored and possession percentage

---


## Backend Architecture

For detailed documentation of the backend architecture, including all classes, methods, relationships, and physics formulas, please refer to:

**📄 [BACKEND.md](./BACKEND.md)**

The backend documentation covers:
- Core interfaces (Tactic, TacticDetail, PlayerDetail)
- Match simulation engine (Partido, GameSituations, Aceleracion)
- Player command system (CommandMoveTo, CommandHitBall)
- Ball physics and trajectories (AirTrajectory, FloorTrajectory)
- Utility classes (Constants, Position, TacticValidate)
- Complete data flow architecture and design patterns
- Migration considerations for modern technologies

---

## Frontend Architecture

For detailed documentation of the frontend visualization system, including all rendering components, GUI classes, and viewer implementations, please refer to:

**📄 [FRONTEND.md](./FRONTEND.md)**

The frontend documentation covers:
- Dual viewer system (VisorBasico 2D Swing, VisorOpenGl 3D)
- Rendering components (PintaCancha, PintaJugador, PintaBalon, PintaMarcador)
- GUI components (PrincipalFrame, AsistenteFrame, PrincipalDatos)
- Backend integration via PartidoInterface
- Update loop and rendering pipeline
- Audio system with event-driven playback
- Camera controls and coordinate transformation
- Complete architecture diagrams and data flow

---

## Setup and Running Instructions

For complete step-by-step instructions to set up, compile, and run JavaCup 2013 on Windows with Java 21, please refer to:

**📄 [SETUP_AND_RUNNING.md](./SETUP_AND_RUNNING.md)**

The setup guide covers:
- Prerequisites and system requirements
- Java 21 installation and configuration
- Project compilation (command line, Eclipse, IntelliJ)
- Running the application and launching matches
- Creating and testing custom tactics
- Graphical tactic editor usage
- Troubleshooting common issues
- Advanced configuration options
- Quick start summary for experienced users

**Quick Start** (for the impatient):
```batch
# 1. Install Java 21
# 2. Clone project
cd javacup2013

# 3. Compile
javac -encoding ISO-8859-1 -d bin -cp "libs/*;src;recursos;tacticas" src/JavaCup.java

# 4. Run
java -cp "bin;libs/*;recursos;tacticas" JavaCup

# 5. Select tactics, click "Ver Partido", enjoy!
```

---
