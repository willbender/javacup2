import { useRef } from 'react'
import { Canvas, useFrame } from '@react-three/fiber'
import { OrbitControls } from '@react-three/drei'
import './App.css'

function RotatingCube() {
  const meshRef = useRef()
  
  useFrame((state, delta) => {
    if (meshRef.current) {
      meshRef.current.rotation.x += delta
      meshRef.current.rotation.y += delta * 0.5
    }
  })

  return (
    <mesh ref={meshRef}>
      <boxGeometry args={[2, 2, 2]} />
      <meshStandardMaterial color="royalblue" />
    </mesh>
  )
}

function App() {
  return (
    <div className="app-container">
      <h1>Hello World - JavaCup2 Frontend</h1>
      <p>Welcome to the JavaCup2 Frontend built with React and Three.js</p>
      
      <div className="canvas-container">
        <Canvas camera={{ position: [0, 0, 5] }}>
          <ambientLight intensity={0.5} />
          <pointLight position={[10, 10, 10]} />
          <RotatingCube />
          <OrbitControls />
        </Canvas>
      </div>
      
      <p className="instructions">
        Drag to rotate • Scroll to zoom
      </p>
    </div>
  )
}

export default App
