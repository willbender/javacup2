# Frontend Architecture

The JavaCup 2013 frontend provides a complete visualization and user interface system for the football simulation framework. It consists of a dual-viewer rendering system (2D Basic and 3D OpenGL), a comprehensive GUI for match configuration, and a graphical tactic editor.

## Table of Contents
- [Overview](#overview)
- [Rendering System](#rendering-system)
- [GUI Components](#gui-components)
- [Backend Integration](#backend-integration)
- [Update Loop & Rendering Flow](#update-loop--rendering-flow)
- [Audio System](#audio-system)
- [Event Handling](#event-handling)
- [Camera Controls](#camera-controls)
- [Architecture Summary](#architecture-summary)

---

## Overview

**Package**: `org.javahispano.javacup.render` (visualization) and `org.javahispano.javacup.gui` (user interface)

The frontend is built with:
- **Java Swing** - GUI components and 2D viewer
- **Slick2D/LWJGL** - 3D OpenGL rendering and audio
- **OpenAL** - 3D positional audio via LWJGL

**Key Features**:
- Dual viewer system (2D Swing + 3D OpenGL)
- Real-time match visualization with physics-accurate rendering
- Audio feedback system (crowd noise, kicks, goals, whistles)
- Match configuration and tactic selection UI
- Graphical tactic editor with formation designer
- Match recording and replay capability
- Customizable uniforms and player rendering

---

## Rendering System

### Package Structure

```
org.javahispano.javacup.render/
├── VisorOpenGl.java         # 3D OpenGL viewer (primary)
├── VisorBasico.java         # 2D Swing viewer (lightweight)
├── PintaCancha.java         # Field/stadium renderer
├── PintaJugador.java        # Player sprite renderer
├── PintaJugadorNew.java     # Enhanced player rendering
├── PintaBalon.java          # Ball renderer with animation
├── PintaMarcador.java       # Scoreboard overlay
├── PintaPublicidad.java     # Advertisement overlays
├── Transforma.java          # Coordinate transformation utilities
└── EstiloUniforme.java      # Uniform style enumeration
```

### Core Viewers

#### 1. VisorBasico.java (2D Basic Viewer)
**Lines of Code**: ~618  
**Purpose**: Simple top-down 2D visualization using Java Swing

**Key Fields**:
```java
private static int fps = 20;                    // Frames per second
private static boolean dibujaJugadores = true;  // Draw player sprites
private static boolean dobleBuffer = true;      // Double buffering
private static boolean numeros = true;          // Show jersey numbers
private static boolean sonidos = true;          // Enable audio
private PartidoInterface p;                     // Match engine reference
private Timer timer;                            // Swing timer for updates
private Position[][] pos = new Position[11][2]; // Player positions
private double[][] ang = new double[11][2];     // Player orientations
private int[][] iter = new int[11][2];          // Animation frames
```

**Constructor**:
```java
public VisorBasico(PartidoInterface partido, PrincipalFrame principal)
```
- Initializes images (field, goals, players, ball)
- Loads 14 audio files (ambient, kicks, goals, whistles)
- Creates Swing Timer with interval `920/fps` ms
- Sets up JFrame with field canvas

**Key Methods**:

```java
private void loadImages()
```
- Loads field background (`imgCampo`)
- Loads goal images (`imgArcoSup`, `imgArcoInf`)
- Creates player sprites with team colors
- Loads ball animation frames
- Applies color transformations for uniforms

```java
private void loadSound()
```
- Loads 9 ambient crowd sounds (`audio/0.ogg` to `audio/8.ogg`)
- Loads kick sounds (`remate1.ogg`, `remate2.ogg`)
- Loads goal sounds (`gol.ogg`)
- Loads whistle, rebound, goalpost sounds
- Sets volume levels for ambient and action sounds

```java
public void pinta(Graphics g)
```
Main rendering method called by timer:
1. Clear background
2. Draw field with markings
3. Draw player shadows (offset for 3D effect)
4. Draw players with rotation and animation
5. Draw ball and ball shadow
6. Display score and match time at bottom
7. Show events (goal celebration, offside indicator)

**Rendering Pipeline**:
```
Timer tick (every 920/fps ms)
  ├─ Call p.iterar() to advance match
  ├─ Check events: p.esGol(), p.estanRematando(), etc.
  ├─ Play corresponding audio
  ├─ Update pos[][] from p.getPosiciones()
  ├─ Calculate player angles from position changes
  ├─ Increment animation frames: iter[i] = (iter[i] + 1) % 14
  └─ Call repaint() to trigger pinta(Graphics)
```

**Coordinate System**:
- Field origin at center (0, 0)
- X-axis: -52.5m to +52.5m (horizontal)
- Y-axis: -34m to +34m (vertical)
- Screen coordinates converted via scaling: `screenX = worldX * pixelsPerMeter + centerX`

**Relationships**:
- Uses `PartidoInterface` for match state queries
- Receives updates via Swing Timer
- Renders using standard Java2D Graphics
- Simple and performant for older hardware

---

#### 2. VisorOpenGl.java (3D OpenGL Viewer)
**Lines of Code**: ~875  
**Purpose**: Advanced 3D visualization with Slick2D/LWJGL

**Implements**: `org.newdawn.slick.Game` interface

**Key Fields**:
```java
private static int fps = 20;                      // Target FPS
private static double escala = 15.0;              // Pixels per meter
private static int estadioIdx = 0;                // Stadium variant (0-1)
private static boolean jugador3d = false;         // 3D player models
private static boolean estadio = true;            // Show stadium
private static boolean entorno = true;            // Show environment
private static boolean autoescala = true;         // Auto-adjust zoom
private static boolean marcador = true;           // Show scoreboard
private static int tipoTexto = 0;                 // Player label mode

private PartidoInterface partido;                 // Match engine
private PintaCancha pc;                           // Field renderer
private PintaJugador pjLocal, pjVisita;          // Player renderers
private PintaBalon pb;                            // Ball renderer
private PintaMarcador pm;                         // Scoreboard renderer
private PintaPublicidad pp;                       // Advertisement renderer

private double vx = 0, vy = 0;                    // Camera velocity
private double px = 0, py = 0;                    // Camera position
private double giro = 0;                          // Camera rotation
private double velgiro = 0;                       // Rotation velocity

private int[][] iteraciones = new int[11][2];    // Animation frames
private double[][] angulos = new double[11][2];   // Player rotations
private Position[][] posPrev, posActu;            // Position history

private Sound[] remate = new Sound[2];            // Kick sounds
private Sound gol, silbato, rebote;               // Event sounds
private Sound[] ambiente = new Sound[9];          // Ambient sounds
private Sound[] poste = new Sound[2];             // Goalpost sounds
private Sound[] ovacion = new Sound[2];           // Celebration sounds
```

**Constructor Options**:
```java
// Live match
public VisorOpenGl(Partido partido, int sx, int sy, boolean fullscreen, PrincipalFrame principal)

// Replay mode
public VisorOpenGl(PartidoGuardado pg, int sx, int sy, boolean fullscreen, PrincipalFrame principal)
```

**Key Methods**:

```java
public void init(GameContainer gc)
```
Initialization phase:
- Creates all Pinta* renderer objects
- Loads textures and images via Slick2D
- Loads all audio files
- Checks for uniform color conflicts (warns if similar colors)
- Initializes camera position and parameters
- Sets up OpenGL state

```java
public void update(GameContainer gc, int delta)
```
Called every frame before rendering:
- Advances match: `partido.iterar()`
- Updates camera position based on ball
- Processes keyboard input
- Updates player animation frames
- Handles event timers (goal celebration, offside indicators)
- Plays audio for match events
- Updates rotation angles for players

```java
public void render(GameContainer gc, Graphics g)
```
Main rendering pipeline:
1. **Clear screen** with black background
2. **Draw environment** (if enabled): City/stadium exterior via `pc.pintaEntorno()`
3. **Draw stadium** (if enabled): Stands and structure via `pc.pintaEstadio()`
4. **Draw field**: Playing surface with markings via `pc.pintaCancha()`
5. **Draw goals**: Both goals via `pc.pintaArcos()`
6. **For each team** (local, then visiting):
   - For each of 11 players:
     - Draw player shadow
     - Draw player sprite with rotation and animation
     - Draw player label (number/name based on `tipoTexto`)
7. **Draw ball**: Shadow + ball with altitude-based scaling via `pb.pintaBalon()`
8. **Draw overlays**:
   - Scoreboard (if enabled) via `pm.pinta()`
   - Advertisements via `pp.pintaPublicidad()`
   - Event images (goal, offside, substitution)
9. **Draw UI**: FPS counter, match time, pause indicator

```java
public void keyPressed(int key, char c)
```
Keyboard controls:
- **F1**: Toggle stadium rendering
- **F2**: Toggle environment background
- **F4**: Cycle player text mode (off → nearby numbers → nearby names → all numbers → all names)
- **F5**: Toggle scoreboard
- **F9**: Save screenshot
- **P**: Pause/resume match
- **Arrow Keys**: Rotate camera
- **+/-**: Adjust zoom
- **ESC**: Exit viewer

**Camera System**:
- **Position**: `(px, py)` tracks ball position with smoothing
- **Rotation**: `giro` angle for dynamic viewing (can spin around field)
- **Zoom**: `escala` adjusts pixels per meter (auto-adjusts if `autoescala` enabled)
- **Auto-follow**: Camera centers on ball with configurable offset

**Event Visualization**:
- **Goal**: Displays celebration overlay for 3 seconds (`golImage`)
- **Offside**: Shows offside indicator for 2 seconds (`offSideImage`)
- **Substitution**: Brief change notification (`cambioImage`)
- All events trigger corresponding audio

**Relationships**:
- Implements Slick2D `Game` interface for game loop
- Uses `AppGameContainer` for OpenGL context
- Delegates rendering to specialized Pinta* classes
- Queries `PartidoInterface` each frame for match state
- More feature-rich but requires OpenGL support

---

### Rendering Components (Pinta* Classes)

#### 1. PintaCancha.java (Field Renderer)
**Lines of Code**: ~140  
**Purpose**: Renders playing field, stadium, and environment

**Key Fields**:
```java
private Image pasto;              // Grass texture
private Image arcilla;            // Clay texture (alternate)
private Image arcoSup, arcoInf;   // Goal images
private Image entorno;            // Environment background
private Image estadio;            // Stadium structure
private Image cancha;             // Field markings overlay
private int estadioIdx;           // 0=Nou Camp, 1=Bernabéu
```

**Key Methods**:
```java
public void pintaEntorno(Graphics g, Position pos, double escala)
```
- Draws background environment (city skyline, mountains)
- Scaled and positioned behind stadium
- Optional based on settings

```java
public void pintaEstadio(Graphics g, Position pos, double escala)
```
- Draws stadium structure (stands, roof, architecture)
- Two variants: Nou Camp (0) and Bernabéu (1)
- Positioned to frame the field

```java
public void pintaCancha(Graphics g, Position pos, double escala)
```
- Draws grass/clay surface
- Overlays field markings (center circle, penalty areas, goal boxes, corner arcs)
- Uses `Transforma` for coordinate conversion

```java
public void pintaArcos(Graphics g, Position pos, double escala)
```
- Draws both goals at field ends
- Properly scaled with perspective

**Field Layout**:
- 105m × 68m playing area
- Goal: 7.32m wide × 5.0m tall (amplified 1.2× for visibility)
- Penalty area: 40.32m × 16.5m
- Goal box: 18.32m × 5.5m
- All dimensions from `Constants.java`

---

#### 2. PintaJugador.java (Player Renderer)
**Lines of Code**: ~137  
**Purpose**: Renders players with custom uniforms and animations

**Key Fields**:
```java
private Image[][] img = new Image[7][6];          // Base sprites (7 styles × 6 frames)
private Image[][] imgJug = new Image[11][14];     // Player sprites (11 players × 14 frames)
private Image sombra;                              // Shadow image
private TacticDetail impl;                         // Team configuration
private boolean uniformeAlternativo = false;       // Use alternate kit
```

**Constructor**:
```java
public PintaJugador(TacticDetail impl, boolean uniformeAlternativo)
```
- Loads base sprite images for 7 uniform styles
- Calls `update()` to generate custom player sprites

**Key Methods**:
```java
public void update(boolean uniformeAlternativo)
```
Core customization logic:
1. **For each of 11 players**:
   - Get player details (name, number, colors, isGoalkeeper)
   - Select uniform colors (primary or alternate kit)
   - Select appropriate uniform style (from `EstiloUniforme`)
   
2. **Color replacement algorithm**:
   - Iterate through base sprite pixels
   - Replace template colors with custom colors:
     - **Yellow** (0xFFFF00) → Shirt color
     - **Magenta** (0xFF00FF) → Shorts color
     - **Blue** (0x0000FF) → Socks color
     - **Green** (0x00FF00) → Stripe/line color
     - **White** (0xFFFFFF) → Hair color
   - Apply player-specific skin tone
   - Generate 14 animation frames (7 forward + 7 reverse)

3. **Special handling**:
   - Goalkeepers use separate color scheme
   - Hair color per player from `PlayerDetail.getHairColor()`
   - Skin color per player from `PlayerDetail.getSkinColor()`

**Animation System**:
- 14 frames per player (7 unique + 7 mirrored)
- Frame cycles through walking/running animation
- Frame selection: `imgJug[playerIdx][frameNumber % 14]`
- Rotation applied externally before drawing

**Relationships**:
- Uses `TacticDetail` for team uniform configuration
- Uses `PlayerDetail[]` for individual player colors
- Called by `VisorOpenGl` for each player each frame
- Generates sprites once at initialization, reuses for performance

---

#### 3. PintaBalon.java (Ball Renderer)
**Lines of Code**: ~41  
**Purpose**: Renders animated soccer ball with 3D effect

**Key Fields**:
```java
private Image[] balon = new Image[6];  // 6 rotation frames
private Image sombra;                   // Shadow image
```

**Key Methods**:
```java
public void pintaBalon(int iter, double angulo, double escala, 
                       double x, double y, double z, Graphics g)
```
Rendering logic:
1. Calculate shadow offset based on ball height `z`
2. Draw shadow at ground level (lighter as ball rises)
3. Rotate graphics context by `angulo`
4. Scale ball size: `size = 0.225 × escala × (1 + z/16)` (appears larger when higher)
5. Draw ball frame: `balon[iter % 6]` for rotation animation
6. Restore graphics transform

**Physics Integration**:
- Height `z` from `partido.getAlturaBalon()`
- Position `(x, y)` from `partido.getPosVisibleBalon()`
- Creates 3D illusion through shadow separation and size scaling

---

#### 4. PintaMarcador.java (Scoreboard Renderer)
**Lines of Code**: ~139  
**Purpose**: Displays electronic scoreboard with scores and team names

**Key Fields**:
```java
private String local;          // Local team 3-letter code
private String visita;         // Visiting team 3-letter code
private TrueTypeFont f, f2;    // Font renderers
```

**Constructor**:
```java
public PintaMarcador(String nombreLocal, String nombreVisita)
```
- Extracts 3-letter abbreviations from team names
- Algorithm: Takes first letter + first two consonants, or first 3 letters
- Examples: "Real Madrid" → "RMD", "Barcelona" → "BAR"

**Key Methods**:
```java
public void pinta(Graphics g, int golesLocal, int golesVisita, 
                  int iteracion, int posesion)
```
Renders scoreboard overlay:
- Team abbreviations in yellow
- Scores in large white font
- Match time (calculated from `iteracion`)
- Possession percentage bar
- Semi-transparent background panel

---

#### 5. PintaPublicidad.java (Advertisement Renderer)
**Lines of Code**: ~52  
**Purpose**: Renders advertisement banners around field

**Key Fields**:
```java
private Image pubHorizontal;   // Horizontal ad strip
private Image pubVertical;     // Vertical ad strip
```

**Key Methods**:
```java
public void pintaPublicidad(Graphics g, Position pos, double escala)
```
- Draws perimeter advertisements scaled to field
- Positioned along sidelines and behind goals
- Static images (no animation)

---

#### 6. Transforma.java (Coordinate Transformation)
**Lines of Code**: ~27  
**Purpose**: Converts world coordinates to screen pixels

**Key Methods**:
```java
public static Position transform(Position pos, Position posRel, 
                                  int xPixel, int yPixel, double escala)
```
Formula:
```
screenX = (pos.x - posRel.x) × escala + xPixel
screenY = (pos.y - posRel.y) × escala + yPixel
```
- `pos`: World position to convert
- `posRel`: Reference point (camera position)
- `(xPixel, yPixel)`: Screen center coordinates
- `escala`: Scale factor (pixels per meter)

**Overloaded variants**:
```java
public static Position transform(Position pos, double escala)  // No offset
public static Double transform(Double value, double escala)    // Scalar scaling
```

**Usage Example**:
```java
// Convert ball position to screen coordinates
Position screenPos = Transforma.transform(
    ballPos,           // World position
    cameraPos,         // Camera reference
    screenWidth/2,     // Screen center X
    screenHeight/2,    // Screen center Y
    15.0              // 15 pixels per meter
);
```

---

#### 7. EstiloUniforme.java (Uniform Style Enum)
**Lines of Code**: ~38  
**Purpose**: Defines uniform pattern styles

**Values**:
```java
FRANJA_HORIZONTAL(1)    // Horizontal stripe
FRANJA_VERTICAL(2)      // Vertical stripe
FRANJA_DIAGONAL(3)      // Diagonal stripe
LINEAS_HORIZONTALES(4)  // Horizontal lines (multiple)
LINEAS_VERTICALES(5)    // Vertical lines (multiple)
SIN_ESTILO(6)           // Solid color (no pattern)
```

**Usage**:
- Returned by `TacticDetail.getStyle()` and `getStyle2()`
- Determines which base sprite images to use in `PintaJugador`
- Examples: Barcelona (vertical stripes), Real Madrid (solid white)

---

## GUI Components

### Package Structure

```
org.javahispano.javacup.gui/
├── principal/
│   ├── PrincipalFrame.java      # Main application window
│   └── PrincipalDatos.java      # Configuration persistence
└── asistente/
    ├── AsistenteFrame.java      # Tactic editor GUI
    ├── TacticDetailImpl.java    # Editor's tactic implementation
    ├── JugadorImpl.java         # Editor's player implementation
    └── PoligonosData.java       # Formation polygon data
```

### 1. PrincipalFrame.java (Main Application Window)
**Lines of Code**: ~900+  
**Purpose**: Central hub for application - tactic selection, match launching, configuration

**Key Fields**:
```java
private PrincipalDatos datos;                          // Persistent configuration
private DefaultListModel tacticasModel;                // Available tactics list
private DefaultListModel guardados;                    // Saved matches list
private HashMap<Class, TacticDetail> tactics;          // Tactic metadata cache
private Class tl, tv;                                  // Selected tactics (local/visiting)
private JFileChooser jfc;                              // File chooser dialog
private ArrayList<File> filedirs;                      // Tactic source directories
```

**Constructor**:
```java
public PrincipalFrame()
```
Initialization:
1. Load configuration from `config.xml` via `load()`
2. Scan for available tactics via `scanForTactics()`
3. Build GUI with tabbed interface
4. Populate tactic list
5. Set default selections

**Key Methods**:

```java
private void scanForTactics(List<File> directories)
```
Tactic discovery algorithm:
1. Scan `build/classes` and `bin` directories recursively
2. Load all `.class` files
3. Check if class implements `Tactic` interface
4. Verify not abstract and has public constructor
5. Add to `tacticasModel` for display

```java
private void scanDir(File dir)
```
Recursive directory scanner:
- Discovers all `.class` files
- Converts file paths to package names
- Uses reflection to load and validate classes
- Caches `TacticDetail` to avoid repeated instantiation

```java
private void jButton1ActionPerformed(ActionEvent evt)
```
"Ver Partido" (View Match) button handler:
1. Validate tactic selections (`tl != null && tv != null`)
2. Create `Partido` instance with selected tactics
3. Check if pre-execution requested (`jCheckBox12`)
   - If yes: Run match to completion, wrap in `PartidoGuardado`
4. Determine viewer type from `datos.visor` (0=Basic, 1=OpenGL)
5. Create and launch selected viewer
6. Hide `PrincipalFrame`, transfer focus to viewer

```java
private void load()
```
Configuration loading:
- Deserialize `config.xml` using XStream
- Populates `datos` object
- Handles missing/corrupted files with defaults

```java
private void save()
```
Configuration saving:
- Serializes `datos` to `config.xml`
- Preserves all settings across sessions

```java
private void fromDatos() / private void toDatos()
```
Synchronization between UI and data model:
- `fromDatos()`: Updates UI components from `datos` fields
- `toDatos()`: Captures UI state into `datos` fields

**UI Tabs**:

**Tab 1: Tácticas (Tactics)**
- List of available tactics with icons
- Buttons: "Táctica Local", "Táctica Visita", "Local ↔ Visita" (swap)
- Button: "Ver Partido" (launch match)
- ComboBox: Viewer selection (Basic/OpenGL)

**Tab 2: Guardados (Saved Matches)**
- List of recorded match files
- Buttons: "Agregar partido" (add), "Quitar partido" (remove), "Ver Partido" (replay)
- File chooser for match selection

**Tab 3: Configuración (Configuration)**
Nested tabs:
- **General**: FPS, audio volumes, save prompts
- **Visor Básico**: Double buffer, sprites, numbers
- **Visor OpenGL**: Resolution, fullscreen, scale, stadium, environment, scoreboard, text mode

**Match Execution Flow**:
```
User selects tactics → "Ver Partido" button
  ├─ Create Partido(tl, tv, save=true)
  ├─ Optional: Pre-execute match
  │   └─ while (p.getEstado() != 7) p.iterar()
  ├─ Wrap in PartidoGuardado if saving
  ├─ Create viewer:
  │   ├─ VisorBasico(partido, this)
  │   └─ VisorOpenGl(partido, sx, sy, fullscreen, this)
  └─ Hide PrincipalFrame, show viewer
```

**Relationships**:
- Uses `PrincipalDatos` for persistence
- Creates `Partido` from selected `Tactic` classes
- Launches `VisorBasico` or `VisorOpenGl` based on configuration
- Manages saved match files via `PartidoGuardado`

---

### 2. PrincipalDatos.java (Configuration Data Model)
**Lines of Code**: ~132  
**Purpose**: Serializable configuration object for persistence

**All Fields**:
```java
private File dir;                         // Working directory
private int sx = 800, sy = 600;           // Window resolution
private boolean fullscreen = false;       // Fullscreen mode
private boolean maximizado = false;       // Window maximized
private boolean marcador = true;          // Show scoreboard
private boolean entrada = true;           // Input enabled
private boolean guardar = false;          // Auto-save matches
private boolean entorno = true;           // Show environment
private boolean estadio = true;           // Show stadium
private boolean autoescala = true;        // Auto-adjust scale
private boolean numeros = true;           // Show player numbers
private boolean sonidos = true;           // Audio enabled
private boolean jugadores3d = false;      // 3D player models
private boolean buffer = true;            // Pre-render buffer
private int estadioIdx = 0;               // Stadium variant (0-1)
private double escala = 15.0;             // Pixels per meter
private Rectangle ubicacion;              // Window bounds
private int splitpos = 0;                 // Splitter position
private int tab = 0;                      // Active tab index
private int visor = 1;                    // 0=Basic, 1=OpenGL
private float volumenAmbiente = 0.5f;     // Ambient volume [0-1]
private float volumenCancha = 0.5f;       // Match sounds volume [0-1]
private boolean dibujaJugadores = true;   // Draw player sprites
private boolean dobleBuffer = true;       // 2D double buffering
private int fps = 20;                     // Target FPS
private int tipoTexto = 0;                // Player label mode
private String local;                     // Selected local tactic class
private String visita;                    // Selected visiting tactic class
private ArrayList<URL> guardados;         // Saved match URLs
```

**Serialization**:
- Uses XStream library for XML serialization
- Saved to `config.xml` in project root
- Loaded on application startup
- Saved on application shutdown or manual save

**XML Structure Example**:
```xml
<org.javahispano.javacup.gui.principal.PrincipalDatos>
  <sx>800</sx>
  <sy>600</sy>
  <fullscreen>false</fullscreen>
  <visor>1</visor>
  <fps>20</fps>
  <escala>15.0</escala>
  <local>org.javahispano.javacup.tacticas.Example</local>
  <visita>org.javahispano.javacup.tacticas.Another</visita>
  ...
</org.javahispano.javacup.gui.principal.PrincipalDatos>
```

---

### 3. AsistenteFrame.java (Tactic Editor)
**Lines of Code**: ~1200+  
**Purpose**: Visual editor for creating and configuring tactics

**Key Features**:

**1. Team Configuration Tab**
- Team name input
- Country selection
- Coach name
- Uniform designer:
  - Primary kit colors (shirt, shorts, socks, stripe, goalkeeper)
  - Alternate kit colors (same structure)
  - Style selection (horizontal/vertical/diagonal stripes, lines, solid)
  - Real-time preview via `PintaJugador`

**2. Player Configuration Tab**
- Table with 11 rows (one per player)
- Columns:
  - Name (text input)
  - Number (1-99, validated for uniqueness)
  - Is Goalkeeper (checkbox, exactly one required)
  - Skin color (color picker)
  - Hair color (color picker)
  - Speed slider [0-1]
  - Power slider [0-1]
  - Precision slider [0-1]
- Credit system: Total credits limited, distributed among attributes
- Validation: Ensures single goalkeeper, no duplicate numbers

**3. Formation Editor Tab**
- Visual field representation
- Drag-and-drop player positioning
- Formation templates:
  - 4-4-2, 4-3-3, 3-5-2, etc.
- Three formation types:
  - **Start positions** (when team kicks off after conceding)
  - **No-start positions** (when opponent kicks off after scoring)
  - **In-game formations** (custom tactical setups)
- Kick simulator:
  - Power slider [0-1]
  - Angle input [0-360°]
  - Vertical angle [0-60°]
  - Ball trajectory visualization using physics
  - Shows where kick will land

**Key Fields**:
```java
private TacticDetailImpl impl;          // Tactic being edited
private PintaJugador jp;                 // Uniform preview renderer
private CanvasGameContainer cgc;         // Slick2D canvas for rendering
private XStream xs;                      // XML serializer
private EstiloUniforme[] estilos;        // Available uniform styles
```

**Key Methods**:

```java
private void updatePreview()
```
- Regenerates player sprites with current uniform settings
- Updates preview canvas in real-time
- Shows how team will appear in match

```java
private void generateCode()
```
Code generation:
1. Creates complete `TacticDetail` implementation
2. Generates player array initialization
3. Generates formation arrays (`getStartPositions`, `getNoStartPositions`)
4. Formats as compilable Java source code
5. Copies to clipboard or saves to file

```java
private void validateTactic()
```
Validation checks:
- Team name not empty
- Coach name not empty
- Exactly 11 players
- Exactly one goalkeeper
- All player numbers unique
- Attributes in range [0-1]
- Total credits not exceeded

```java
private void simulateKick()
```
Kick simulation:
- Uses actual physics from `AirTrajectory` and `FloorTrajectory`
- Visualizes ball path on field
- Shows landing position
- Color-codes by height (ground=blue, low=green, medium=yellow, high=red)
- Helps design offensive plays

**Menu Options**:
- **File**: New, Open, Save, Save As
- **Generate**: 
  - Complete tactic class (Tactic + TacticDetail + formations)
  - Tactic class only
  - TacticDetail class only
  - Formation code only
- **Validate**: Check for errors

**Code Generation Example**:
```java
public class MyTactic implements Tactic {
    public TacticDetail getDetail() {
        return new TacticDetailImpl(
            "Team Name",
            "Country", 
            "Coach",
            Color.RED,  // shirt
            Color.BLUE, // shorts
            ...
            new PlayerDetail[] {
                new JugadorImpl("Player 1", 1, false, Color.WHITE, Color.BLACK, 0.8, 0.7, 0.9),
                ...
            }
        );
    }
    
    public Position[] getStartPositions(GameSituations sp) {
        return new Position[] {
            new Position(-45, 0),  // Goalkeeper
            new Position(-25, -20), // Defender
            ...
        };
    }
    
    // ... execute() implementation
}
```

**Relationships**:
- Creates `TacticDetailImpl` and `JugadorImpl` instances
- Uses `PintaJugador` for uniform preview
- Integrates physics classes for kick simulation
- Exports code compatible with competition framework

---

## Backend Integration

### PartidoInterface.java Connection

Both viewers query the match engine through `PartidoInterface`:

**State Queries** (called every frame):
```java
int getIteracion()                        // Current iteration (0-3600)
int getGolesLocal(), getGolesVisita()     // Scores
Position[][] getPosiciones()              // 2×11 player positions
Position getPosVisibleBalon()             // Ball position (2D projection)
double getAlturaBalon()                   // Ball height (meters)
double getPosesionBalonLocal()            // Possession ratio [0-1]
TacticDetail getDetalleLocal()            // Local team config
TacticDetail getDetalleVisita()           // Visiting team config
```

**Event Queries** (boolean flags, reset each frame):
```java
boolean esGol()                           // Goal scored
boolean estanRematando()                  // Player kicking
boolean estaRebotando()                   // Ball bouncing
boolean estanSilbando()                   // Whistle blown
boolean estanOvacionando()                // Crowd celebrating
boolean esPoste()                         // Hit goalpost
boolean isOffSide()                       // Offside violation
boolean isLibreIndirecto()                // Indirect free kick
boolean estanSacando()                    // Taking kickoff
```

**State Mutation**:
```java
void iterar()                             // Advance simulation 1 frame
```

**Data Flow**:
```
PartidoInterface (Backend)
     ↓ (queries)
VisorBasico / VisorOpenGl
     ↓ (rendering)
Screen Display
```

**One-way Communication**:
- Viewers only READ state from backend
- Backend advances independently via `iterar()`
- No viewer actions affect match outcome (deterministic simulation)
- Ensures fair competition (no cheating via viewer manipulation)

---

## Update Loop & Rendering Flow

### VisorBasico (2D) Update Cycle

**Timing**: Swing Timer with interval `920 / fps` milliseconds

```
Timer fires
  ├─ partido.iterar()                    // Advance match 1 frame
  ├─ Check events:
  │   ├─ if (p.esGol()) → play gol.ogg, show celebration (3 sec)
  │   ├─ if (p.estanRematando()) → play remate1/2.ogg
  │   ├─ if (p.esPoste()) → play poste1/2.ogg + ovacion
  │   ├─ if (p.isOffSide()) → play silbato, show offside (2 sec)
  │   ├─ if (p.estanSilbando()) → play silbato
  │   └─ if (p.estaRebotando()) → play rebote.ogg
  ├─ Update positions:
  │   └─ pos[][] = p.getPosiciones()
  ├─ Calculate orientations:
  │   └─ ang[i][j] = atan2(pos[i][j] - posPrev[i][j])
  ├─ Advance animations:
  │   └─ iter[i][j] = (iter[i][j] + 1) % 14
  └─ jPanel1.repaint()                   // Trigger paintComponent()
      └─ pinta(Graphics g)               // Render frame
          ├─ Draw field background
          ├─ Draw goals
          ├─ For each player:
          │   ├─ Draw shadow
          │   └─ Draw player sprite (rotated, animated)
          ├─ Draw ball shadow
          ├─ Draw ball
          ├─ Draw scoreboard (bottom panel)
          └─ Draw event overlays (goal, offside)
```

**Frame Rate**: 
- Target: 20 FPS (configurable)
- Actual: `1000 / (920/fps)` = ~21.7 FPS at fps=20
- Match speed: 60 iterations/second in simulation, displayed at viewer FPS

---

### VisorOpenGl (3D) Game Loop

**Timing**: Slick2D fixed timestep game loop

```
Slick2D Game Loop (every ~50ms for 20 FPS)
  ├─ Input handling
  │   ├─ Keyboard: Arrow keys, F-keys, +/-, P, ESC
  │   └─ Mouse: Camera pan (if enabled)
  │
  ├─ update(GameContainer gc, int delta)
  │   ├─ partido.iterar()                    // Advance simulation
  │   ├─ Event detection & audio:
  │   │   ├─ if (partido.esGol()) → golIter = 60, play gol.ogg
  │   │   ├─ if (partido.estanRematando()) → play remate[random].ogg
  │   │   ├─ if (partido.esPoste()) → play poste + ovacion
  │   │   ├─ if (partido.isOffSide()) → offSideIter = 40, play silbato
  │   │   └─ if (partido.estaRebotando()) → play rebote.ogg
  │   ├─ Camera update:
  │   │   ├─ px = ballPos.x (smooth follow)
  │   │   ├─ py = ballPos.y
  │   │   ├─ giro += velgiro (rotation)
  │   │   └─ if (autoescala) escala = calcAutoScale()
  │   ├─ Animation update:
  │   │   ├─ iteraciones[i][j]++ (player animation frames)
  │   │   └─ angulos[i][j] = atan2(velocity) (player orientation)
  │   ├─ Event timers:
  │   │   ├─ golIter-- (goal celebration countdown)
  │   │   └─ offSideIter-- (offside indicator countdown)
  │   └─ Ambient sound (every ~200 iterations):
  │       └─ play ambiente[random].ogg
  │
  └─ render(GameContainer gc, Graphics g)
      ├─ Clear screen (black)
      ├─ Setup viewport and transforms
      ├─ if (entorno) pc.pintaEntorno()      // Background
      ├─ if (estadio) pc.pintaEstadio()      // Stadium
      ├─ pc.pintaCancha()                     // Field
      ├─ pc.pintaArcos()                      // Goals
      ├─ For local team (11 players):
      │   ├─ Draw shadow at pos[i][0]
      │   └─ pjLocal.pinta(player, frame, angle, pos)
      ├─ For visiting team (11 players):
      │   ├─ Draw shadow at pos[i][1]
      │   └─ pjVisita.pinta(player, frame, angle, pos)
      ├─ Draw ball:
      │   ├─ Shadow at ground level
      │   └─ pb.pintaBalon(frame, angle, z)
      ├─ if (marcador) pm.pinta(scores, time, possession)
      ├─ pp.pintaPublicidad()                 // Ads
      ├─ Draw overlays:
      │   ├─ if (golIter > 0) draw golImage
      │   └─ if (offSideIter > 0) draw offSideImage
      ├─ Draw player labels (if tipoTexto > 0):
      │   └─ For nearby/all players: draw number/name
      ├─ Draw UI:
      │   ├─ FPS counter (top-left)
      │   ├─ Match time (top-center)
      │   └─ Pause indicator (if paused)
      └─ Swap buffers
```

**Performance**:
- OpenGL hardware acceleration
- Texture caching via Slick2D
- Batch rendering for efficiency
- Target: 20 FPS, typical: 40-60 FPS on modern hardware

---

## Audio System

### Sound Resources

**Formats**: OGG Vorbis (cross-platform via OpenAL)

**Categories**:
```
recursos/audio/
├── 0.ogg to 8.ogg         # 9 ambient crowd sounds (looping)
├── remate1.ogg, remate2.ogg   # Kick sounds
├── gol.ogg                # Goal celebration
├── silbato.ogg            # Whistle (kickoff, fouls)
├── rebote.ogg             # Ball bounce
├── poste1.ogg, poste2.ogg     # Goalpost hit
└── ovacion1.ogg, ovacion2.ogg # Crowd cheering
```

### Volume Control

**Two Independent Channels**:
```java
float volumenAmbiente = 0.5f;    // Background crowd noise [0-1]
float volumenCancha = 1.0f;      // Match action sounds [0-1]
```

**Dynamic Adjustment**:
- Ambient sounds loop continuously at `volumenAmbiente`
- Action sounds (kicks, goals) play at `volumenCancha`
- Pitch variation: `pitch = 0.95 + random(0.1)` for natural variation

### Audio Triggering

**Event-Driven Playback**:
```java
if (partido.esGol()) {
    gol.play(1.0f, volumenCancha);
    ovacion[random(2)].play(1.0f, volumenCancha);
}

if (partido.estanRematando()) {
    remate[random(2)].play(pitch(), volumenCancha);
}

if (partido.esPoste()) {
    poste[random(2)].play(1.0f, volumenCancha);
    ovacion[random(2)].play(1.0f, volumenCancha);
}
```

**Ambient Management**:
- Plays random crowd sound every ~200 iterations (10 seconds)
- Creates atmospheric background without repetition
- Separate volume control from action sounds

**Relationships**:
- Sounds loaded via Slick2D `Sound` class (wraps OpenAL)
- Triggered by `PartidoInterface` event flags
- Volume settings from `PrincipalDatos` configuration

---

## Event Handling

### Match Events

Complete event system with visual and audio feedback:

| Event | Backend Flag | Audio | Visual Effect | Duration |
|-------|-------------|-------|---------------|----------|
| **Goal** | `esGol()` | gol.ogg + ovacion | "GOL!" overlay | 3 seconds |
| **Kick** | `estanRematando()` | remate1/2.ogg | Player animation | 1 frame |
| **Goalpost** | `esPoste()` | poste + ovacion | Spark effect | 1 second |
| **Offside** | `isOffSide()` | silbato.ogg | "Offside" indicator | 2 seconds |
| **Whistle** | `estanSilbando()` | silbato.ogg | None | - |
| **Bounce** | `estaRebotando()` | rebote.ogg | None | - |
| **Celebration** | `estanOvacionando()` | ovacion1/2.ogg | Crowd animation | - |
| **Kickoff** | `estanSacando()` | silbato.ogg | Player positioning | Variable |

### Event Visualization Timers

**VisorOpenGl** event counters:
```java
private int golIter = 0;           // Goal celebration countdown (60 frames = 3 sec)
private int offSideIter = 0;       // Offside indicator countdown (40 frames = 2 sec)
private int iterSaca = 0;          // Kickoff positioning timer
private int saqueIter = 0;         // Throw-in animation timer
```

**Update Logic**:
```java
if (partido.esGol()) {
    golIter = 60;                  // Set 3-second timer
    play(gol);
}

if (golIter > 0) {
    drawImage(golImage);           // Show "GOL!" overlay
    golIter--;                     // Countdown
}
```

### User Input Events

**Keyboard Controls** (VisorOpenGl):
```java
public void keyPressed(int key, char c) {
    switch(key) {
        case Input.KEY_F1:
            estadio = !estadio;    // Toggle stadium
            break;
        case Input.KEY_F2:
            entorno = !entorno;    // Toggle environment
            break;
        case Input.KEY_F4:
            tipoTexto = (tipoTexto + 1) % 5;  // Cycle text mode
            break;
        case Input.KEY_F5:
            marcador = !marcador;  // Toggle scoreboard
            break;
        case Input.KEY_P:
            pause = !pause;        // Pause/resume
            break;
        case Input.KEY_ESCAPE:
            gc.exit();             // Quit viewer
            break;
    }
}
```

**Mouse Controls**:
- Scroll wheel: Adjust zoom (`escala`)
- Arrow keys: Rotate camera (`giro`)
- No direct match interaction (read-only viewer)

---

## Camera Controls

### OpenGL Camera System

**Camera Parameters**:
```java
private double px = 0, py = 0;     // Camera world position
private double vx = 0, vy = 0;     // Camera velocity (for smoothing)
private double giro = 0;           // Rotation angle (radians)
private double velgiro = 0;        // Rotation velocity
private double escala = 15.0;      // Zoom level (pixels/meter)
```

**Camera Modes**:

**1. Follow Mode** (default):
- Camera tracks ball position with smoothing
- Update logic:
  ```java
  Position ballPos = partido.getPosVisibleBalon();
  px += (ballPos.x - px) * 0.1;  // Smooth follow (10% per frame)
  py += (ballPos.y - py) * 0.1;
  ```

**2. Manual Mode** (arrow keys):
- User controls rotation
- Left/Right arrows adjust `velgiro`
- `giro` accumulates rotation over time
- Creates orbital camera effect

**3. Auto-Scale Mode** (`autoescala = true`):
- Dynamically adjusts zoom to fit action
- Zooms in when ball near goal
- Zooms out for long passes
- Logic:
  ```java
  double distToGoal = ballPos.distance(nearestGoal);
  escala = 15.0 + (30.0 - distToGoal) * 0.5;  // Closer = more zoom
  ```

### Zoom Control

**Keyboard**:
- `+` / `=`: Zoom in (increase `escala`)
- `-`: Zoom out (decrease `escala`)
- Range: 5.0 to 30.0 pixels/meter

**Auto-adjustment**:
- When `autoescala = true`, keyboard zoom temporarily overrides
- Auto-adjustment resumes after 5 seconds of inactivity

### Coordinate Transformation

**World to Screen**:
```java
Position screenPos = Transforma.transform(
    worldPos,      // Player/ball position in meters
    cameraPos,     // Camera position (px, py)
    screenW/2,     // Screen center X
    screenH/2,     // Screen center Y
    escala         // Zoom factor
);
```

**Rotation**:
```java
g.rotate(giro, screenCenterX, screenCenterY);
// Draw field rotated
g.rotate(-giro, screenCenterX, screenCenterY);  // Restore
```

**Perspective Effect**:
- Ball size scales with height: `size = baseSize × (1 + z/16)`
- Shadow opacity decreases with height: `alpha = 1.0 - z/20`
- Creates 3D illusion in 2D rendering

---

## Architecture Summary

### Component Relationships

```
┌─────────────────────────────────────────────────────────────┐
│                     PrincipalFrame                          │
│  (Main Window - Configuration & Tactic Selection)          │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  ├─ PrincipalDatos (config.xml persistence)
                  │
                  ├─ Partido (match engine, implements PartidoInterface)
                  │
                  └─ [Launch Viewer] ─────┐
                                          │
        ┌─────────────────────────────────┴─────────────────────────┐
        │                                                             │
        v                                                             v
┌───────────────────┐                                    ┌──────────────────────┐
│   VisorBasico     │                                    │   VisorOpenGl        │
│   (2D Viewer)     │                                    │   (3D Viewer)        │
├───────────────────┤                                    ├──────────────────────┤
│ - Swing Timer     │                                    │ - Slick2D Game       │
│ - Graphics2D      │                                    │ - OpenGL rendering   │
│ - Simple sprites  │                                    │ - Camera system      │
└───────┬───────────┘                                    └────────┬─────────────┘
        │                                                          │
        │  queries PartidoInterface                               │  queries PartidoInterface
        │  every frame                                            │  every frame
        v                                                          v
┌───────────────────────────────────────────────────────────────────────────┐
│                         PartidoInterface                                  │
│  (Read-only view of match state)                                         │
├───────────────────────────────────────────────────────────────────────────┤
│ - getPosiciones() → player positions                                     │
│ - getPosVisibleBalon() → ball position                                   │
│ - getGolesLocal/Visita() → scores                                        │
│ - esGol(), estanRematando(), etc. → event flags                         │
│ - iterar() → advance simulation                                          │
└───────────────────────────────────────────────────────────────────────────┘
        │
        │  implemented by
        v
┌───────────────────────────────────────────────────────────────────────────┐
│                           Partido                                         │
│  (Match simulation engine - see BACKEND.md)                              │
└───────────────────────────────────────────────────────────────────────────┘
```

### Rendering Components (OpenGL)

```
VisorOpenGl
    │
    ├─ PintaCancha (field, stadium, environment)
    │   ├─ pintaEntorno() → background scenery
    │   ├─ pintaEstadio() → stadium structure
    │   ├─ pintaCancha() → playing surface + markings
    │   └─ pintaArcos() → goals
    │
    ├─ PintaJugador (×2: local & visiting teams)
    │   ├─ update() → regenerate sprites with team colors
    │   └─ pinta() → render single player (called 11× per team)
    │
    ├─ PintaBalon
    │   └─ pintaBalon() → animated ball with shadow
    │
    ├─ PintaMarcador
    │   └─ pinta() → scoreboard overlay
    │
    ├─ PintaPublicidad
    │   └─ pintaPublicidad() → perimeter advertisements
    │
    └─ Audio System
        ├─ ambiente[9] → crowd noise (looping)
        ├─ remate[2] → kick sounds
        ├─ gol → goal celebration
        ├─ silbato → whistle
        ├─ rebote → bounce
        ├─ poste[2] → goalpost hit
        └─ ovacion[2] → cheering
```

### Data Flow

```
Match Execution:
  PrincipalFrame → select tactics → create Partido
  
Rendering Loop (every frame):
  Viewer.update()
    ├─ partido.iterar()                    (advance simulation)
    ├─ Check event flags                   (esGol, estanRematando, etc.)
    ├─ Play audio for events
    ├─ Query positions: getPosiciones()
    ├─ Update camera position
    └─ Advance animation frames
  
  Viewer.render()
    ├─ Clear screen
    ├─ Transform to camera coordinates
    ├─ Draw background layers (environment → stadium → field)
    ├─ Draw players (local team, then visiting team)
    ├─ Draw ball
    ├─ Draw overlays (scoreboard, events, UI)
    └─ Display buffer
```

### Configuration Flow

```
Startup:
  PrincipalFrame.load() → XStream deserialize config.xml → PrincipalDatos
  
User Configuration:
  PrincipalFrame UI → modify settings → PrincipalDatos
  
Apply to Viewers:
  Before launching viewer:
    VisorBasico/OpenGl.setStatic(datos.field)  (copy to static fields)
  
Shutdown:
  PrincipalFrame.save() → XStream serialize PrincipalDatos → config.xml
```

### Key Design Principles

**1. Separation of Concerns**:
- **Backend** (Partido): Pure simulation, no rendering code
- **Frontend** (Viewers): Pure rendering, no game logic
- **Interface** (PartidoInterface): Clean contract between layers

**2. Dual Viewer Pattern**:
- Common interface allows viewer swapping
- VisorBasico: Lightweight, accessible
- VisorOpenGl: Feature-rich, hardware accelerated
- Both query same PartidoInterface

**3. Component-Based Rendering**:
- Pinta* classes encapsulate rendering logic
- Reusable across viewers
- Independent styling and animation

**4. Event-Driven Audio**:
- Match events trigger sounds
- No polling required
- Natural integration with game loop

**5. Configuration Persistence**:
- XStream XML serialization
- All settings preserved across sessions
- Easy manual editing if needed

**6. Deterministic Replay**:
- PartidoGuardado stores complete match history
- Exact reproduction of matches
- No randomness in replay (uses saved random seeds)

### Migration Considerations

**To Modern Technologies**:
- Replace Slick2D/LWJGL with modern OpenGL (LWJGL3) or Vulkan
- Replace Swing with JavaFX or web-based UI (Electron, React)
- Replace XStream with JSON (Jackson, Gson)
- Consider WebGL for cross-platform rendering
- Extract audio system for separate module (consider OpenAL-soft)
- Modernize file I/O (use NIO.2)
- Consider reactive architecture (RxJava, Project Reactor)
- Separate viewer as standalone module from match engine

**Preserve**:
- PartidoInterface contract (critical for compatibility)
- Rendering component structure (Pinta* pattern)
- Event-driven audio system
- Configuration data model
- Dual viewer concept (lightweight + feature-rich)

**Performance Improvements**:
- Multi-threaded rendering (separate game thread from render thread)
- Shader-based rendering (GPU acceleration)
- Texture atlasing (reduce draw calls)
- Instanced rendering for players (batch 22 players)
- Asset streaming (lazy load textures/sounds)

---

This comprehensive frontend documentation captures all architectural details needed for migrating the JavaCup 2013 visualization system to modern technologies while preserving its core functionality and design patterns.
