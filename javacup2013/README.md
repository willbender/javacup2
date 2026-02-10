# JavaCup 2013 - Football Simulation Framework

## Table of Contents
- [General Description](#general-description)
- [Backend Architecture](#backend-architecture)
- [Frontend Architecture](#frontend-architecture)
- [Setup and Running Instructions](#setup-and-running-instructions)

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

The backend is located in the `org.javahispano.javacup.model` package and contains all the game logic, match simulation, physics, and AI interfaces. This section provides a detailed description of every class and their relationships.

### Package Structure

```
org.javahispano.javacup.model/
├── Tactic.java                 # Main interface for AI tactics
├── TacticDetail.java           # Interface for team configuration
├── PlayerDetail.java           # Interface for player configuration
├── engine/                     # Match simulation engine
│   ├── Partido.java           # Core match simulator
│   ├── GameSituations.java    # Game state provider for tactics
│   ├── JugadorImpl.java       # Player implementation
│   ├── Aceleracion.java       # Acceleration physics
│   ├── PartidoInterface.java  # Public match API
│   ├── TacticaImpl.java       # Tactic wrapper
│   ├── TacticaDetalleImpl.java # TacticDetail wrapper
│   ├── PartidoGuardado.java   # Saved match for replay
│   └── Iteracion.java         # Iteration data holder
├── command/                    # Player command system
│   ├── Command.java           # Abstract command base
│   ├── CommandMoveTo.java     # Movement command
│   └── CommandHitBall.java    # Kicking command
├── trajectory/                 # Ball physics
│   ├── AbstractTrajectory.java # Base trajectory class
│   ├── AirTrajectory.java     # Aerial ball physics
│   └── FloorTrajectory.java   # Ground ball physics
└── util/                       # Utilities
    ├── Constants.java         # Game constants
    ├── Position.java          # 2D position utilities
    └── TacticValidate.java    # Tactic validation
```

### Core Interfaces

#### 1. Tactic Interface
**Package**: `org.javahispano.javacup.model`  
**Purpose**: Main interface that participants implement to create their AI tactics

**Methods**:
```java
public TacticDetail getDetail();
```
- Returns team configuration (name, coach, players, uniforms)
- Called once at match initialization

```java
public List<Command> execute(GameSituations sp);
```
- **Most important method** - Contains the AI logic
- Called every iteration (60 times per second)
- Receives `GameSituations` with current game state
- Returns list of commands for all 11 players
- Each command specifies what a player should do this iteration

```java
public Position[] getStartPositions(GameSituations sp);
```
- Returns array of 11 positions for players when team kicks off
- Used after conceding a goal or at match start
- Players must be in own half of field

```java
public Position[] getNoStartPositions(GameSituations sp);
```
- Returns array of 11 positions when opponent kicks off
- Used after scoring a goal
- Players can position anywhere on field

**Relationships**:
- Implemented by user-created tactic classes (in `tacticas/` folder)
- Used by `Partido` to get commands each iteration
- Wrapped by `TacticaImpl` for internal use

#### 2. TacticDetail Interface
**Package**: `org.javahispano.javacup.model`  
**Purpose**: Defines team configuration and visual appearance

**Methods**:
```java
public String getTacticName();        // Team name
public String getCountry();           // Country
public String getCoach();             // Coach name
public Color getShirtColor();         // Primary shirt color
public Color getShortsColor();        // Shorts color
public Color getShirtLineColor();     // Shirt stripe/line color
public Color getSocksColor();         // Socks color
public Color getGoalKeeper();         // Goalkeeper jersey color
public EstiloUniforme getStyle();     // Uniform style (stripes, etc.)
// Secondary kit colors (same methods with "2" suffix)
public Color getShirtColor2();
// ... (similar methods for alternate kit)
public PlayerDetail[] getPlayers();   // Array of 11 players
```

**Relationships**:
- Returned by `Tactic.getDetail()`
- Implemented by `TacticaDetalleImpl` (concrete implementation)
- Used by rendering system to display teams
- Contains array of `PlayerDetail` objects

#### 3. PlayerDetail Interface
**Package**: `org.javahispano.javacup.model`  
**Purpose**: Defines individual player configuration and attributes

**Methods**:
```java
public String getPlayerName();     // Player name
public Color getSkinColor();       // Skin color for rendering
public Color getHairColor();       // Hair color for rendering
public int getNumber();            // Jersey number (1-99)
public boolean isGoalKeeper();     // True if goalkeeper
public double getSpeed();          // Speed attribute [0.0-1.0]
public double getPower();          // Kick power attribute [0.0-1.0]
public double getPrecision();      // Kick precision attribute [0.0-1.0]
```

**Attributes Explained**:
- **Speed**: Affects how fast player moves (0.25-0.5 m/iteration)
- **Power**: Affects kick strength (1.2-2.4 m/iteration ball velocity)
- **Precision**: Lower value = less angular error on kicks
- Teams have limited "credits" to distribute among all players and attributes

**Relationships**:
- Returned by `TacticDetail.getPlayers()`
- Implemented by `JugadorImpl` (concrete implementation)
- Used by `GameSituations` to provide player stats to tactics
- Used by physics engine to calculate movement and kicks

_(Continuing with detailed class descriptions...)_

### Match Simulation Engine

The match simulation engine classes handle all game logic, physics calculations, and state management during a match.

#### 1. Partido.java (Core Match Engine)
**Package**: `org.javahispano.javacup.model.engine`  
**Lines of Code**: ~1627  
**Purpose**: The heart of the system - simulates complete football matches

This is the most complex and critical class in the backend. It orchestrates the entire match simulation.

**Key Responsibilities**:
- Execute match iterations (3600 total)
- Process tactical commands from both teams
- Calculate player movements with physics (acceleration, energy)
- Manage ball trajectories and physics
- Detect and handle game events (goals, fouls, offsides, out-of-bounds)
- Track match state and statistics
- Provide match information to viewers

**Key Fields**:

**Player State** (22 players total: 11 per team):
```java
private Position[] posLocal = new Position[11];     // Local team positions
private Position[] posVisita = new Position[11];    // Visiting team positions
private JugadorImpl[] jugadoresLocal;               // Local players config
private JugadorImpl[] jugadoresVisita;              // Visiting players config
private double[] energiaLocal = new double[11];     // Energy levels [0-1]
private double[] energiaVisita = new double[11];
private Aceleracion[] aceleracionLocal;             // Acceleration objects
private Aceleracion[] aceleracionVisita;
private int[] iterGolpearBalonLocal = new int[11];  // Kick cooldown timers
private int[] iterGolpearBalonVisita = new int[11];
```

**Ball State**:
```java
private Position balon;                    // Ball position (x, y) meters
private double balonDx, balonDy, balonDz;  // Ball velocity components
private double alturaBalon;                // Ball height above ground
private AbstractTrajectory trayectoria;    // Current trajectory object
```

**Match State**:
```java
private int estado;              // Match state (0-7, 7=finished)
private int iteracion;           // Current iteration (0-3600)
private int golesLocal;          // Local team score
private int golesVisita;         // Visiting team score
private int posesionLocal;       // Local possession counter
private int posesionVisita;      // Visiting possession counter
```

**Match Events** (flags set each iteration):
```java
private boolean gol;              // Goal scored
private boolean rebote;           // Ball bounced
private boolean silbato;          // Whistle blown
private boolean poste;            // Hit goalpost
private boolean ovacion;          // Crowd cheering
private boolean[] offSidePlayers; // Offside status per player
```

**Key Methods**:

```java
public void iterar()
```
The main simulation loop - executes one iteration. Called 3600 times per match.

**Processing Steps**:
1. **Check match end**: If estado == 7, match is over
2. **Update ball physics**: Calculate new ball position from trajectory
3. **Call tactics**: Execute both teams' `execute()` methods to get commands
4. **Process movements**: Update player positions based on CommandMoveTo
5. **Process kicks**: Determine if any player can kick, apply one kick if possible
6. **Create trajectory**: Generate new ball path from kick parameters
7. **Check goals**: Detect if ball crossed goal line
8. **Check boundaries**: Handle ball out of bounds (throw-ins, corners, goal kicks)
9. **Check offsides**: Verify offside violations
10. **Update physics**: Refresh energy and acceleration for all players
11. **Prepare next iteration**: Update game state for next cycle

```java
private void procesarComandos(List<Command> comandosLocal, List<Command> comandosVisita)
```
Processes all player commands from both teams:
- Separates movement commands from kick commands
- Handles conflicts (if multiple commands for same player, last one wins)
- Updates player positions based on CommandMoveTo
- Stores kick commands for later processing

```java
private void calcularRemate()
```
Determines which player (if any) kicks the ball:
- Checks each player's distance to ball (must be < 1.0 meters)
- Checks ball height (must be < 2.0 meters for field players, < 5.0 for goalkeeper)
- Applies probability based on ball velocity (harder to control fast-moving ball)
- If multiple players can kick, randomly selects one
- Applies angular error based on player's precision attribute
- Creates new trajectory (AirTrajectory or FloorTrajectory)
- Sets kick cooldown timer (30 iterations before player can kick again)

```java
private void detectarGol()
```
Detects goals:
- Checks if ball position crossed goal line
- Verifies ball height is below crossbar (< 5.0 meters)
- Verifies ball is within goal width
- Increments score counter
- Sets goal event flag
- Checks for offside before confirming goal

```java
private void verificarFueraDeJuego()
```
Implements offside rule:
- Finds position of second-last defender
- Compares attacking players' positions
- Only applies in opponent's half of field
- Nullifies goal if offside occurred
- Awards indirect free kick to defending team

```java
private void moverJugadores()
```
Updates all player positions:
- Calculates direction vector to target
- Applies speed factor (from player attributes)
- Applies energy factor (current energy level)
- Applies acceleration factor (from direction changes)
- Handles sprint mode (1.2× multiplier, high energy cost)
- Clamps positions to field boundaries

```java
private void actualizarEnergia()
```
Updates energy for all 22 players:
- Passive recovery: +0.00001 per iteration (very gradual)
- Sprint cost: -0.02 per iteration (significant)
- Minimum: 0.55 (can't go below)
- Maximum: 1.0 (full energy)
- Energy affects movement speed proportionally

**Match State Machine**:
- **Estado 0**: Initial kickoff setup
- **Estado 1**: Normal play (ball in play)
- **Estado 2**: After goal scored (preparing for kickoff)
- **Estado 3**: Ball out of bounds (preparing for throw-in)
- **Estado 4**: Corner kick
- **Estado 5**: Goal kick
- **Estado 6**: Free kick (direct or indirect)
- **Estado 7**: Match ended (3600 iterations completed)

**Relationships**:
- Implements `PartidoInterface` for external access
- Uses `TacticaImpl` wrappers for both teams' tactics
- Creates and updates `GameSituations` each iteration
- Uses `AbstractTrajectory` subclasses for ball physics
- Accessed by viewers for rendering
- Can be saved via `PartidoGuardado` for replay

#### 2. GameSituations.java (Game State Provider)
**Package**: `org.javahispano.javacup.model.engine`  
**Lines of Code**: ~416  
**Purpose**: Provides current match state to tactics for AI decision-making

This class acts as an information provider - it gives tactics everything they need to know about the current game state without allowing them to modify it.

**Key Fields** (duplicated for each team's perspective):
```java
private Position balon;                          // Ball position
private double alturaBalon;                      // Ball altitude
private int golesMios, golesContrarios;         // Scores from my perspective
private int iteracion;                           // Current iteration number
private Position[] misJugadores = new Position[11];   // My players' positions
private Position[] rivales = new Position[11];        // Opponent positions
private boolean[] puedenRematar = new boolean[11];    // Can my players kick?
private boolean[] puedenRematarRival = new boolean[11]; // Can opponents kick?
private int[][] iteracionesParaRematar = new int[2][11]; // Kick cooldown timers
private double[] miEnergia = new double[11];           // My players' energy
private double[] rivalEnergia = new double[11];        // Opponents' energy
private Aceleracion[] miAceleracion;                   // My acceleration objects
private Aceleracion[] rivalAceleracion;                // Opponents' acceleration
private boolean saco, sacaRival;                       // Who is taking kickoff?
private AbstractTrajectory trayectoria;                // Ball trajectory
private PlayerDetail[][] jugadores;                    // Player configurations
private boolean[] offSidePlayers = new boolean[11];   // Offside status
```

**Key Methods** (Public API for Tactics):

**Basic Information**:
```java
public Position[] myPlayers()           // My 11 player positions
public Position[] rivalPlayers()        // Opponent 11 player positions
public Position ballPosition()          // Ball position (x, y)
public double ballAltitude()            // Ball height (z)
public int myGoals()                    // My current score
public int rivalGoals()                 // Opponent's score
public int iteration()                  // Current iteration (0-3600)
```

**Player Capabilities**:
```java
public int[] canKick()                  // Indices of my players who can kick now
public int[] rivalCanKick()             // Indices of opponents who can kick now
public int[] iterationsToKick()         // Iterations until each of my players can kick
public int[] rivalIterationsToKick()    // Iterations until opponents can kick
```

**Player Attributes**:
```java
public PlayerDetail[] myPlayersDetail()      // My player configurations
public PlayerDetail[] rivalPlayersDetail()   // Opponent player configurations
public double getMyPlayerSpeed(int idx)      // Speed of my player idx
public double getMyPlayerPower(int idx)      // Power of my player idx
public double getMyPlayerError(int idx)      // Error (1-precision) of my player idx
// Similar methods for rival players
```

**Energy and Acceleration**:
```java
public double getMyPlayerEnergy(int idx)         // My player's energy [0-1]
public double getMyPlayerAceleration(int idx)    // My player's acceleration factor
public double getRivalEnergy(int idx)            // Opponent's energy
public double getRivalAceleration(int idx)       // Opponent's acceleration
```

**Match Status**:
```java
public boolean isStarts()               // True if I'm taking kickoff
public boolean isRivalStarts()          // True if opponent is taking kickoff
```

**Ball Trajectory Prediction**:
```java
public double[] getTrajectory(int iteracion)
```
Returns predicted ball position [x, y, z] at future iteration.
- Critical for AI planning: "Where will ball be in 10 iterations?"
- Uses physics trajectory calculations
- Accounts for air resistance, gravity, bounces

**Distance Calculations** (for AI planning):
```java
public double distanceIter(int playerIndex, int iter, boolean isSprint)
```
Calculates how far player will travel in a specific future iteration:
- Accounts for acceleration ramping up
- Accounts for energy decreasing over time
- Accounts for sprint multiplier if applicable
- Returns distance in meters

```java
public double distanceTotal(int playerIndex, int iter)
```
Calculates total distance player can cover over multiple iterations:
- Sums up distance from each iteration
- Used to answer: "Can player reach that position in time?"

**Offside Detection**:
```java
public boolean[] getOffSidePlayers()
```
Returns array indicating which of my players are currently offside

**Deprecated Methods**:
```java
@Deprecated
public int[] getRecoveryBall()
```
Attempts to predict which players can intercept ball (doesn't account for player blocking)

**Protected Methods** (Internal - called by Partido):
```java
protected void set(...)  // Multiple overloaded versions
```
Updates all game state fields - called by Partido each iteration before passing to tactics

**Relationships**:
- Created by `Partido` (one instance for each team)
- Updated by `Partido.iterar()` before calling tactics
- Passed to `Tactic.execute()` as parameter
- Provides read-only view of match state
- References shared `PlayerDetail` arrays
- Uses `AbstractTrajectory` for predictions

**Usage Pattern**:
```java
// In Partido.iterar():
gameLocalSituations.set(...);  // Update with current state
List<Command> cmdsLocal = tacticLocal.execute(gameLocalSituations);  // Get commands
```

_(Due to length constraints, I'll summarize the remaining backend classes more concisely while maintaining detail for migration purposes)_

#### 3. JugadorImpl.java (Player Implementation)
**Package**: `org.javahispano.javacup.model.engine`  
**Purpose**: Immutable implementation of PlayerDetail interface

**All Fields**:
```java
private String nombre;          // Player name
private Color colorPiel;        // Skin color for rendering
private Color colorPelo;        // Hair color for rendering
private int numero;             // Jersey number
private boolean esArquero;      // Is goalkeeper flag
private double velocidad;       // Speed attribute [0-1]
private double remate;          // Power attribute [0-1]
private double error;           // Error attribute [0-1] (1-precision)
```

**Constructor**: Takes all 8 parameters
**Methods**: Simple getters for each field
**Immutable**: No setters, fields are final

#### 4. Aceleracion.java (Acceleration Physics)
**Package**: `org.javahispano.javacup.model.engine`  
**Purpose**: Models realistic acceleration/deceleration during turns

**Fields**:
```java
private double aceleracionX = 1.0;  // X-axis acceleration [0-1]
private double aceleracionY = 1.0;  // Y-axis acceleration [0-1]
private double direccionX = 0;      // Previous X direction
private double direccionY = 0;      // Previous Y direction
```

**Key Method**:
```java
public void actualizar(double dirX, double dirY)
```
Called each iteration with new movement direction:
- If direction changes significantly: reduces acceleration to minimum (0.7-0.9)
- If direction maintained: gradually increases acceleration toward 1.0
- Recovery rate: Constants.ACELERACION_INCR (0.05) per iteration
- Models momentum: Can't instantly change direction at full speed

**Result**: Realistic turning physics where players slow down when changing direction

### Player Command System

#### 1. Command.java (Abstract Base)
**Package**: `org.javahispano.javacup.model.command`

**Enum**:
```java
public enum CommandType { MOVE_TO, HIT_BALL }
```

**Abstract Methods**:
```java
public abstract CommandType getCommandType();
public abstract int getPlayerIndex();  // Which player (0-10)
```

#### 2. CommandMoveTo.java (Movement)
**Package**: `org.javahispano.javacup.model.command`

**Fields**:
```java
private int indJugador;      // Player index (0-10)
private Position irA;        // Target position
private boolean sprint;      // Sprint mode flag
```

**Usage Example**:
```java
// Move player 5 to position (20, 10)
new CommandMoveTo(5, new Position(20, 10));

// Move player 3 to position (0, 0) with sprint
new CommandMoveTo(3, new Position(0, 0), true);
```

**Sprint Mode**:
- Speed multiplied by 1.2×
- Requires minimum energy of 0.8
- Costs 0.02 energy per iteration (vs passive 0.00001 recovery)
- Strategic trade-off: speed now vs stamina later

#### 3. CommandHitBall.java (Kicking)
**Package**: `org.javahispano.javacup.model.command`

**Three Kicking Modes**:

**1. Dribble Forward**:
```java
new CommandHitBall(playerIdx)
```
Player advances with ball under control

**2. Kick to Coordinate**:
```java
// Ground kick
new CommandHitBall(playerIdx, new Position(x, y), power, false);

// High kick (30° vertical angle)
new CommandHitBall(playerIdx, new Position(x, y), power, true);

// Custom vertical angle (0-60°)
new CommandHitBall(playerIdx, new Position(x, y), power, angleVertical);
```

**3. Kick at Angle**:
```java
// Kick at 45° horizontally, full power, high kick
new CommandHitBall(playerIdx, 45.0, 1.0, true);

// Kick at 90°, half power, 25° vertical angle
new CommandHitBall(playerIdx, 90.0, 0.5, 25.0);
```

**Parameters**:
- `power`: 0.0 to 1.0 (percentage of player's max power)
- `angle`: 0° to 360° (0° = right, 90° = up/forward, 180° = left, 270° = down/back)
- `verticalAngle`: 0° to 60° (0° = ground, 60° = steep lob)
- `highKick` boolean: shortcut for 30° vertical angle

**Kick Execution**:
- Only executes if player is within 1.0m of ball
- Only if ball is below 2.0m height (5.0m for goalkeeper)
- Only if cooldown timer expired (30 iterations since last kick)
- Angular error applied based on player precision
- Creates AirTrajectory (if vertical angle > 0) or FloorTrajectory

### Ball Physics System

#### 1. AbstractTrajectory.java (Base)
**Package**: `org.javahispano.javacup.model.trajectory`

**Concept**: Segmented trajectories
- Ball path divided into segments (each with own physics)
- Segments chain together recursively
- Allows transitions: air → bounce → ground → kicked → air...

**Abstract Methods**:
```java
public abstract double getX(double t);   // Horizontal distance at time t
public abstract double getY(double t);   // Vertical height at time t  
public abstract double getDt();          // Segment duration
```

#### 2. AirTrajectory.java (Aerial Physics)
**Package**: `org.javahispano.javacup.model.trajectory`

**Physics Model**: Air resistance + gravity

**Constants**:
```java
private static final double g = 9.8;    // Gravity (m/s²)
private static final double k = 0.7;    // Air resistance coefficient
private static final double n = 1.5;    // Drag exponent
private static final double ay = 2.3;   // Vertical velocity adjustment
```

**Formulas**:
```
Horizontal: X(t) = vx0 × (1 - e^(-k×n×t)) / k + vx0 × t + x0
Vertical:   Y(t) = y0 + vy0 × ay × t - g × t² / 2
```

**Chaining**:
- Constructor calculates when ball hits ground (Y = 0)
- Creates next segment automatically:
  - If significant vertical velocity remains → another AirTrajectory (bounce)
  - If velocity low → FloorTrajectory (rolls to stop)

#### 3. FloorTrajectory.java (Ground Physics)
**Package**: `org.javahispano.javacup.model.trajectory`

**Physics Model**: Ground friction (quadratic deceleration)

**Formula**:
```
X(t) = vx0 × t - k × t² / 2 + x0   (where k = 4)
```

**Behavior**:
- Ball gradually slows down
- Stops when t = vx0 / k
- Terminal trajectory (no chaining)
- Y(t) always returns 0 (on ground)

### Utility Classes

#### 1. Constants.java
**Package**: `org.javahispano.javacup.model.util`

**Critical Constants** (complete list):

**Field Dimensions** (meters):
- LARGO_CANCHA = 105.0, ANCHO_CANCHA = 68.0
- LARGO_ARCO = 7.32, ALTO_ARCO = 5.0 (×1.2 amplification)
- LARGO_AREA_GRANDE = 40.32, ANCHO_AREA_GRANDE = 16.5
- LARGO_AREA_CHICA = 18.32, ANCHO_AREA_CHICA = 5.5

**Player Movement**:
- VELOCIDAD_MIN = 0.25, VELOCIDAD_MAX = 0.5 (m/iter)
- SPRINT_ACEL = 1.2, SPRINT_ENERGIA_MIN = 0.8, SPRINT_DESGASTE = 0.02
- ACELERACION_INCR = 0.05, ACELERACION_MIN = 0.7

**Kicking**:
- VELOCIDAD_MIN_REMATE = 1.2, VELOCIDAD_MAX_REMATE = 2.4 (m/iter)
- ANGULO_VERTICAL = 30.0, ANGULO_VERTICAL_MAX = 60.0 (degrees)
- DISTANCIA_CONTROL_BALON = 1.0 (meters)
- ALTURA_CONTROL_BALON = 2.0 (meters, 5.0 for goalkeeper)
- ITERACIONES_GOLPEAR_BALON = 30 (cooldown)

**Energy**:
- ENERGIA_MAX = 1.0, ENERGIA_MIN = 0.55
- ENERGIA_DIFF = 0.00001 (passive recovery per iteration)

**Match**:
- ITERACIONES = 3600 (total iterations = 60 seconds × 60 iterations/sec)
- FPS = 20 (rendering frame rate)
- DISTANCIA_SAQUE = 9.15 (kickoff distance)
- TIEMPO_SAQUE_MAX = 150 (max iterations for kickoff)

**Utility Methods**:
```java
public static double getVelocidad(double velocidad)  // Converts [0-1] to [0.25-0.5]
public static double getVelocidadRemate(double remate)  // Converts [0-1] to [1.2-2.4]
```

#### 2. Position.java
**Package**: `org.javahispano.javacup.model.util`

**Fields**: `private double x, y`

**Complete Method List**:
- **Constructors**: Position(), Position(x,y), Position(pos)
- **Movement**: moveAngle(angle, dist), movePosition(target, dist, limit)
- **Distance**: distance(other), norm() [returns x²+y² for speed]
- **Angles**: angle(), getInvertedPosition()
- **Bounds**: isInsideGameField(margin), setInsideGameField()
- **Array ops**: nearestIndex(...), nearestIndexes(...)
- **Static**: Intersection(p1,p2,p3,p4), medium(p1,p2)

**Coordinate System**:
- Origin (0,0) at field center
- X: -52.5 to +52.5, Y: -34 to +34
- Positive X = right, Positive Y = top

#### 3. TacticValidate.java
**Package**: `org.javahispano.javacup.model.util`

**Validates**:
- Team name not empty
- Coach name not empty
- Exactly 11 players
- Exactly one goalkeeper
- Unique player numbers
- Attributes in range [0-1]
- Credit limits not exceeded

### Backend Summary

**Data Flow Architecture**:
```
Match Initialization:
  Partido → loads tactics → TacticaImpl → TacticDetail → JugadorImpl[]

Each Iteration (60 times/second):
  1. Partido updates ball position (trajectory physics)
  2. Partido.set() → GameSituations (populate current state)
  3. GameSituations → Tactic.execute() → List<Command>
  4. Commands → Partido.procesarComandos() → update positions
  5. Partido.calcularRemate() → determine kick → create Trajectory
  6. Partido.detectarGol() → check scoring
  7. Partido.actualizarEnergia() → update player stamina
  8. Loop to step 1

Rendering:
  Viewer → PartidoInterface methods → get state for display
```

**Key Design Patterns**:
- **Interface-based**: Tactic, TacticDetail, PlayerDetail allow custom implementations
- **Command Pattern**: Commands encapsulate player actions
- **Strategy Pattern**: Different trajectory types for different physics
- **Observer Pattern**: PartidoInterface provides read-only view for observers
- **Immutable Data**: Player details, positions copied to prevent cheating

**Migration Considerations**:
- Heavy use of inheritance and interfaces - map to modern patterns
- Mutable state in Partido - consider immutable state + functional updates
- Direct field access - consider getters/setters or properties
- Tight coupling - consider dependency injection
- No async - modern version could parallelize calculations
- Physics calculations are deterministic - preserve exact formulas for compatibility

---

