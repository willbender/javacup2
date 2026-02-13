# Sound System Documentation

## Overview

The JavaCup2 frontend includes a complete sound system migrated from the original JavaCup2013 project. The implementation provides an immersive audio experience during matches with crowd ambiance, game events, and user controls.

## Audio Files

All sound files are located in `public/sounds/` and are in OGG format for broad browser compatibility.

### Available Sounds

- **Ambient Crowd Sounds** (9 files): `0.ogg` through `8.ogg`
  - Background stadium atmosphere
  - Played randomly every ~6 seconds during matches
  - Volume: 50% (0.5)

- **Game Event Sounds**:
  - `silbato.ogg` - Whistle (match start, important events)
  - `gol.ogg` - Goal celebration
  - `remate1.ogg`, `remate2.ogg` - Ball kicks/shots
  - `rebote.ogg` - Ball bounce
  - `poste1.ogg`, `poste2.ogg` - Ball hitting goal post
  - `ovacion1.ogg`, `ovacion2.ogg` - Crowd cheering
  - Volume: 100% (1.0)

## Architecture

### SoundManager (`src/utils/SoundManager.js`)

Singleton class that manages all audio playback:

```javascript
import soundManager from './utils/SoundManager'

// Play a specific sound
soundManager.play('silbato', 1.0, true) // name, volume, pitchVariation

// Play random sound from array
soundManager.playRandom(['remate1', 'remate2'], 1.0, true)

// Enable/disable all sounds
soundManager.setEnabled(false)
soundManager.setEnabled(true)

// Stop all sounds
soundManager.stopAll()
```

#### Features

- **Preloading**: All sounds are preloaded on app startup
- **Sound Cloning**: Allows overlapping playback of the same sound
- **Pitch Variation**: ±5% random pitch shift for variety
- **Volume Control**: Independent volume per sound
- **Error Handling**: Graceful degradation if sounds fail to load

### useMatchSounds Hook (`src/utils/useMatchSounds.js`)

React hook for integrating sounds into match gameplay:

```javascript
const { 
  playKickSound,
  playGoalSound,
  playPostSound,
  playBounceSound,
  playWhistleSound,
  toggleSounds,
  isSoundEnabled
} = useMatchSounds(matchStarted)
```

#### Automatic Sound Events

- **Match Start**: Whistle plays automatically when match begins
- **Ambient Sounds**: Crowd sounds play periodically during the match
- **Match Stop**: All sounds stop when match ends

#### Manual Sound Triggers

Use the returned functions to trigger sounds on specific events:

```javascript
// When ball is kicked
playKickSound()

// When goal is scored
playGoalSound()

// When ball hits post
playPostSound()

// When ball bounces
playBounceSound()

// For important events
playWhistleSound()
```

## Usage in Components

### Basic Integration

```javascript
import { useMatchSounds } from './utils/useMatchSounds'

function MatchComponent() {
  const [matchStarted, setMatchStarted] = useState(false)
  const { playKickSound, toggleSounds } = useMatchSounds(matchStarted)
  
  // Trigger sound on event
  const handleBallKick = () => {
    playKickSound()
    // ... other logic
  }
  
  return (
    <div>
      <button onClick={toggleSounds}>Toggle Sounds</button>
      <button onClick={handleBallKick}>Kick Ball</button>
    </div>
  )
}
```

### Current Implementation

The sound system is integrated into `App.jsx`:

1. **Match Start**: Whistle plays when "Create Match" is clicked
2. **Ball Movement**: Kick sounds play randomly (15% chance) when ball moves significantly
3. **Ambient Atmosphere**: Crowd sounds play every ~6 seconds during the match
4. **User Control**: Toggle button switches between 🔊 (On) and 🔇 (Off)

## Browser Compatibility

The implementation uses the native HTML5 Audio API, which is supported by all modern browsers:

- Chrome/Edge: ✅ Full support
- Firefox: ✅ Full support
- Safari: ✅ Full support
- Opera: ✅ Full support

### Autoplay Policy

Some browsers restrict autoplay of audio. The whistle sound on match start may require user interaction first. This is handled automatically by the browser.

## Future Enhancements

Potential additions to the sound system:

1. **More Event Sounds**: 
   - Goal-specific sounds when ball reaches goal area
   - Post hit detection and sound
   - Collision/tackle sounds between players

2. **Volume Controls**: 
   - Individual volume sliders for ambient vs game sounds
   - Master volume control

3. **Sound Themes**: 
   - Different stadium atmospheres
   - Multiple commentator options

4. **Positional Audio**: 
   - Stereo panning based on ball/player position
   - Distance-based volume

## Troubleshooting

### Sounds Not Playing

1. Check browser console for errors
2. Verify sound files exist in `public/sounds/`
3. Ensure sounds are enabled (check toggle button)
4. Try interacting with the page first (browser autoplay policy)

### Performance Issues

If too many sounds cause lag:

1. Reduce ambient sound frequency in `useMatchSounds.js` (increase the iteration count)
2. Reduce kick sound probability in `App.jsx` (lower the random threshold)

### File Format Issues

OGG format is widely supported, but if you encounter issues:

1. Convert sounds to MP3 or WAV format
2. Update file extensions in `SoundManager.js`
3. Ensure browser supports the format

## Credits

Sound files and original implementation from JavaCup2013 project.
Adapted for web using HTML5 Audio API and React hooks.
