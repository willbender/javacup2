import { useRef, useEffect, useState } from 'react'
import './MatchField.css'

const MatchField = ({ players, ball }) => {
  const canvasRef = useRef(null)
  const [images, setImages] = useState({
    field: null,
    goalTop: null,
    goalBottom: null,
    ballFrames: [],
    ballShadow: null,
    playerShadow: null,
    playerSprites: {}
  })
  const [ballFrame, setBallFrame] = useState(0)

  // Load all images
  useEffect(() => {
    const loadImage = (src) => {
      return new Promise((resolve, reject) => {
        const img = new Image()
        img.onload = () => resolve(img)
        img.onerror = reject
        img.src = src
      })
    }

    const loadAllImages = async () => {
      try {
        const [field, goalTop, goalBottom, ballShadow, playerShadow, ...ballFrames] = await Promise.all([
          loadImage('/images/field/field.png'),
          loadImage('/images/goals/goal-top.png'),
          loadImage('/images/goals/goal-bottom.png'),
          loadImage('/images/ball/shadow.png'),
          loadImage('/images/player/shadow.png'),
          loadImage('/images/ball/ball-0.png'),
          loadImage('/images/ball/ball-1.png'),
          loadImage('/images/ball/ball-2.png'),
          loadImage('/images/ball/ball-3.png'),
          loadImage('/images/ball/ball-4.png'),
          loadImage('/images/ball/ball-5.png'),
        ])

        // Load player sprites (style 1 - default)
        const playerSprites = {}
        for (let i = 0; i < 7; i++) {
          playerSprites[`1${i}`] = await loadImage(`/images/player/1${i}.png`)
        }

        setImages({
          field,
          goalTop,
          goalBottom,
          ballFrames,
          ballShadow,
          playerShadow,
          playerSprites
        })
      } catch (error) {
        console.error('Error loading images:', error)
      }
    }

    loadAllImages()
  }, [])

  // Animate ball
  useEffect(() => {
    const interval = setInterval(() => {
      setBallFrame(prev => (prev + 1) % 6)
    }, 100)
    return () => clearInterval(interval)
  }, [])

  // Draw the field
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas || !images.field) return

    const ctx = canvas.getContext('2d')
    const width = canvas.width
    const height = canvas.height

    // Clear canvas
    ctx.clearRect(0, 0, width, height)

    // Draw field image (scaled to fit canvas)
    ctx.drawImage(images.field, 0, 0, width, height)

    // Draw goals if loaded
    if (images.goalTop) {
      const goalWidth = 60
      const goalHeight = 20
      ctx.drawImage(images.goalTop, (width - goalWidth) / 2, 10, goalWidth, goalHeight)
    }
    
    if (images.goalBottom) {
      const goalWidth = 60
      const goalHeight = 20
      ctx.drawImage(images.goalBottom, (width - goalWidth) / 2, height - 30, goalWidth, goalHeight)
    }

    // Draw players
    if (players && images.playerShadow) {
      players.forEach(player => {
        const x = player.x * width
        const y = player.y * height

        // Draw shadow
        if (images.playerShadow) {
          ctx.drawImage(images.playerShadow, x - 6, y - 6, 13, 13)
        }

        // Draw player sprite or fallback to circle
        const spriteKey = `1${player.pose || 0}`
        if (images.playerSprites[spriteKey]) {
          ctx.drawImage(images.playerSprites[spriteKey], x - 6, y - 6, 13, 13)
        } else {
          // Fallback to drawing circles
          ctx.beginPath()
          ctx.arc(x, y, 8, 0, Math.PI * 2)
          ctx.fillStyle = player.team === 'home' ? '#ff0000' : '#0000ff'
          ctx.fill()
          ctx.strokeStyle = '#ffffff'
          ctx.lineWidth = 1
          ctx.stroke()
        }

        // Draw player number
        ctx.fillStyle = '#ffffff'
        ctx.font = 'bold 10px Arial'
        ctx.textAlign = 'center'
        ctx.textBaseline = 'middle'
        ctx.fillText(player.number, x, y)
      })
    }

    // Draw ball with animation
    if (ball && images.ballFrames.length > 0) {
      const bx = ball.x * width
      const by = ball.y * height

      // Draw ball shadow
      if (images.ballShadow) {
        ctx.drawImage(images.ballShadow, bx - 2, by - 2, 8, 8)
      }

      // Draw animated ball
      const ballImage = images.ballFrames[ballFrame]
      if (ballImage) {
        ctx.drawImage(ballImage, bx - 4, by - 4, 8, 8)
      }
    }
  }, [players, ball, images, ballFrame])

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
