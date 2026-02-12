import { useState, useEffect, useMemo } from 'react'
import MatchField from './components/MatchField'
import TacticSelector from './components/TacticSelector'
import './App.css'

// Mock tactics data
const mockTactics = [
  { id: '1', name: '4-4-2 Classic' },
  { id: '2', name: '4-3-3 Attacking' },
  { id: '3', name: '3-5-2 Defensive' },
  { id: '4', name: '4-2-3-1 Modern' },
  { id: '5', name: '5-3-2 Counter' }
]

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

  // Simple animation when match is running
  useEffect(() => {
    if (!matchStarted) return

    const interval = setInterval(() => {
      // Move ball in a simple pattern
      setBall(prev => {
        const newBall = {
          x: Math.max(0.1, Math.min(0.9, prev.x + (Math.random() - 0.5) * 0.02)),
          y: Math.max(0.1, Math.min(0.9, prev.y + (Math.random() - 0.5) * 0.02))
        }
        
        // Animate players moving slightly towards the new ball position
        setPlayers(prevPlayers => 
          prevPlayers.map(player => {
            const dx = (newBall.x - player.x) * 0.01
            const dy = (newBall.y - player.y) * 0.01
            
            // Add some random movement
            const randomX = (Math.random() - 0.5) * 0.005
            const randomY = (Math.random() - 0.5) * 0.005
            
            const newX = Math.max(0.1, Math.min(0.9, player.x + dx + randomX))
            const newY = Math.max(0.1, Math.min(0.9, player.y + dy + randomY))
            
            // Check if player is moving
            const isMoving = Math.abs(newX - player.x) > 0.001 || Math.abs(newY - player.y) > 0.001
            
            // Update animation frame
            let newIter = player.iter
            if (isMoving) {
              // Cycle through animation frames 0-13
              newIter = (player.iter + 1) % 14
            } else {
              // When not moving, use standing pose (frame 3)
              newIter = 3
            }
            
            // Convert iter to pose (0-6 with ping-pong effect)
            let pose = newIter
            if (newIter > 6) {
              pose = 13 - newIter
            }
            
            return {
              ...player,
              x: newX,
              y: newY,
              iter: newIter,
              pose: pose
            }
          })
        )
        
        return newBall
      })
    }, 100) // Update every 100ms

    return () => clearInterval(interval)
  }, [matchStarted])

  const homeTacticName = useMemo(
    () => mockTactics.find(t => t.id === homeTactic)?.name,
    [homeTactic]
  )
  
  const awayTacticName = useMemo(
    () => mockTactics.find(t => t.id === awayTactic)?.name,
    [awayTactic]
  )

  const handleCreateMatch = () => {
    if (!homeTactic || !awayTactic) {
      alert('Please select tactics for both teams!')
      return
    }
    setMatchStarted(true)
  }

  const handleResetMatch = () => {
    setMatchStarted(false)
    setPlayers(getInitialPlayers())
    setBall({ x: 0.5, y: 0.5 })
  }

  return (
    <div className="app-container">
      <h1>JavaCup2 - Match Simulator</h1>
      
      <div className="content-wrapper">
        <div className="control-panel">
          <h2>Match Configuration</h2>
          
          <TacticSelector
            label="Home Team Tactic"
            selectedTactic={homeTactic}
            onTacticChange={setHomeTactic}
            tactics={mockTactics}
          />
          
          <TacticSelector
            label="Away Team Tactic"
            selectedTactic={awayTactic}
            onTacticChange={setAwayTactic}
            tactics={mockTactics}
          />
          
          <div className="button-group">
            {!matchStarted ? (
              <button 
                className="create-match-btn"
                onClick={handleCreateMatch}
              >
                Create Match
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
          
          {matchStarted && (
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
