import './TacticSelector.css'

const TacticSelector = ({ label, selectedTactic, onTacticChange, tactics }) => {
  return (
    <div className="tactic-selector">
      <label className="tactic-label">{label}</label>
      <select 
        className="tactic-dropdown"
        value={selectedTactic}
        onChange={(e) => onTacticChange(e.target.value)}
      >
        <option value="">Select a tactic...</option>
        {tactics.map((tactic, index) => (
          <option key={index} value={tactic.id}>
            {tactic.name}
          </option>
        ))}
      </select>
    </div>
  )
}

export default TacticSelector
