import { useState, useEffect } from 'react'
import MatchField from './components/MatchField'
import TacticSelector from './components/TacticSelector'
import { useMatchSounds } from './utils/useMatchSounds'
import { fetchTactics, runMatch, checkBackendHealth } from './services/api'
import './App.css'

// Initial player positions (normalized 0-1)
const getInitialPlayers = () => {
  const homeTeam = [
    { number: 1, x: 0.5, y: 0.1, team: 'home', iter: 3 }, // Goalkeeper
    { number: 2, x: 0.3, y: 0.25, team: 'home', iter: 3 }, // Defenders
    { number: 3, x: 0.5, y: 0.25, team: 'home', iter: 3 },
    { number: 4, x: 0.7, y: 0.25, team: 'home', iter: 3 },
    { number: 5, x: 0.3, y: 0.4, team: 'home', iter: 3 }, // Midfielders
    { number: 6, x: 0.5, y: 0.4, team: 'home', iter: 3 },
    { number: 7, x: 0.7, y: 0.4, team: 'home', iter: 3 },
    { number: 8, x: 0.3, y: 0.55, team: 'home', iter: 3 }, // Forwards
    { number: 9, x: 0.5, y: 0.55, team: 'home', iter: 3 },
    { number: 10, x: 0.7, y: 0.55, team: 'home', iter: 3 },
    { number: 11, x: 0.5, y: 0.65, team: 'home', iter: 3 }
  ]

  const awayTeam = [
    { number: 1, x: 0.5, y: 0.9, team: 'away', iter: 3 }, // Goalkeeper
    { number: 2, x: 0.3, y: 0.75, team: 'away', iter: 3 }, // Defenders
    { number: 3, x: 0.5, y: 0.75, team: 'away', iter: 3 },
    { number: 4, x: 0.7, y: 0.75, team: 'away', iter: 3 },
    { number: 5, x: 0.3, y: 0.6, team: 'away', iter: 3 }, // Midfielders
    { number: 6, x: 0.5, y: 0.6, team: 'away', iter: 3 },
    { number: 7, x: 0.7, y: 0.6, team: 'away', iter: 3 },
    { number: 8, x: 0.3, y: 0.45, team: 'away', iter: 3 }, // Forwards
    { number: 9, x: 0.5, y: 0.45, team: 'away', iter: 3 },
    { number: 10, x: 0.7, y: 0.45, team: 'away', iter: 3 },
    { number: 11, x: 0.5, y: 0.35, team: 'away', iter: 3 }
  ]

  return [...homeTeam, ...awayTeam]
}

