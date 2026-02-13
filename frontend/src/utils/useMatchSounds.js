import { useEffect, useRef, useCallback } from 'react'
import soundManager from './SoundManager'

/**
 * Custom hook for managing match sounds
 * @param {boolean} matchStarted - Whether the match is running
 * @returns {object} - Sound control functions
 */
export const useMatchSounds = (matchStarted) => {
  const ambientIntervalRef = useRef(null)
  const iterCountRef = useRef(0)

  // Start/stop ambient crowd sounds
  useEffect(() => {
    if (matchStarted && soundManager.isEnabled()) {
      // Play whistle when match starts
      soundManager.play('silbato', 1.0, true)

      // Start ambient crowd sounds (play every 50-70 iterations like the original)
      ambientIntervalRef.current = setInterval(() => {
        iterCountRef.current++
        
        // Play ambient sound every 50-70 iterations
        if (iterCountRef.current % 60 === 0) {
          const ambientSounds = ['0', '1', '2', '3', '4', '5', '6', '7', '8']
          soundManager.playRandom(ambientSounds, 0.5, false)
        }
      }, 100)
    } else {
      // Stop ambient sounds when match is not running
      if (ambientIntervalRef.current) {
        clearInterval(ambientIntervalRef.current)
        ambientIntervalRef.current = null
      }
      iterCountRef.current = 0
    }

    return () => {
      if (ambientIntervalRef.current) {
        clearInterval(ambientIntervalRef.current)
      }
    }
  }, [matchStarted])

  // Play kick/shooting sound
  const playKickSound = useCallback(() => {
    if (!soundManager.isEnabled()) return
    soundManager.playRandom(['remate1', 'remate2'], 1.0, true)
  }, [])

  // Play goal sound
  const playGoalSound = useCallback(() => {
    if (!soundManager.isEnabled()) return
    soundManager.play('gol', 1.0, false)
    // Add cheering after goal
    setTimeout(() => {
      soundManager.playRandom(['ovacion1', 'ovacion2'], 1.0, false)
    }, 500)
  }, [])

  // Play post hit sound
  const playPostSound = useCallback(() => {
    if (!soundManager.isEnabled()) return
    soundManager.playRandom(['poste1', 'poste2'], 1.0, true)
  }, [])

  // Play bounce sound
  const playBounceSound = useCallback(() => {
    if (!soundManager.isEnabled()) return
    soundManager.play('rebote', 1.0, true)
  }, [])

  // Play whistle sound
  const playWhistleSound = useCallback(() => {
    if (!soundManager.isEnabled()) return
    soundManager.play('silbato', 1.0, true)
  }, [])

  // Toggle sound on/off
  const toggleSounds = useCallback(() => {
    soundManager.setEnabled(!soundManager.isEnabled())
    return soundManager.isEnabled()
  }, [])

  return {
    playKickSound,
    playGoalSound,
    playPostSound,
    playBounceSound,
    playWhistleSound,
    toggleSounds,
    isSoundEnabled: () => soundManager.isEnabled()
  }
}
