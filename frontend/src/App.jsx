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

  // Simple animation when match is running - use actual match data if available
  useEffect(() => {
    if (!matchStarted || !matchResult) return

    // If we have match result data, animate through the iterations
    if (matchResult.iterationsJson && matchResult.iterationsJson.length > 0) {
      let currentIteration = 0
      const totalIterations = matchResult.iterationsJson.length

      const interval = setInterval(() => {
        if (currentIteration >= totalIterations) {
          clearInterval(interval)
          return
        }

        const iteration = matchResult.iterationsJson[currentIteration]
        
        // Update ball position
        if (iteration.visibleBallPosition) {
          setBall({ 
            x: iteration.visibleBallPosition.x, 
            y: iteration.visibleBallPosition.y 
          })
        }

        // Update player positions
        if (iteration.positions && iteration.positions.length === 2) {
          const homePositions = iteration.positions[0]
          const awayPositions = iteration.positions[1]
          
          const updatedPlayers = []
          
          // Add home team players
          homePositions.forEach((pos, index) => {
            updatedPlayers.push({
              number: index + 1,
              x: pos.x,
              y: pos.y,
              team: 'home',
              iter: currentIteration % 14,
              pose: (currentIteration % 14) > 6 ? 13 - (currentIteration % 14) : (currentIteration % 14)
            })
          })
          
          // Add away team players
          awayPositions.forEach((pos, index) => {
            updatedPlayers.push({
              number: index + 1,
              x: pos.x,
              y: pos.y,
              team: 'away',
              iter: currentIteration % 14,
              pose: (currentIteration % 14) > 6 ? 13 - (currentIteration % 14) : (currentIteration % 14)
            })
          })
          
          setPlayers(updatedPlayers)
        }

        currentIteration++
      }, 50) // Update every 50ms for smoother animation

      return () => clearInterval(interval)
    }
  }, [matchStarted, matchResult])

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
      
      console.log('Match completed:', {
        homeTeam: result.homeTeam?.teamName,
        awayTeam: result.awayTeam?.teamName,
        score: `${result.finalHomeGoals} - ${result.finalAwayGoals}`,
        iterations: result.totalIterations
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
                Possession: {matchResult.homeTeam?.teamName || homeTacticName} {(matchResult.finalHomePossession * 100).toFixed(1)}% - 
                {matchResult.awayTeam?.teamName || awayTacticName} {((1 - matchResult.finalHomePossession) * 100).toFixed(1)}%
              </p>
              <p className="iterations-info">Total Iterations: {matchResult.totalIterations}</p>
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
