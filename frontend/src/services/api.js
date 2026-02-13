// API service for communicating with the JavaCup backend

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

/**
 * Fetches the list of available tactics from the backend
 * @returns {Promise<Array<string>>} Array of tactic names
 */
export const fetchTactics = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/tactics`)
    
    if (!response.ok) {
      throw new Error(`Failed to fetch tactics: ${response.statusText}`)
    }
    
    const data = await response.json()
    return data.tactics || []
  } catch (error) {
    console.error('Error fetching tactics:', error)
    throw error
  }
}

/**
 * Runs a match between two tactics
 * @param {string} homeTacticName - Name of the home team tactic
 * @param {string} awayTacticName - Name of the away team tactic
 * @returns {Promise<Object>} SavedMatch object with complete match data
 */
export const runMatch = async (homeTacticName, awayTacticName) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/match/run`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        homeTacticName,
        awayTacticName,
      }),
    })
    
    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.error || `Failed to run match: ${response.statusText}`)
    }
    
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Error running match:', error)
    throw error
  }
}

/**
 * Checks if the backend API is available
 * @returns {Promise<boolean>} True if the backend is reachable
 */
export const checkBackendHealth = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/health`)
    return response.ok
  } catch (error) {
    console.error('Backend health check failed:', error)
    return false
  }
}
