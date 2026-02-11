import { useRef, useEffect } from 'react'
import './MatchField.css'

const MatchField = ({ players, ball }) => {
  const canvasRef = useRef(null)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    const width = canvas.width
    const height = canvas.height

    // Clear canvas
    ctx.clearRect(0, 0, width, height)

    // Draw field background (green)
    ctx.fillStyle = '#2d7d2d'
    ctx.fillRect(0, 0, width, height)

    // Draw field markings (white)
    ctx.strokeStyle = '#ffffff'
    ctx.lineWidth = 2

    // Outer boundary
    ctx.strokeRect(20, 20, width - 40, height - 40)

    // Center line
    ctx.beginPath()
    ctx.moveTo(20, height / 2)
    ctx.lineTo(width - 20, height / 2)
    ctx.stroke()

    // Center circle
    ctx.beginPath()
    ctx.arc(width / 2, height / 2, 40, 0, Math.PI * 2)
    ctx.stroke()

    // Center spot
    ctx.beginPath()
    ctx.arc(width / 2, height / 2, 3, 0, Math.PI * 2)
    ctx.fillStyle = '#ffffff'
    ctx.fill()

    // Penalty areas
    const penaltyWidth = 180
    const penaltyHeight = 80
    
    // Top penalty area
    ctx.strokeRect((width - penaltyWidth) / 2, 20, penaltyWidth, penaltyHeight)
    
    // Bottom penalty area
    ctx.strokeRect((width - penaltyWidth) / 2, height - 20 - penaltyHeight, penaltyWidth, penaltyHeight)

    // Goal areas
    const goalWidth = 80
    const goalHeight = 30
    
    // Top goal area
    ctx.strokeRect((width - goalWidth) / 2, 20, goalWidth, goalHeight)
    
    // Bottom goal area
    ctx.strokeRect((width - goalWidth) / 2, height - 20 - goalHeight, goalWidth, goalHeight)

    // Penalty spots
    ctx.beginPath()
    ctx.arc(width / 2, 20 + penaltyHeight - 20, 3, 0, Math.PI * 2)
    ctx.fillStyle = '#ffffff'
    ctx.fill()

    ctx.beginPath()
    ctx.arc(width / 2, height - 20 - penaltyHeight + 20, 3, 0, Math.PI * 2)
    ctx.fill()

    // Corner arcs
    const cornerRadius = 10
    // Top-left
    ctx.beginPath()
    ctx.arc(20, 20, cornerRadius, 0, Math.PI / 2)
    ctx.stroke()
    // Top-right
    ctx.beginPath()
    ctx.arc(width - 20, 20, cornerRadius, Math.PI / 2, Math.PI)
    ctx.stroke()
    // Bottom-left
    ctx.beginPath()
    ctx.arc(20, height - 20, cornerRadius, -Math.PI / 2, 0)
    ctx.stroke()
    // Bottom-right
    ctx.beginPath()
    ctx.arc(width - 20, height - 20, cornerRadius, Math.PI, Math.PI * 1.5)
    ctx.stroke()

    // Draw players
    if (players) {
      players.forEach(player => {
        const x = player.x * width
        const y = player.y * height

        // Draw shadow
        ctx.beginPath()
        ctx.arc(x + 2, y + 2, 8, 0, Math.PI * 2)
        ctx.fillStyle = 'rgba(0, 0, 0, 0.3)'
        ctx.fill()

        // Draw player
        ctx.beginPath()
        ctx.arc(x, y, 8, 0, Math.PI * 2)
        ctx.fillStyle = player.team === 'home' ? '#ff0000' : '#0000ff'
        ctx.fill()
        ctx.strokeStyle = '#ffffff'
        ctx.lineWidth = 1
        ctx.stroke()

        // Draw player number
        ctx.fillStyle = '#ffffff'
        ctx.font = 'bold 10px Arial'
        ctx.textAlign = 'center'
        ctx.textBaseline = 'middle'
        ctx.fillText(player.number, x, y)
      })
    }

    // Draw ball
    if (ball) {
      const bx = ball.x * width
      const by = ball.y * height

      // Draw ball shadow
      ctx.beginPath()
      ctx.arc(bx + 1, by + 1, 4, 0, Math.PI * 2)
      ctx.fillStyle = 'rgba(0, 0, 0, 0.4)'
      ctx.fill()

      // Draw ball
      ctx.beginPath()
      ctx.arc(bx, by, 5, 0, Math.PI * 2)
      ctx.fillStyle = '#ffffff'
      ctx.fill()
      ctx.strokeStyle = '#000000'
      ctx.lineWidth = 1
      ctx.stroke()
    }
  }, [players, ball])

  return (
    <div className="match-field-container">
      <canvas 
        ref={canvasRef} 
        width={400} 
        height={600}
        className="match-field-canvas"
      />
    </div>
  )
}

export default MatchField
