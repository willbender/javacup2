package com.javacup.model.engine;

import com.javacup.model.TacticDetail;
import com.javacup.model.util.Position;

/**
 * Public interface for match execution and visualization.
 * <p>
 * This interface provides read-only access to match state for viewers
 * and controls match execution. It hides internal implementation details
 * while exposing necessary information for rendering and match control.
 * </p>
 * <p>
 * <strong>Usage Pattern:</strong>
 * <ol>
 *   <li>Create match with two tactics</li>
 *   <li>Call {@link #iterate()} repeatedly to advance match</li>
 *   <li>Query match state for rendering/display</li>
 *   <li>Continue until match ends (3600 iterations)</li>
 * </ol>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public interface MatchInterface {

    /**
     * Checks if a goal was scored in the current iteration.
     *
     * @return true if goal was scored
     */
    boolean isGoal();

    /**
     * Checks if ball hit the goalpost in the current iteration.
     *
     * @return true if ball hit goalpost
     */
    boolean isGoalpost();

    /**
     * Checks if ball is bouncing on the grass.
     *
     * @return true if ball is bouncing
     */
    boolean isBouncing();

    /**
     * Checks if crowd is cheering.
     *
     * @return true if crowd is cheering
     */
    boolean isCheering();

    /**
     * Checks if a player is kicking the ball.
     *
     * @return true if player is kicking
     */
    boolean isKicking();

    /**
     * Checks if a set piece (kickoff, throw-in, etc.) is being taken.
     *
     * @return true if set piece is being taken
     */
    boolean isTakingSetPiece();

    /**
     * Checks if referee is whistling.
     *
     * @return true if referee is whistling
     */
    boolean isWhistling();

    /**
     * Gets the ball's current altitude above ground.
     *
     * @return ball height in meters (0 = on ground)
     */
    double getBallAltitude();

    /**
     * Checks if the match was recorded for replay.
     *
     * @return true if match was recorded
     */
    boolean wasRecorded();

    /**
     * Checks if there was a change in set piece situation this iteration.
     *
     * @return true if set piece changed
     */
    boolean isSetPieceChanged();

    /**
     * Gets the home team's tactic detail.
     *
     * @return home team configuration
     */
    TacticDetail getHomeDetail();

    /**
     * Gets the away team's tactic detail.
     *
     * @return away team configuration
     */
    TacticDetail getAwayDetail();

    /**
     * Gets the saved match data (if match was recorded).
     *
     * @return saved match, or null if not recorded
     */
    SavedMatch getSavedMatch();

    /**
     * Gets the visible ball position (may differ from actual when out of bounds).
     *
     * @return visible ball position
     */
    Position getVisibleBallPosition();

    /**
     * Gets positions of all entities on the field.
     * <p>
     * Returns a 2D array where:
     * <ul>
     *   <li>positions[0] = home team player positions (11 positions)</li>
     *   <li>positions[1] = away team player positions (11 positions)</li>
     *   <li>positions[2][0] = ball position</li>
     * </ul>
     * </p>
     *
     * @return array of positions
     */
    Position[][] getPositions();

    /**
     * Executes one match iteration (1/60th of a second).
     * <p>
     * This method:
     * <ul>
     *   <li>Updates ball physics</li>
     *   <li>Calls tactics for commands</li>
     *   <li>Processes player movements</li>
     *   <li>Handles kicks and ball physics</li>
     *   <li>Detects goals, out-of-bounds, etc.</li>
     *   <li>Updates match state</li>
     * </ul>
     * </p>
     *
     * @throws Exception if an error occurs during iteration
     */
    void iterate() throws Exception;

    /**
     * Gets the home team's current score.
     *
     * @return number of goals scored by home team
     */
    int getHomeGoals();

    /**
     * Gets the away team's current score.
     *
     * @return number of goals scored by away team
     */
    int getAwayGoals();

    /**
     * Gets the current iteration number.
     * <p>
     * Match runs from iteration 0 to 3599 (3600 total iterations).
     * </p>
     *
     * @return current iteration (0-3599)
     */
    int getIteration();

    /**
     * Gets the home team's ball possession percentage.
     *
     * @return possession percentage (0.0-1.0)
     */
    double getHomePossession();

    /**
     * Checks if an offside violation occurred.
     *
     * @return true if offside occurred
     */
    boolean isOffside();

    /**
     * Checks if an indirect free kick was awarded.
     *
     * @return true if indirect free kick was awarded
     */
    boolean isIndirectFreeKick();
}
