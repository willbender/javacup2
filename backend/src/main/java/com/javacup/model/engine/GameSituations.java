package com.javacup.model.engine;

import com.javacup.model.PlayerDetail;
import com.javacup.model.trajectory.AbstractTrajectory;
import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides game state information to tactics for decision-making.
 * <p>
 * This class acts as an information provider, giving tactics everything they need
 * to know about the current game state without allowing modifications. It provides:
 * <ul>
 *   <li>Player positions (own and opponent)</li>
 *   <li>Ball position and altitude</li>
 *   <li>Score and iteration count</li>
 *   <li>Player capabilities (can kick, energy, acceleration)</li>
 *   <li>Trajectory predictions</li>
 *   <li>Distance calculations for planning</li>
 * </ul>
 * </p>
 * <p>
 * The data is presented from the perspective of "my team" vs "rival team",
 * allowing the same tactic implementation to work for both sides.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Slf4j
public final class GameSituations {

    // Ball state
    private Position ball = new Position();
    private double ballAltitude;
    
    // Score and iteration
    private int myGoals;
    private int rivalGoals;
    private int iteration;
    private int realIteration;
    
    // Player positions
    private Position[] myPlayers;
    private Position[] rivalPlayers;
    
    // Kickoff state
    private boolean starts;
    private boolean rivalStarts;
    
    // Temporary list for collecting indices
    private final List<Integer> tempIndices = new ArrayList<>(11);
    
    // Player details
    private PlayerDetail[][] players;
    
    // Kick cooldown timers
    private final int[][] iterationsToKick = new int[2][11];
    private final boolean[] canKick = new boolean[11];
    private final boolean[] rivalCanKick = new boolean[11];
    
    // Energy and acceleration
    private double[] myEnergy = new double[11];
    private double[] rivalEnergy = new double[11];
    private Acceleration[] myAcceleration = new Acceleration[11];
    private Acceleration[] rivalAcceleration = new Acceleration[11];
    
    // Trajectory data
    private AbstractTrajectory trajectory;
    private double trajectoryX0;
    private double trajectoryY0;
    private double trajectoryT0;
    private double trajectoryAngle0;
    private boolean invertCoordinates;
    
    // Offside tracking
    private final boolean[] offsidePlayers = new boolean[11];

    /**
     * Creates a new game situations instance.
     * <p>
     * Initializes all arrays and sets default values.
     * </p>
     */
    public GameSituations() {
        myPlayers = new Position[11];
        rivalPlayers = new Position[11];
        
        for (int i = 0; i < 11; i++) {
            myPlayers[i] = new Position();
            rivalPlayers[i] = new Position();
            iterationsToKick[0][i] = 0;
            iterationsToKick[1][i] = 0;
            canKick[i] = false;
            rivalCanKick[i] = false;
            myEnergy[i] = 1.0;
            rivalEnergy[i] = 1.0;
        }
    }

    // ========================================================================
    // Public Query Methods - Used by Tactics
    // ========================================================================

    /**
     * Gets positions of all my players.
     *
     * @return array of 11 player positions
     */
    public Position[] myPlayers() {
        return myPlayers;
    }

    /**
     * Gets positions of all rival players.
     *
     * @return array of 11 rival positions
     */
    public Position[] rivalPlayers() {
        return rivalPlayers;
    }

    /**
     * Gets the ball's current position.
     *
     * @return ball position
     */
    public Position ballPosition() {
        return ball;
    }

    /**
     * Gets the ball's current altitude above ground.
     *
     * @return altitude in meters (0 = on ground)
     */
    public double ballAltitude() {
        return ballAltitude;
    }

    /**
     * Gets my team's current score.
     *
     * @return number of goals scored
     */
    public int myGoals() {
        return myGoals;
    }

    /**
     * Gets the rival team's current score.
     *
     * @return number of goals scored by rival
     */
    public int rivalGoals() {
        return rivalGoals;
    }

    /**
     * Gets the current iteration number.
     *
     * @return iteration (0-3599)
     */
    public int iteration() {
        return iteration;
    }

