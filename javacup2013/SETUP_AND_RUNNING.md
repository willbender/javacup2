# Setup and Running Instructions

This guide provides step-by-step instructions to set up, compile, and run JavaCup 2013 on Windows with Java 21.

## Table of Contents
- [Prerequisites](#prerequisites)
- [System Requirements](#system-requirements)
- [Installation Steps](#installation-steps)
- [Running the Application](#running-the-application)
- [Creating and Testing Tactics](#creating-and-testing-tactics)
- [Running Matches](#running-matches)
- [Troubleshooting](#troubleshooting)
- [Advanced Configuration](#advanced-configuration)

---

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 21**
   - Download from: https://www.oracle.com/java/technologies/downloads/#java21
   - Or use OpenJDK: https://adoptium.net/
   - **Important**: While JavaCup 2013 was originally built for Java 6, it is compatible with Java 21

2. **Git** (to clone the repository)
   - Download from: https://git-scm.com/download/win
   - Or download as ZIP from GitHub

3. **IDE (Optional but Recommended)**
   - **Eclipse**: https://www.eclipse.org/downloads/
   - **IntelliJ IDEA**: https://www.jetbrains.com/idea/download/
   - **VS Code** with Java extension pack: https://code.visualstudio.com/

### System Requirements

**Minimum Requirements**:
- **OS**: Windows 7 or later (Windows 10/11 recommended)
- **CPU**: Dual-core processor, 1.5 GHz or faster
- **RAM**: 2 GB minimum, 4 GB recommended
- **Graphics**: OpenGL 2.0 compatible graphics card (for 3D viewer)
- **Disk Space**: 500 MB for project and dependencies
- **Display**: 800×600 minimum resolution, 1280×720 recommended

**Recommended for Optimal Performance**:
- **OS**: Windows 10/11 64-bit
- **CPU**: Quad-core processor, 2.5 GHz or faster
- **RAM**: 8 GB or more
- **Graphics**: Dedicated GPU with OpenGL 3.0+ support
- **Display**: 1920×1080 or higher

---

## Installation Steps

### Step 1: Install Java 21

1. **Download JDK 21**:
   - Visit https://www.oracle.com/java/technologies/downloads/#java21
   - Download "Windows x64 Installer" (e.g., `jdk-21_windows-x64_bin.exe`)

2. **Install JDK**:
   - Run the installer
   - Accept license agreement
   - Choose installation directory (default: `C:\Program Files\Java\jdk-21`)
   - Click "Install"

3. **Set JAVA_HOME Environment Variable**:
   ```batch
   # Open Command Prompt as Administrator and run:
   setx JAVA_HOME "C:\Program Files\Java\jdk-21" /M
   setx PATH "%PATH%;%JAVA_HOME%\bin" /M
   ```

   Or manually:
   - Right-click "This PC" → Properties → Advanced system settings
   - Click "Environment Variables"
   - Under "System variables", click "New"
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-21` (adjust if different)
   - Edit "Path" variable, add: `%JAVA_HOME%\bin`

4. **Verify Installation**:
   ```batch
   # Open new Command Prompt and run:
   java -version
   javac -version
   ```
   
   Expected output:
   ```
   java version "21" (or "21.0.x")
   javac 21 (or 21.0.x)
   ```

### Step 2: Clone or Download the Project

**Option A: Using Git** (recommended)
```batch
# Open Command Prompt
cd C:\
git clone https://github.com/willbender/javacup2.git
cd javacup2\javacup2013
```

**Option B: Download ZIP**
1. Visit https://github.com/willbender/javacup2
2. Click "Code" → "Download ZIP"
3. Extract to `C:\javacup2\`
4. Navigate to `C:\javacup2\javacup2013`

### Step 3: Verify Project Structure

Check that your directory structure looks like this:
```
javacup2013/
├── src/                    # Source code
│   ├── JavaCup.java       # Main entry point
│   ├── Torneo.java        # Tournament runner
│   └── org/javahispano/javacup/
├── libs/                   # External JAR libraries
│   ├── slick.jar          # Slick2D game library
│   ├── lwjgl.jar          # OpenGL bindings
│   ├── JTattoo.jar        # UI theme
│   └── ... (other JARs)
├── recursos/               # Resources (images, sounds)
├── tacticas/               # Example tactics
├── bin/                    # Compiled classes (created during build)
├── config.xml              # Configuration file
├── *.dll / *.so / *.jnilib # Native libraries for LWJGL
├── README.md
├── BACKEND.md
└── FRONTEND.md
```

---

## Compiling the Project

### Method 1: Using Command Line (javac)

1. **Open Command Prompt** in the `javacup2013` directory:
   ```batch
   cd C:\javacup2\javacup2013
   ```

2. **Create output directory**:
   ```batch
   mkdir bin
   ```

3. **Compile the project**:
   ```batch
   javac -d bin -cp "libs/*;src;recursos;tacticas" src/JavaCup.java
   ```

   This will compile all dependencies automatically.

4. **Verify compilation**:
   ```batch
   dir bin\org\javahispano\javacup
   ```
   You should see compiled `.class` files.

### Method 2: Using Eclipse IDE

1. **Open Eclipse**
2. **Import Project**:
   - File → Open Projects from File System
   - Directory: Browse to `C:\javacup2\javacup2013`
   - Click "Finish"

3. **Fix Build Path** (if needed):
   - Right-click project → Build Path → Configure Build Path
   - Libraries tab → Verify JRE System Library is Java 21
   - Add External JARs → Browse to `libs/` and add all JAR files

4. **Build Project**:
   - Project → Clean → Clean all projects → OK
   - Project → Build Automatically (should be checked)

### Method 3: Using IntelliJ IDEA

1. **Open IntelliJ IDEA**
2. **Open Project**:
   - File → Open → Browse to `C:\javacup2\javacup2013`
3. **Configure SDK**:
   - File → Project Structure → Project
   - SDK: Select Java 21 (or download if not available)
   - Language level: 21
4. **Add Libraries**:
   - File → Project Structure → Libraries
   - Click "+" → Java
   - Browse to `libs/` folder → Select all JAR files → OK
5. **Build Project**:
   - Build → Build Project

---

## Running the Application

### Method 1: Command Line Execution

From the `javacup2013` directory:

```batch
# Run the main application (GUI)
java -cp "bin;libs/*;recursos;tacticas" JavaCup

# Alternative: Run a tournament directly (headless)
java -cp "bin;libs/*;recursos;tacticas" Torneo
```

**Important**: Native libraries (DLL files) must be in the same directory where you run the command.

### Method 2: Eclipse IDE

1. **Run Main Class**:
   - Right-click `src/JavaCup.java`
   - Run As → Java Application

2. **Configure Run Arguments** (optional):
   - Run → Run Configurations
   - Main class: `JavaCup`
   - Arguments tab: Add any JVM arguments if needed

### Method 3: IntelliJ IDEA

1. **Run Main Class**:
   - Right-click `src/JavaCup.java`
   - Run 'JavaCup.main()'

2. **Edit Configuration** (if needed):
   - Run → Edit Configurations
   - Main class: `JavaCup`
   - VM options: Add `-Djava.library.path=.` if native library errors occur

### Method 4: Create Executable JAR (Advanced)

Create a runnable JAR file:

```batch
# From javacup2013 directory
cd bin

# Create manifest file
echo Main-Class: JavaCup > manifest.txt
echo Class-Path: ../libs/slick.jar ../libs/lwjgl.jar ../libs/JTattoo.jar ../libs/slf4j-api-1.6.4.jar ../libs/slf4j-simple-1.6.4.jar ../libs/xstream-1.3.jar ../libs/xpp3_min-1.1.4c.jar ../libs/jogg-0.0.7.jar ../libs/jorbis-0.0.15.jar ../libs/jinput.jar ../libs/lwjgl_util.jar ../libs/swing-layout-1.0.3.jar ../libs/AbsoluteLayout.jar >> manifest.txt

# Create JAR
jar cvfm JavaCup.jar manifest.txt -C ../bin .

# Run JAR
java -jar JavaCup.jar
```

---

## Creating and Testing Tactics

### Understanding Tactics

A **tactic** is a Java class that implements the `Tactic` interface and defines:
- Team configuration (name, colors, players)
- Player formations (starting positions)
- AI logic (how players move and kick)

### Using the Graphical Tactic Editor

1. **Run the Code Generator**:
   ```batch
   java -cp "bin;libs/*" JavaCupCodeGenerator
   ```

2. **Configure Your Team**:
   - **Tab 1: "Equipo"** (Team)
     - Enter team name, country, coach name
     - Select uniform colors (shirt, shorts, socks, stripes)
     - Choose uniform style (horizontal/vertical stripes, solid, etc.)
     - Click "Al Azar" for random colors

   - **Tab 2: "Jugadores"** (Players)
     - Configure 11 players:
       - Name, jersey number (1-99)
       - Check "¿Es Portero?" for goalkeeper (exactly one required)
       - Set skin and hair colors
       - Distribute attributes using sliders:
         - **Velocidad** (Speed): 0.0-1.0
         - **Remate** (Power): 0.0-1.0
         - **Error** (Precision): 0.0-1.0 (lower is better)
     - **Budget**: Limited credits to distribute among all players

   - **Tab 3: "Alineaciones y Simulación"** (Formations)
     - Drag players to position them on field
     - Create formations:
       - "Inicio Sacando": When your team kicks off
       - "Inicio Recibiendo": When opponent kicks off
       - Custom formations for gameplay
     - Test kicks:
       - Set power, angle, vertical angle
       - Click ">" to simulate trajectory

3. **Generate Code**:
   - Menu → Generar → Generar Todo (Generate All)
   - Saves complete Tactic implementation to clipboard
   - Paste into new Java file in `tacticas/` folder

4. **Save Configuration**:
   - Menu → Archivo → Guardar
   - Saves tactic XML configuration

### Manual Tactic Creation

Create a new file `tacticas/org/javahispano/javacup/tacticas/MyTactic.java`:

```java
package org.javahispano.javacup.tacticas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.javahispano.javacup.model.*;
import org.javahispano.javacup.model.command.*;
import org.javahispano.javacup.model.engine.*;
import org.javahispano.javacup.model.util.*;
import org.javahispano.javacup.render.EstiloUniforme;

public class MyTactic implements Tactic {
    
    @Override
    public TacticDetail getDetail() {
        PlayerDetail[] players = new PlayerDetail[11];
        players[0] = new JugadorImpl("Goalkeeper", 1, true, Color.WHITE, Color.BLACK, 0.5, 0.5, 0.5);
        players[1] = new JugadorImpl("Defender 1", 2, false, Color.WHITE, Color.BLACK, 0.6, 0.4, 0.7);
        // ... configure all 11 players
        
        return new TacticaDetalleImpl(
            "My Team",                      // Team name
            "Country",                      // Country
            "Coach Name",                   // Coach
            Color.RED,                      // Shirt color
            Color.BLUE,                     // Shorts color
            Color.RED,                      // Shirt line color
            Color.WHITE,                    // Socks color
            Color.YELLOW,                   // Goalkeeper color
            EstiloUniforme.FRANJA_VERTICAL, // Uniform style
            Color.WHITE,                    // Alt shirt color
            Color.BLACK,                    // Alt shorts color
            Color.WHITE,                    // Alt shirt line color
            Color.BLACK,                    // Alt socks color
            Color.ORANGE,                   // Alt goalkeeper color
            EstiloUniforme.SIN_ESTILO,      // Alt uniform style
            players
        );
    }
    
    @Override
    public Position[] getStartPositions(GameSituations sp) {
        // Positions when kicking off (after conceding goal)
        return new Position[] {
            new Position(-45, 0),    // Goalkeeper
            new Position(-25, -20),  // Left defender
            new Position(-25, 20),   // Right defender
            // ... positions for all 11 players
        };
    }
    
    @Override
    public Position[] getNoStartPositions(GameSituations sp) {
        // Positions when opponent kicks off (after scoring)
        return new Position[] {
            new Position(-45, 0),    // Goalkeeper
            new Position(-20, -25),  // Left defender
            new Position(-20, 25),   // Right defender
            // ... positions for all 11 players
        };
    }
    
    @Override
    public List<Command> execute(GameSituations sp) {
        List<Command> commands = new ArrayList<>();
        
        // Get game state
        Position ball = sp.ballPosition();
        Position[] myPlayers = sp.myPlayers();
        int[] canKick = sp.canKick();
        
        // Simple AI: Players chase ball
        for (int i = 0; i < 11; i++) {
            if (canKick.length > 0 && canKick[0] == i) {
                // Player can kick - shoot at goal
                commands.add(new CommandHitBall(i, new Position(52, 0), 1.0, false));
            } else {
                // Move toward ball
                commands.add(new CommandMoveTo(i, ball));
            }
        }
        
        return commands;
    }
}
```

### Compile New Tactic

```batch
javac -d bin -cp "bin;libs/*;src;recursos;tacticas" tacticas/org/javahispano/javacup/tacticas/MyTactic.java
```

---

## Running Matches

### Using the GUI (PrincipalFrame)

1. **Launch Application**:
   ```batch
   java -cp "bin;libs/*;recursos;tacticas" JavaCup
   ```

2. **Main Window Opens**:
   - **Tab "Tácticas"**: Select tactics to compete

3. **Select Teams**:
   - Available tactics listed in center panel
   - Click "Táctica Local" to select home team
   - Click "Táctica Visita" to select away team
   - Selected tactics shown at bottom

4. **Choose Viewer**:
   - Dropdown at bottom: "Visor Básico" (2D) or "Visor OpenGL" (3D)

5. **Start Match**:
   - Click "Ver Partido" button
   - Match begins immediately

6. **During Match**:
   - **2D Viewer (VisorBasico)**:
     - Simple top-down view
     - Shows scores and possession at bottom
   
   - **3D Viewer (VisorOpenGl)**:
     - Dynamic 3D camera
     - Keyboard controls:
       - **F1**: Toggle stadium
       - **F2**: Toggle environment
       - **F4**: Cycle player labels (off/numbers/names)
       - **F5**: Toggle scoreboard
       - **P**: Pause/resume
       - **Arrow keys**: Rotate camera
       - **+/-**: Zoom in/out
       - **ESC**: Exit viewer

### Running Tournament Mode (Headless)

For automated testing without GUI:

```batch
java -cp "bin;libs/*;recursos;tacticas" Torneo
```

Modify `Torneo.java` to specify which tactics to compete.

### Saving and Replaying Matches

1. **Save Match**:
   - In PrincipalFrame → Tab "Configuración" → Check "Guardar Partidos"
   - Matches automatically saved after completion

2. **Replay Match**:
   - Tab "Guardados"
   - Click "Agregar partido" → Browse to saved match file
   - Select match from list
   - Click "Ver Partido"

---

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: "java.lang.UnsatisfiedLinkError: no lwjgl in java.library.path"

**Cause**: Native libraries (DLL files) not found.

**Solution**:
1. Ensure all `.dll` files are in the same directory where you run `java` command
2. Or set library path explicitly:
   ```batch
   java -Djava.library.path=. -cp "bin;libs/*;recursos;tacticas" JavaCup
   ```

#### Issue 2: "ClassNotFoundException" or "NoClassDefFoundError"

**Cause**: Missing JAR files in classpath.

**Solution**:
1. Verify all JARs in `libs/` folder exist
2. Check classpath includes all necessary JARs:
   ```batch
   java -cp "bin;libs/*;recursos;tacticas" JavaCup
   ```

#### Issue 3: OpenGL Viewer Crashes or Black Screen

**Cause**: Graphics driver issues or incompatible OpenGL version.

**Solution**:
1. Update graphics drivers to latest version
2. Use 2D viewer instead: Select "Visor Básico" in GUI
3. Disable hardware acceleration:
   ```batch
   java -Dsun.java2d.noddraw=true -cp "bin;libs/*" JavaCup
   ```

#### Issue 4: "Error: Could not find or load main class JavaCup"

**Cause**: Incorrect working directory or classpath.

**Solution**:
1. Ensure you're in `javacup2013` directory
2. Check `bin/` folder contains compiled classes
3. Recompile if necessary:
   ```batch
   javac -d bin -cp "libs/*;src;recursos;tacticas" src/JavaCup.java
   ```

#### Issue 5: Audio Not Playing

**Cause**: Missing audio files or OpenAL issues.

**Solution**:
1. Verify `recursos/audio/` folder contains `.ogg` files
2. Check Windows audio settings (not muted)
3. Install OpenAL:
   - Download from: https://www.openal.org/downloads/
   - Install `oalinst.exe`

#### Issue 6: Compilation Errors with Java 21

**Cause**: Code originally written for Java 6.

**Solution**:
1. Most code should work without changes
2. If warnings appear about deprecated APIs, they can be ignored
3. For incompatible code, modify to use modern Java APIs

#### Issue 7: Config.xml Errors

**Cause**: Corrupted configuration file.

**Solution**:
1. Delete `config.xml`
2. Application will create new default configuration on next run

---

## Advanced Configuration

### Modifying config.xml

The `config.xml` file stores all application settings. Key settings:

```xml
<org.javahispano.javacup.gui.principal.PrincipalDatos>
  <sx>800</sx>                <!-- Window width -->
  <sy>600</sy>                <!-- Window height -->
  <fullscreen>false</fullscreen>
  <visor>1</visor>            <!-- 0=Basic 2D, 1=OpenGL 3D -->
  <fps>20</fps>               <!-- Frames per second -->
  <escala>15.0</escala>       <!-- Zoom level (pixels per meter) -->
  <sonidos>true</sonidos>     <!-- Enable sounds -->
  <volumenAmbiente>0.5</volumenAmbiente>  <!-- Ambient volume -->
  <volumenCancha>0.5</volumenCancha>      <!-- Match sounds volume -->
  <estadio>true</estadio>     <!-- Show stadium -->
  <entorno>true</entorno>     <!-- Show environment -->
  <marcador>true</marcador>   <!-- Show scoreboard -->
</org.javahispano.javacup.gui.principal.PrincipalDatos>
```

### Performance Tuning

**For Better Performance**:
1. Use 2D viewer (Visor Básico)
2. Lower FPS to 15 in config.xml
3. Disable stadium and environment in OpenGL viewer (F1, F2)
4. Close other applications

**For Better Visual Quality**:
1. Use OpenGL viewer (Visor OpenGL)
2. Increase resolution in config.xml
3. Enable all visual effects
4. Set FPS to 30 or 60

### Custom Resolutions

Edit `config.xml` before launching:
```xml
<sx>1920</sx>
<sy>1080</sy>
<fullscreen>true</fullscreen>
```

### Batch Script for Easy Launch

Create `run_javacup.bat` in `javacup2013/` directory:

```batch
@echo off
echo Starting JavaCup 2013...
cd /d %~dp0
java -Djava.library.path=. -cp "bin;libs/*;recursos;tacticas" JavaCup
if errorlevel 1 pause
```

Double-click `run_javacup.bat` to launch application.

### Creating Desktop Shortcut (Windows)

1. Right-click desktop → New → Shortcut
2. Location: 
   ```
   "C:\Program Files\Java\jdk-21\bin\java.exe" -Djava.library.path="C:\javacup2\javacup2013" -cp "C:\javacup2\javacup2013\bin;C:\javacup2\javacup2013\libs\*" JavaCup
   ```
3. Name: JavaCup 2013
4. Click Finish
5. Right-click shortcut → Properties → Start in: `C:\javacup2\javacup2013`

---

## Additional Resources

### Useful Files

- **README.md**: Project overview and architecture summary
- **BACKEND.md**: Detailed backend/model architecture documentation
- **FRONTEND.md**: Detailed frontend/rendering documentation
- **tutorial2012.pdf**: Original Spanish tutorial (legacy reference)

### Example Tactics

Example tactics are located in `tacticas/org/javahispano/javacup/tacticas/jvc2013/`:
- `romedal/RomedalTeam.java`
- `masia13/Masia.java`
- `twentythree/Team2313.java`
- And many more...

Study these for reference when creating your own tactics.

### Online Resources

- **LWJGL Wiki**: https://wiki.lwjgl.org/
- **Slick2D Documentation**: http://slick.ninjacave.com/
- **Java 21 Documentation**: https://docs.oracle.com/en/java/javase/21/

### Getting Help

If you encounter issues:
1. Check this SETUP_AND_RUNNING.md troubleshooting section
2. Review BACKEND.md and FRONTEND.md for architecture details
3. Examine example tactics in `tacticas/` folder
4. Check GitHub issues for similar problems

---

## Quick Start Summary

**For the impatient - minimum steps**:

1. Install Java 21
2. Clone/download project
3. Compile:
   ```batch
   cd javacup2013
   javac -d bin -cp "libs/*;src;recursos;tacticas" src/JavaCup.java
   ```
4. Run:
   ```batch
   java -cp "bin;libs/*;recursos;tacticas" JavaCup
   ```
5. Select two tactics from list
6. Click "Ver Partido"
7. Enjoy the match!

**Happy coding and may the best tactic win! ⚽**