function App() {
  const [homeTactic, setHomeTactic] = useState('')
  const [awayTactic, setAwayTactic] = useState('')
  const [matchStarted, setMatchStarted] = useState(false)
  const [players, setPlayers] = useState(getInitialPlayers())
  const [ball, setBall] = useState({ x: 0.5, y: 0.5 })
  const [soundEnabled, setSoundEnabled] = useState(true)
  const [tactics, setTactics] = useState([])
  const [loading, setLoading] = useState(false)
  const [backendAvailable, setBackendAvailable] = useState(true)
  const [matchResult, setMatchResult] = useState(null)
  const [error, setError] = useState(null)
  const [currentIteration, setCurrentIteration] = useState(0)
  const [isPlaying, setIsPlaying] = useState(false)

  // Fetch tactics from backend on component mount
  useEffect(() => {
    const loadTactics = async () => {
      try {
        setLoading(true)
        setError(null)
        
        // Check if backend is available
        const isHealthy = await checkBackendHealth()
        setBackendAvailable(isHealthy)
        
        if (!isHealthy) {
          setError('Backend server is not available. Please start the backend.')
          return
        }
        
        const tacticsList = await fetchTactics()
        setTactics(tacticsList)
      } catch (err) {
        console.error('Failed to load tactics:', err)
        setError(`Failed to load tactics: ${err.message}`)
        setBackendAvailable(false)
      } finally {
        setLoading(false)
      }
    }
    
    loadTactics()
  }, [])

  // Initialize sound system
  const { toggleSounds } = useMatchSounds(matchStarted)

  // Helper function to update display for a specific iteration
  const updateIterationDisplay = (iterationIndex) => {
    if (!matchResult?.iterations || iterationIndex >= matchResult.iterations.length) return

    const iteration = matchResult.iterations[iterationIndex]
    
    // Update ball position from ballX and ballY
    if (iteration.ballX !== undefined && iteration.ballY !== undefined) {
      setBall({ 
        x: iteration.ballX, 
        y: iteration.ballY 
      })
    }

    // Update player positions from separate arrays
    if (iteration.homePlayerX && iteration.homePlayerY && 
        iteration.awayPlayerX && iteration.awayPlayerY) {
      
      const updatedPlayers = []
      
      // Calculate animation frame
      const animFrame = iterationIndex % 14
      const pose = animFrame > 6 ? 13 - animFrame : animFrame
      
      // Add home team players
      for (let i = 0; i < iteration.homePlayerX.length; i++) {
        updatedPlayers.push({
          number: i + 1,
          x: iteration.homePlayerX[i],
          y: iteration.homePlayerY[i],
          team: 'home',
          iter: animFrame,
          pose: pose
        })
      }
      
      // Add away team players
      for (let i = 0; i < iteration.awayPlayerX.length; i++) {
        updatedPlayers.push({
          number: i + 1,
          x: iteration.awayPlayerX[i],
          y: iteration.awayPlayerY[i],
          team: 'away',
          iter: animFrame,
          pose: pose
        })
      }
      
      setPlayers(updatedPlayers)
    }
  }

  // Auto-play animation when playing
  useEffect(() => {
    if (!isPlaying || !matchResult?.iterations) return

    const interval = setInterval(() => {
      setCurrentIteration(prev => {
        const next = prev + 1
        if (next >= matchResult.iterations.length) {
          setIsPlaying(false)
          return matchResult.iterations.length - 1
        }
        return next
      })
    }, 50) // Update every 50ms for smoother animation

    return () => clearInterval(interval)
  }, [isPlaying, matchResult])

  // Update display when currentIteration changes
  useEffect(() => {
    if (!matchStarted || !matchResult) return

    // If we have match result data, update the display
    if (matchResult.iterations && matchResult.iterations.length > 0) {
      updateIterationDisplay(currentIteration)
    }
  }, [currentIteration, matchStarted, matchResult])

  const handleToggleSounds = () => {
    const newState = toggleSounds()
    setSoundEnabled(newState)
  }

  // Get tactic names for display
  const homeTacticName = homeTactic
  const awayTacticName = awayTactic

  const handleCreateMatch = async () => {
    if (!homeTactic || !awayTactic) {
      alert('Please select tactics for both teams!')
      return
    }
    
    try {
      setLoading(true)
      setError(null)
      setMatchResult(null)
      
      // Run the match via backend API
      const result = await runMatch(homeTactic, awayTactic)
      
      // Store the match result
      setMatchResult(result)
      setMatchStarted(true)
      setCurrentIteration(0)
      setIsPlaying(true) // Auto-start playback
      
      console.log('Match completed:', {
        homeTeam: result.homeTeam?.teamName,
        awayTeam: result.awayTeam?.teamName,
        score: `${result.finalHomeGoals} - ${result.finalAwayGoals}`,
        iterations: result.iterations?.length || 0
      })
    } catch (err) {
      console.error('Failed to run match:', err)
      setError(`Failed to run match: ${err.message}`)
    } finally {
      setLoading(false)
    }
  }

  const handleResetMatch = () => {
    setMatchStarted(false)
    setMatchResult(null)
    setPlayers(getInitialPlayers())
    setBall({ x: 0.5, y: 0.5 })
    setCurrentIteration(0)
    setIsPlaying(false)
  }

  return (
    <div className="app-container">
      <h1>JavaCup2 - Match Simulator</h1>
      
      <div className="content-wrapper">
        <div className="control-panel">
          <h2>Match Configuration</h2>
          
          {!backendAvailable && (
            <div className="error-message">
              ⚠️ Backend server not available. Please ensure the backend is running on {import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}
            </div>
          )}
          
          {error && (
            <div className="error-message">
              ⚠️ {error}
            </div>
          )}
          
          {loading && <div className="loading-message">⏳ Loading...</div>}
          
          <TacticSelector
            label="Home Team Tactic"
            selectedTactic={homeTactic}
            onTacticChange={setHomeTactic}
            tactics={tactics}
          />
          
          <TacticSelector
            label="Away Team Tactic"
            selectedTactic={awayTactic}
            onTacticChange={setAwayTactic}
            tactics={tactics}
          />
          
          <div className="button-group">
            {!matchStarted ? (
              <button 
                className="create-match-btn"
                onClick={handleCreateMatch}
                disabled={loading || !backendAvailable || !homeTactic || !awayTactic}
              >
                {loading ? 'Running Match...' : 'Create Match'}
              </button>
            ) : (
              <button 
                className="reset-match-btn"
                onClick={handleResetMatch}
              >
                Reset Match
              </button>
            )}
          </div>
          
          <div className="sound-control">
            <button 
              className="sound-toggle-btn"
              onClick={handleToggleSounds}
              title={soundEnabled ? "Disable sounds" : "Enable sounds"}
            >
              {soundEnabled ? '🔊 Sounds On' : '🔇 Sounds Off'}
            </button>
          </div>
          
          {matchResult && (
            <div className="match-status">
              <h3>Match Result</h3>
              <p className="team-info">🔴 {matchResult.homeTeam?.teamName || homeTacticName}: {matchResult.finalHomeGoals} goals</p>
              <p className="team-info">🔵 {matchResult.awayTeam?.teamName || awayTacticName}: {matchResult.finalAwayGoals} goals</p>
              <p className="possession-info">
                Possession: {matchResult.homeTeam?.teamName || homeTacticName} {(matchResult.finalHomePossession * 100).toFixed(1)}% - {matchResult.awayTeam?.teamName || awayTacticName} {((1 - matchResult.finalHomePossession) * 100).toFixed(1)}%
              </p>
              <p className="iterations-info">Total Iterations: {matchResult.iterations?.length || 0}</p>
              
              {/* Playback Controls */}
              <div className="playback-controls">
                <h4>Match Playback</h4>
                <div className="playback-buttons">
                  <button 
                    onClick={() => setCurrentIteration(0)}
                    title="Go to start"
                    className="playback-btn"
                  >
                    ⏮️ Start
                  </button>
                  <button 
                    onClick={() => setIsPlaying(!isPlaying)}
                    title={isPlaying ? "Pause" : "Play"}
                    className="playback-btn"
                  >
                    {isPlaying ? '⏸️ Pause' : '▶️ Play'}
                  </button>
                  <button 
                    onClick={() => setCurrentIteration(matchResult.iterations.length - 1)}
                    title="Go to end"
                    className="playback-btn"
                  >
                    ⏭️ End
                  </button>
                </div>
                <div className="playback-slider">
                  <label htmlFor="iteration-slider">
                    Iteration: {currentIteration + 1} / {matchResult.iterations?.length || 0}
                  </label>
                  <input
                    id="iteration-slider"
                    type="range"
                    min="0"
                    max={Math.max(0, (matchResult.iterations?.length || 1) - 1)}
                    value={currentIteration}
                    onChange={(e) => {
                      setIsPlaying(false)
                      setCurrentIteration(parseInt(e.target.value))
                    }}
                    className="slider"
                  />
                </div>
              </div>
            </div>
          )}
          
          {matchStarted && !matchResult && (
            <div className="match-status">
              <p className="status-active">Match is running...</p>
              <p className="team-info">🔴 Home Team: {homeTacticName}</p>
              <p className="team-info">🔵 Away Team: {awayTacticName}</p>
            </div>
          )}
        </div>
        
        <div className="field-container">
          <MatchField players={players} ball={ball} />
        </div>
      </div>
    </div>
  )
}

export default App