    /**
     * Checks if my team is taking a kickoff.
     *
     * @return true if my team kicks off
     */
    public boolean isStarts() {
        return starts;
    }

    /**
     * Checks if rival team is taking a kickoff.
     *
     * @return true if rival kicks off
     */
    public boolean isRivalStarts() {
        return rivalStarts;
    }

    /**
     * Gets indices of my players who can kick right now.
     *
     * @return array of player indices
     */
    public int[] canKick() {
        tempIndices.clear();
        for (int i = 0; i < 11; i++) {
            if (canKick[i]) {
                tempIndices.add(i);
            }
        }
        return tempIndices.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Gets indices of rival players who can kick right now.
     *
     * @return array of player indices
     */
    public int[] rivalCanKick() {
        tempIndices.clear();
        for (int i = 0; i < 11; i++) {
            if (rivalCanKick[i]) {
                tempIndices.add(i);
            }
        }
        return tempIndices.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Gets iterations remaining until each of my players can kick.
     *
     * @return array of iteration counts (0 = can kick now)
     */
    public int[] iterationsToKick() {
        return iterationsToKick[0];
    }

    /**
     * Gets iterations remaining until each rival player can kick.
     *
     * @return array of iteration counts
     */
    public int[] rivalIterationsToKick() {
        return iterationsToKick[1];
    }

    /**
     * Gets configuration details for all my players.
     *
     * @return array of player details
     */
    public PlayerDetail[] myPlayersDetail() {
        return players[0];
    }

    /**
     * Gets configuration details for all rival players.
     *
     * @return array of player details
     */
    public PlayerDetail[] rivalPlayersDetail() {
        return players[1];
    }

    /**
     * Gets a specific player's speed attribute.
     *
     * @param playerIndex player index (0-10)
     * @return speed factor (0.0-1.0)
     */
    public double getMyPlayerSpeed(int playerIndex) {
        return myPlayersDetail()[playerIndex].getSpeed();
    }

    /**
     * Gets a specific player's kick power attribute.
     *
     * @param playerIndex player index (0-10)
     * @return power factor (0.0-1.0)
     */
    public double getMyPlayerPower(int playerIndex) {
        return myPlayersDetail()[playerIndex].getPower();
    }

    /**
     * Gets a specific player's precision attribute.
     *
     * @param playerIndex player index (0-10)
     * @return precision factor (0.0-1.0)
     */
    public double getMyPlayerError(int playerIndex) {
        return myPlayersDetail()[playerIndex].getPrecision();
    }

    /**
     * Gets a specific rival's speed attribute.
     *
     * @param playerIndex player index (0-10)
     * @return speed factor (0.0-1.0)
     */
    public double getRivalPlayerSpeed(int playerIndex) {
        return rivalPlayersDetail()[playerIndex].getSpeed();
    }

    /**
     * Gets a specific rival's kick power attribute.
     *
     * @param playerIndex player index (0-10)
     * @return power factor (0.0-1.0)
     */
    public double getRivalPlayerPower(int playerIndex) {
        return rivalPlayersDetail()[playerIndex].getPower();
    }

    /**
     * Gets a specific rival's precision attribute.
     *
     * @param playerIndex player index (0-10)
     * @return precision factor (0.0-1.0)
     */
    public double getRivalPlayerError(int playerIndex) {
        return rivalPlayersDetail()[playerIndex].getPrecision();
    }

    /**
     * Gets a player's current energy level.
     *
     * @param playerIndex player index (0-10)
     * @return energy level (0.55-1.0)
     */
    public double getMyPlayerEnergy(int playerIndex) {
        return myEnergy[playerIndex];
    }

    /**
     * Gets a rival's current energy level.
     *
     * @param playerIndex player index (0-10)
     * @return energy level (0.55-1.0)
     */
    public double getRivalEnergy(int playerIndex) {
        return rivalEnergy[playerIndex];
    }

    /**
     * Gets a player's current acceleration factor.
     * <p>
     * This represents the movement penalty from direction changes.
     * </p>
     *
     * @param playerIndex player index (0-10)
     * @return acceleration factor (0.7-1.0)
     */
    public double getMyPlayerAceleration(int playerIndex) {
        return myAcceleration[playerIndex].getGlobalAcceleration();
    }

    /**
     * Gets a rival's current acceleration factor.
     *
     * @param playerIndex player index (0-10)
     * @return acceleration factor (0.7-1.0)
     */
    public double getRivalAceleration(int playerIndex) {
        return rivalAcceleration[playerIndex].getAccelerationX() * 
               rivalAcceleration[playerIndex].getAccelerationY();
    }

    /**
     * Predicts ball position at a future iteration.
     * <p>
     * This is critical for AI planning: "Where will the ball be in N iterations?"
     * Uses physics trajectory calculations accounting for air resistance, gravity, and bounces.
     * </p>
     *
     * @param futureIteration future iteration relative to current
     * @return array [x, y, z] with predicted position in meters
     */
    public double[] getTrajectory(int futureIteration) {
        double time = (futureIteration + this.iteration - trajectoryT0) / 60.0;
        double radius = trajectory.getX(time) * Constants.TRAJECTORY_VELOCITY_AMPLIFIER;
        double z = trajectory.getY(time) * Constants.TRAJECTORY_VELOCITY_AMPLIFIER * 2;
        double x = trajectoryX0 + radius * Math.cos(trajectoryAngle0);
        double y = trajectoryY0 + radius * Math.sin(trajectoryAngle0);
        
        if (invertCoordinates) {
            x = -x;
            y = -y;
        }
        
        return new double[]{x, y, z};
    }

    /**
     * Calculates distance a player will travel in a specific future iteration.
     * <p>
     * Accounts for:
     * <ul>
     *   <li>Acceleration ramping up over time</li>
     *   <li>Energy decreasing over time</li>
     *   <li>Sprint multiplier if applicable</li>
     * </ul>
     * Returns distance for that specific iteration, not cumulative.
     * </p>
     *
     * @param playerIndex player index (0-10)
     * @param iterationOffset iterations in the future
     * @param isSprint true if player will be sprinting
     * @return distance in meters for that iteration
     */
    public double distanceIter(int playerIndex, int iterationOffset, boolean isSprint) {
        // Calculate acceleration at future iteration
        double accelIncrement = Constants.ACCELERATION_INCREMENT * iterationOffset;
        
        double accelX = myAcceleration[playerIndex].getAccelerationX() + accelIncrement;
        double accelY = myAcceleration[playerIndex].getAccelerationY() + accelIncrement;
        
        // Clamp to maximum
        accelX = Math.min(accelX, 1.0);
        accelY = Math.min(accelY, 1.0);
        
        double accelFactor = accelX * accelY;
        
        // Calculate energy at future iteration (passive recovery)
        double energyReduction = iterationOffset * Constants.ENERGY_RECOVERY_RATE;
        double energy = myEnergy[playerIndex] + energyReduction;
        
        // Clamp energy to valid range
        energy = Math.max(Constants.MIN_ENERGY, Math.min(Constants.MAX_ENERGY, energy));
        
        // Sprint multiplier if conditions are met
        double sprintMultiplier = (isSprint && energy > Constants.MIN_SPRINT_ENERGY) ? 
                                  Constants.SPRINT_MULTIPLIER : 1.0;
        
        // Calculate distance
        return Constants.getSpeed(myPlayersDetail()[playerIndex].getSpeed()) * 
               energy * 
               accelFactor * 
               sprintMultiplier;
    }

    /**
     * Calculates total distance a player can travel over multiple iterations.
     * <p>
     * Sums up distance from each iteration without sprint.
     * Used to answer: "Can this player reach that position in time?"
     * </p>
     * <p>
     * <strong>Note:</strong> This is an approximation as it doesn't account for
     * energy spent during sprint or actual movement.
     * </p>
     *
     * @param playerIndex player index (0-10)
     * @param iterations number of future iterations
     * @return total distance in meters
     */
    public double distanceTotal(int playerIndex, int iterations) {
        double totalDistance = 0;
        for (int i = 0; i < iterations; i++) {
            totalDistance += distanceIter(playerIndex, i, false);
        }
        return totalDistance;
    }

    /**
     * Gets offside status for each of my players.
     * <p>
     * Returns array where true indicates player is currently offside.
     * </p>
     *
     * @return array of offside flags
     */
    public boolean[] getOffSidePlayers() {
        calculateOffSidePlayers();
        return offsidePlayers;
    }

    /**
     * Attempts to predict which players can intercept the ball.
     * <p>
     * <strong>DEPRECATED:</strong> This method doesn't account for player blocking
     * and may give incorrect results. Use with caution.
     * </p>
     *
     * @return array where first element is iteration of interception,
     *         followed by player indices sorted by proximity
     * @deprecated Does not account for player blocking
     */
    @Deprecated
    public int[] getRecoveryBall() {
        int iterationCount = 0;
        boolean found = false;
        Position playerPos;
        double requiredDistance;
        double actualDistance;
        int foundIteration = -1;
        LinkedList<Double> foundPlayers = new LinkedList<>();
        PlayerDetail[] details = myPlayersDetail();
        
        while (!found) {
            double[] ballPos = getTrajectory(iterationCount);
            
            // Check if ball is out of bounds
            if (!(new Position(ballPos[0], ballPos[1])).isInsideGameField(2)) {
                return new int[]{};
            }
            
            // Check if ball is at controllable height
            if (ballPos[2] <= Constants.GOAL_HEIGHT) {
                for (int i = 0; i < myPlayers.length; i++) {
                    double maxHeight = details[i].isGoalKeeper() ? 
                                      Constants.GOAL_HEIGHT : 
                                      Constants.BALL_CONTROL_HEIGHT;
                    
                    if (ballPos[2] <= maxHeight) {
                        playerPos = myPlayers[i];
                        requiredDistance = distanceTotal(i, iterationCount);
                        actualDistance = playerPos.distance(new Position(ballPos[0], ballPos[1]));
                        
                        if (requiredDistance >= actualDistance) {
                            found = true;
                            foundPlayers.add(actualDistance);
                            foundPlayers.add((double) i);
                            foundIteration = iterationCount;
                        }
                    }
                }
            }
            iterationCount++;
        }
        
        // Sort by distance (bubble sort)
        for (int i = 2; i < foundPlayers.size(); i += 2) {
            for (int j = 0; j < i; j += 2) {
                if (foundPlayers.get(i) < foundPlayers.get(j)) {
                    double tempDist = foundPlayers.get(i);
                    double tempIdx = foundPlayers.get(i + 1);
                    foundPlayers.set(i, foundPlayers.get(j));
                    foundPlayers.set(i + 1, foundPlayers.get(j + 1));
                    foundPlayers.set(j, tempDist);
                    foundPlayers.set(j + 1, tempIdx);
                }
            }
        }
        
        // Remove distances, keep only indices
        for (int i = foundPlayers.size() - 1; i >= 0; i -= 2) {
            foundPlayers.remove(i - 1);
        }
        
        // Add iteration as first element
        foundPlayers.add(0, (double) foundIteration);
        
        // Convert to int array
        int[] result = new int[foundPlayers.size()];
        for (int i = 0; i < foundPlayers.size(); i++) {
            result[i] = foundPlayers.get(i).intValue();
        }
        
        return result;
    }

    // ========================================================================
    // Protected Update Methods - Called by Match Engine
    // ========================================================================

    /**
     * Updates which players can kick.
     *
     * @param myCanKick array of my players' kick availability
     * @param rivalCanKick array of rival players' kick availability
     */
    protected void set(boolean[] myCanKick, boolean[] rivalCanKick) {
        System.arraycopy(myCanKick, 0, this.canKick, 0, 11);
        System.arraycopy(rivalCanKick, 0, this.rivalCanKick, 0, 11);
    }

    /**
     * Updates complete game state.
     * <p>
     * Called by the match engine each iteration before passing to tactics.
     * </p>
     *
     * @param ball ball position
     * @param ballAltitude ball height
     * @param myGoals my team's score
     * @param rivalGoals rival team's score
     * @param iteration current iteration
     * @param myPlayers my player positions
     * @param rivalPlayers rival player positions
     * @param myAcceleration my players' acceleration
     * @param rivalAcceleration rival players' acceleration
     * @param myEnergy my players' energy
     * @param rivalEnergy rival players' energy
     * @param starts true if I'm taking kickoff
     * @param rivalStarts true if rival is taking kickoff
     * @param myKickCooldown my players' kick cooldown
     * @param rivalKickCooldown rival players' kick cooldown
     * @param trajectory current ball trajectory
     * @param x0 trajectory origin X
     * @param y0 trajectory origin Y
     * @param t0 trajectory start time
     * @param angle0 trajectory angle
     * @param realIter real iteration count
     * @param invert whether to invert coordinates
     */
    protected void set(Position ball, double ballAltitude, int myGoals, int rivalGoals,
                      int iteration, Position[] myPlayers, Position[] rivalPlayers,
                      Acceleration[] myAcceleration, Acceleration[] rivalAcceleration,
                      double[] myEnergy, double[] rivalEnergy, boolean starts, boolean rivalStarts,
                      int[] myKickCooldown, int[] rivalKickCooldown,
                      AbstractTrajectory trajectory, double x0, double y0, double t0,
                      double angle0, int realIter, boolean invert) {
        
        this.ball = ball;
        this.myGoals = myGoals;
        this.rivalGoals = rivalGoals;
        this.iteration = iteration;
        this.ballAltitude = ballAltitude;
        
        for (int i = 0; i < 11; i++) {
            this.myPlayers[i] = myPlayers[i];
            this.rivalPlayers[i] = rivalPlayers[i];
            this.iterationsToKick[0][i] = myKickCooldown[i];
            this.iterationsToKick[1][i] = rivalKickCooldown[i];
            this.myAcceleration[i] = myAcceleration[i];
            this.rivalAcceleration[i] = rivalAcceleration[i];
        }
        
        this.myEnergy = myEnergy;
        this.rivalEnergy = rivalEnergy;
        this.starts = starts;
        this.rivalStarts = rivalStarts;
        this.trajectory = trajectory;
        this.trajectoryX0 = x0;
        this.trajectoryY0 = y0;
        this.trajectoryT0 = t0;
        this.trajectoryAngle0 = angle0;
        this.realIteration = realIter;
        this.invertCoordinates = invert;
        
        log.trace("Updated game situations: iteration={}, score={}:{}, ball={}", 
                 iteration, myGoals, rivalGoals, ball);
    }

    /**
     * Sets player detail configurations.
     *
     * @param players array where [0] = my team, [1] = rival team
     */
    protected void set(PlayerDetail[][] players) {
        this.players = players;
    }

    // ========================================================================
    // Private Helper Methods
    // ========================================================================

    /**
     * Calculates which players are currently offside.
     * <p>
     * Offside rule: A player is offside if they are:
     * <ul>
     *   <li>In the opponent's half of the field</li>
     *   <li>Ahead of the second-to-last defender</li>
     *   <li>Ahead of the ball</li>
     * </ul>
     * </p>
     */
    private void calculateOffSidePlayers() {
        Position[] myPositions = myPlayers();
        Position[] rivalPositions = rivalPlayers();
        
        // Find second-to-last defender position
        Position offsideLine = ball;
        Position lastDefenderPos = ball;
        
        for (int i = 0; i < 11; i++) {
            if (lastDefenderPos.getY() < rivalPositions[i].getY()) {
                offsideLine = lastDefenderPos;
                lastDefenderPos = rivalPositions[i];
            } else if (offsideLine.getY() < rivalPositions[i].getY()) {
                offsideLine = rivalPositions[i];
            }
        }
        
        if (offsideLine == null) {
            offsideLine = ball;
        }
        
        // Offside cannot occur in own half
        if (offsideLine.getY() < 0) {
            offsideLine = new Position(0, 0);
        }
        
        // Mark players who are offside
        for (int i = 0; i < 11; i++) {
            offsidePlayers[i] = (myPositions[i].getY() > offsideLine.getY());
        }
    }
}
