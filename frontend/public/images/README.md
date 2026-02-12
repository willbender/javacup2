# JavaCup2 Graphics Assets

This directory contains all graphics assets migrated from the original javacup2013 project, with Spanish filenames translated to English.

## Directory Structure

### `/field` - Field Graphics
- `field.png` - Main soccer field image (formerly `cancha.png`)
- `field-alt.jpg` - Alternative field image (formerly `campo.jpg`)
- `grass.png` - Grass texture (formerly `pasto.png`)
- `grass-v.png` - Vertical grass texture (formerly `pastov.png`)
- `mini-grass.png` - Mini grass texture (formerly `minipasto.png`)
- `mini-grass-2.png` - Alternative mini grass texture (formerly `minipasto2.png`)
- `clay.png` - Clay texture (formerly `arcilla.png`)

### `/goals` - Goal Graphics
- `goal-top.png` - Top goal image (formerly `arco_superior.png`)
- `goal-bottom.png` - Bottom goal image (formerly `arco_inferior.png`)
- `goal-top-1.2.png` - Top goal scaled 1.2x (formerly `arco_superior.1.2.png`)
- `goal-top-1.5.png` - Top goal scaled 1.5x (formerly `arco_superior.1.5.png`)
- `goal-bottom-1.2.png` - Bottom goal scaled 1.2x (formerly `arco_inferior.1.2.png`)
- `goal-bottom-1.5.png` - Bottom goal scaled 1.5x (formerly `arco_inferior.1.5.png`)

### `/ball` - Ball Graphics
- `miniball.png` - Mini ball icon (formerly `minibalon.png`)
- `ball-0.png` through `ball-5.png` - Ball animation frames (formerly `balon0.png` through `balon5.png`)
- `shadow.png` - Ball shadow (formerly `balon/sombra.png`)

**Animation**: The ball uses 6 frames (ball-0.png through ball-5.png) to create a rotation effect. Cycle through these frames at ~100ms intervals for smooth animation.

### `/player` - Player Sprite Graphics
- `10.png` through `66.png` - Player sprites for different styles and poses
- `shadow.png` - Player shadow (formerly `sombra.png`)

**Player Sprite Format**: Files are named as `[style][pose].png` where:
- Style: 1-6 (different player appearances)
- Pose: 0-6 (different animation frames/poses)

Example: `13.png` = Style 1, Pose 3

### `/ui` - UI Control Graphics
- `forward.png` - Forward/advance button (formerly `avanza.png`)
- `backward.png` - Backward/rewind button (formerly `retrocede.png`)
- `pause.png` - Pause button (formerly `pausa.png`)
- `change.png` - Change/substitute button (formerly `cambio.png`)
- `offside.png` - Offside indicator (formerly `fuerajuego.png`)
- `goal.png` - Goal celebration graphic (formerly `gol.png`)
- `x.png` - Close/delete button
- `running-lines.png` - Running lines background
- `running-plain.png` - Running plain background

### `/icons` - Application Icons
Various UI icons including:
- `add.png` - Add button
- `delete.png` - Delete button
- `compile.png` - Compile button
- `up.png`, `down.png` - Navigation arrows
- `equipo.png` - Team icon
- `local.png` - Home team icon
- `visita.png` - Away team icon
- `mas.png` - Plus icon
- `menos.png` - Minus icon

### `/logos` - Logo Graphics
- `pubhor.png` - Horizontal publication logo
- `pubvert.png` - Vertical publication logo

### `/stadiums` - Stadium Graphics
- `/bernabeu` - Bernabéu stadium graphics
  - `bernabeu.png` - Stadium image
  - `entorno.bernabeu.jpg` - Stadium environment
- `/noucamp` - Nou Camp stadium graphics
  - `noucamp_estadio.png` - Stadium image
  - `noucamp_entorno.jpg` - Stadium environment

### Root Level
- `javacup.png` - JavaCup application icon
- `javacup.ico` - JavaCup icon file

## Usage in Components

### MatchField Component
The `MatchField.jsx` component uses the following graphics:
- Field background: `/images/field/field.png`
- Goals: `/images/goals/goal-top.png` and `/images/goals/goal-bottom.png`
- Ball animation: `/images/ball/ball-0.png` through `/images/ball/ball-5.png`
- Ball shadow: `/images/ball/shadow.png`
- Player sprites: `/images/player/[style][pose].png`
- Player shadow: `/images/player/shadow.png`

### Animation Details

**Ball Animation**: 
- 6 frames cycle at 100ms intervals
- Creates a rolling/spinning effect
- Ball shadow rendered separately for depth

**Player Animation**:
- 7 poses per style (0-6)
- Different poses represent running, kicking, standing, etc.
- Shadow rendered below player for depth effect

## Original Source
These graphics were migrated from `javacup2013/recursos/imagenes/` with translated English filenames for better maintainability.
