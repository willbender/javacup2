package com.javacup.model.engine;

import com.javacup.model.TacticDetail;
import com.javacup.model.util.Position;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Complete implementation of SavedMatch for storing and replaying matches.
 * <p>
 * This class stores all match data including:
 * <ul>
 *   <li>Team tactics and configurations</li>
 *   <li>All iterations with player positions and ball state</li>
 *   <li>Match events (goals, kicks, offsides, etc.)</li>
 *   <li>Final match results (score, possession)</li>
 * </ul>
 * </p>
 * <p>
 * The data can be serialized to JSON for storage and transmission.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public final class SavedMatch implements MatchInterface, Serializable {

    private static final long serialVersionUID = 1L;
    
    @JsonIgnore
    private final TacticDetail homeDetail;
    
    @JsonIgnore
    private final TacticDetail awayDetail;
    
    @JsonProperty("homeTeam")
    private final TacticDetailJson homeTeamJson;
    
    @JsonProperty("awayTeam")
    private final TacticDetailJson awayTeamJson;
    
    @JsonIgnore
    private final List<Iteration> iterations;
    
    @JsonProperty("iterations")
    private List<IterationJson> iterationsJson;
    
    @JsonProperty("finalHomeGoals")
    private int finalHomeGoals;
    
    @JsonProperty("finalAwayGoals")
    private int finalAwayGoals;
    
    @JsonProperty("finalHomePossession")
    private double finalHomePossession;
    
    @JsonIgnore
    private int currentIterationIndex = 0;
    
    /**
     * Gets the final home goals.
     *
     * @return final home goals
     */
    public int getFinalHomeGoals() {
        return finalHomeGoals;
    }
    
    /**
     * Gets the final away goals.
     *
     * @return final away goals
     */
    public int getFinalAwayGoals() {
        return finalAwayGoals;
    }
    
    /**
     * Gets the final home possession.
     *
     * @return final home possession
     */
    public double getFinalHomePossession() {
        return finalHomePossession;
    }

    /**
     * Creates a saved match with team details.
     *
     * @param homeDetail home team configuration
     * @param awayDetail away team configuration
     */
    public SavedMatch(TacticDetail homeDetail, TacticDetail awayDetail) {
        this.homeDetail = homeDetail;
        this.awayDetail = awayDetail;
        this.homeTeamJson = new TacticDetailJson(homeDetail);
        this.awayTeamJson = new TacticDetailJson(awayDetail);
        this.iterations = new ArrayList<>();
        this.finalHomeGoals = 0;
        this.finalAwayGoals = 0;
        this.finalHomePossession = 0.0;
    }
    
    /**
     * Adds an iteration snapshot to the saved match.
     *
     * @param iteration the iteration to add
     */
    void addIteration(Iteration iteration) {
        iterations.add(iteration);
    }
    
    /**
     * Sets the final match results.
     *
     * @param homeGoals final home team goals
     * @param awayGoals final away team goals
     * @param homePossession final home possession percentage
     */
    void setFinalResults(int homeGoals, int awayGoals, double homePossession) {
        this.finalHomeGoals = homeGoals;
        this.finalAwayGoals = awayGoals;
        this.finalHomePossession = homePossession;
        
        // Convert all iterations to JSON-friendly format
        this.iterationsJson = new ArrayList<>();
        for (Iteration iter : iterations) {
            iterationsJson.add(new IterationJson(iter));
        }
    }
    
    /**
     * Gets the total number of iterations recorded.
     *
     * @return number of iterations
     */
    public int getTotalIterations() {
        return iterations.size();
    }
    
    /**
     * Gets a specific iteration by index.
     *
     * @param index iteration index
     * @return the iteration at the specified index
     */
    public Iteration getIterationAt(int index) {
        if (index < 0 || index >= iterations.size()) {
            throw new IndexOutOfBoundsException("Iteration index out of bounds: " + index);
        }
        return iterations.get(index);
    }
    
    /**
     * Gets the current iteration for replay.
     *
     * @return current iteration or null if no more iterations
     */
    private Iteration getCurrentIteration() {
        if (currentIterationIndex >= iterations.size()) {
            return null;
        }
        return iterations.get(currentIterationIndex);
    }

    @Override
    public boolean isGoal() {
        Iteration current = getCurrentIteration();
        return current != null && current.isGoal();
    }

    @Override
    public boolean isGoalpost() {
        Iteration current = getCurrentIteration();
        return current != null && current.isGoalpost();
    }

    @Override
    public boolean isBouncing() {
        Iteration current = getCurrentIteration();
        return current != null && current.isBouncing();
    }

    @Override
    public boolean isCheering() {
        Iteration current = getCurrentIteration();
        return current != null && current.isCheering();
    }

    @Override
    public boolean isKicking() {
        Iteration current = getCurrentIteration();
        return current != null && current.isKicking();
    }

    @Override
    public boolean isTakingSetPiece() {
        Iteration current = getCurrentIteration();
        return current != null && current.isTakingSetPiece();
    }

    @Override
    public boolean isWhistling() {
        Iteration current = getCurrentIteration();
        return current != null && current.isWhistling();
    }

    @Override
    public double getBallAltitude() {
        Iteration current = getCurrentIteration();
        return current != null ? Iteration.decompress(current.getBallHeight()) : 0;
    }

    @Override
    public boolean wasRecorded() {
        return true;
    }

    @Override
    public boolean isSetPieceChanged() {
        Iteration current = getCurrentIteration();
        return current != null && current.isSetPieceChanged();
    }

    @Override
    public TacticDetail getHomeDetail() {
        return homeDetail;
    }

    @Override
    public TacticDetail getAwayDetail() {
        return awayDetail;
    }

    @Override
    @JsonIgnore
    public SavedMatch getSavedMatch() {
        return this;
    }

    @Override
    public Position getVisibleBallPosition() {
        Iteration current = getCurrentIteration();
        if (current == null) {
            return new Position();
        }
        return new Position(
            Iteration.decompress(current.getVisibleBallX()),
            Iteration.decompress(current.getVisibleBallY())
        );
    }

    @Override
    public Position[][] getPositions() {
        Iteration current = getCurrentIteration();
        if (current == null) {
            return new Position[3][11];
        }
        
        short[][][] compressedPositions = current.getPositions();
        Position[][] positions = new Position[3][];
        
        // Home team
        positions[0] = new Position[11];
        for (int i = 0; i < 11; i++) {
            positions[0][i] = new Position(
                Iteration.decompress(compressedPositions[0][i][0]),
                Iteration.decompress(compressedPositions[0][i][1])
            );
        }
        
        // Away team
        positions[1] = new Position[11];
        for (int i = 0; i < 11; i++) {
            positions[1][i] = new Position(
                Iteration.decompress(compressedPositions[1][i][0]),
                Iteration.decompress(compressedPositions[1][i][1])
            );
        }
        
        // Ball
        positions[2] = new Position[1];
        positions[2][0] = new Position(
            Iteration.decompress(compressedPositions[2][0][0]),
            Iteration.decompress(compressedPositions[2][0][1])
        );
        
        return positions;
    }

    @Override
    public void iterate() throws Exception {
        if (currentIterationIndex < iterations.size()) {
            currentIterationIndex++;
        }
    }

    @Override
    public int getHomeGoals() {
        Iteration current = getCurrentIteration();
        return current != null ? current.getHomeGoals() : finalHomeGoals;
    }

    @Override
    public int getAwayGoals() {
        Iteration current = getCurrentIteration();
        return current != null ? current.getAwayGoals() : finalAwayGoals;
    }

    @Override
    public int getIteration() {
        Iteration current = getCurrentIteration();
        return current != null ? current.getIteration() : iterations.size();
    }

    @Override
    public double getHomePossession() {
        Iteration current = getCurrentIteration();
        if (current != null) {
            return Iteration.decompress(current.getHomePossessionPercent());
        }
        return finalHomePossession;
    }

    @Override
    public boolean isOffside() {
        Iteration current = getCurrentIteration();
        return current != null && current.isOffside();
    }

    @Override
    public boolean isIndirectFreeKick() {
        Iteration current = getCurrentIteration();
        return current != null && current.isIndirectFreeKick();
    }
}
