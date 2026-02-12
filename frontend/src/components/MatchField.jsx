import { useRef, useEffect, useState } from 'react'
import './MatchField.css'

// Animation constants
const BALL_FRAME_COUNT = 6
const BALL_ANIMATION_INTERVAL_MS = 100
const DEFAULT_PLAYER_STYLE = 1
const MIN_PLAYER_POSE = 0
const MAX_PLAYER_POSE = 6

// Rendering constants
const PLAYER_SPRITE_OFFSET = 6
const PLAYER_SPRITE_SIZE = 13
const PLAYER_FALLBACK_RADIUS = 8
const BALL_SHADOW_OFFSET = 2
const BALL_SHADOW_SIZE = 8
const BALL_OFFSET = 4
const BALL_SIZE = 8
const GOAL_WIDTH = 60
const GOAL_HEIGHT = 20
const GOAL_TOP_OFFSET = 10
const GOAL_BOTTOM_OFFSET = 30

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
        for (let i = MIN_PLAYER_POSE; i <= MAX_PLAYER_POSE; i++) {
          playerSprites[`${DEFAULT_PLAYER_STYLE}${i}`] = await loadImage(`/images/player/${DEFAULT_PLAYER_STYLE}${i}.png`)
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
      setBallFrame(prev => (prev + 1) % BALL_FRAME_COUNT)
    }, BALL_ANIMATION_INTERVAL_MS)
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
      ctx.drawImage(images.goalTop, (width - GOAL_WIDTH) / 2, GOAL_TOP_OFFSET, GOAL_WIDTH, GOAL_HEIGHT)
    }
    
    if (images.goalBottom) {
      ctx.drawImage(images.goalBottom, (width - GOAL_WIDTH) / 2, height - GOAL_BOTTOM_OFFSET, GOAL_WIDTH, GOAL_HEIGHT)
    }

    // Draw players
    if (players) {
      players.forEach(player => {
        const x = player.x * width
        const y = player.y * height

        // Draw shadow
        if (images.playerShadow) {
          ctx.drawImage(images.playerShadow, x - PLAYER_SPRITE_OFFSET, y - PLAYER_SPRITE_OFFSET, PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE)
        }

        // Draw player sprite or fallback to circle
        const spriteKey = `${DEFAULT_PLAYER_STYLE}${player.pose || 0}`
        if (images.playerSprites[spriteKey]) {
          ctx.drawImage(images.playerSprites[spriteKey], x - PLAYER_SPRITE_OFFSET, y - PLAYER_SPRITE_OFFSET, PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE)
        } else {
          // Fallback to drawing circles
          ctx.beginPath()
          ctx.arc(x, y, PLAYER_FALLBACK_RADIUS, 0, Math.PI * 2)
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
        ctx.drawImage(images.ballShadow, bx - BALL_SHADOW_OFFSET, by - BALL_SHADOW_OFFSET, BALL_SHADOW_SIZE, BALL_SHADOW_SIZE)
      }

      // Draw animated ball
      const ballImage = images.ballFrames[ballFrame]
      if (ballImage) {
        ctx.drawImage(ballImage, bx - BALL_OFFSET, by - BALL_OFFSET, BALL_SIZE, BALL_SIZE)
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
