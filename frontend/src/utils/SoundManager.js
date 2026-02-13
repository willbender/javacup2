/**
 * SoundManager - Handles audio playback for the JavaCup match
 * Based on the original JavaCup2013 sound implementation
 */
export class SoundManager {
  constructor() {
    this.sounds = {}
    this.enabled = true
    this.ambientInterval = null
    this.lastAmbientTime = 0
  }

  /**
   * Load a sound file
   * @param {string} name - Sound identifier
   * @param {string} path - Path to sound file
   */
  load(name, path) {
    try {
      const audio = new Audio(path)
      audio.preload = 'auto'
      this.sounds[name] = audio
    } catch (error) {
      console.error(`Failed to load sound: ${name}`, error)
    }
  }

  /**
   * Play a sound with optional volume and pitch variation
   * @param {string} name - Sound identifier
   * @param {number} volume - Volume level (0.0 to 1.0)
   * @param {boolean} pitchVariation - Add random pitch variation
   */
  play(name, volume = 1.0, pitchVariation = false) {
    if (!this.enabled || !this.sounds[name]) return

    try {
      const sound = this.sounds[name].cloneNode()
      sound.volume = Math.max(0, Math.min(1, volume))
      
      // Add pitch variation (±5%) like the original implementation
      if (pitchVariation) {
        sound.playbackRate = 1.0 + (Math.random() - 0.5) * 0.1
      }
      
      sound.play().catch(err => {
        console.warn(`Failed to play sound: ${name}`, err)
      })
    } catch (error) {
      console.error(`Error playing sound: ${name}`, error)
    }
  }

  /**
   * Play a random sound from a list
   * @param {string[]} names - Array of sound identifiers
   * @param {number} volume - Volume level
   * @param {boolean} pitchVariation - Add random pitch variation
   */
  playRandom(names, volume = 1.0, pitchVariation = false) {
    if (names.length === 0) return
    const randomName = names[Math.floor(Math.random() * names.length)]
    this.play(randomName, volume, pitchVariation)
  }

  /**
   * Stop a specific sound
   * @param {string} name - Sound identifier
   */
  stop(name) {
    if (this.sounds[name]) {
      try {
        this.sounds[name].pause()
        this.sounds[name].currentTime = 0
      } catch (error) {
        console.error(`Error stopping sound: ${name}`, error)
      }
    }
  }

  /**
   * Stop all sounds
   */
  stopAll() {
    Object.values(this.sounds).forEach(sound => {
      try {
        sound.pause()
        sound.currentTime = 0
      } catch (error) {
        // Ignore errors when stopping
      }
    })
  }

  /**
   * Enable or disable all sounds
   * @param {boolean} enabled - Whether sounds should be enabled
   */
  setEnabled(enabled) {
    this.enabled = enabled
    if (!enabled) {
      this.stopAll()
    }
  }

  /**
   * Check if sounds are enabled
   * @returns {boolean}
   */
  isEnabled() {
    return this.enabled
  }
}

// Create singleton instance
const soundManager = new SoundManager()

// Load all sounds
const soundFiles = [
  // Ambient crowd sounds
  '0', '1', '2', '3', '4', '5', '6', '7', '8',
  // Game sounds
  'gol', 'ovacion1', 'ovacion2', 'poste1', 'poste2',
  'rebote', 'remate1', 'remate2', 'silbato'
]

soundFiles.forEach(name => {
  soundManager.load(name, `/sounds/${name}.ogg`)
})

export default soundManager
