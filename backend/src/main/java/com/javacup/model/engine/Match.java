package com.javacup.model.engine;

import com.javacup.model.Tactic;
import com.javacup.model.TacticDetail;
import com.javacup.model.PlayerDetail;
import com.javacup.model.command.Command;
import com.javacup.model.command.CommandHitBall;
import com.javacup.model.command.CommandMoveTo;
import com.javacup.model.trajectory.AbstractTrajectory;
import com.javacup.model.trajectory.AirTrajectory;
import com.javacup.model.trajectory.FloorTrajectory;
import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;
import com.javacup.model.util.TacticValidate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main match simulation engine.
 * <p>
 * This is the core class that executes football matches at 60 iterations per second.
 * It handles:
 * <ul>
 *   <li>Ball physics (trajectories, bounces, goals)</li>
 *   <li>Player movement with acceleration and energy</li>
 *   <li>Command processing from tactics</li>
 *   <li>Event detection (goals, fouls, offsides)</li>
 *   <li>Game state management (kickoffs, set pieces)</li>
 *   <li>Match recording for replays</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Match Flow:</strong>
 * <ol>
 *   <li>Constructor: Validate tactics, initialize positions</li>
 *   <li>Call iterate() 3600 times (60 seconds at 60 FPS)</li>
 *   <li>Each iteration: update physics → get commands → move players → detect events</li>
 *   <li>Query state for rendering/display</li>
 * </ol>
 * </p>
 * <p>
 * <strong>Note:</strong> This is a simplified initial implementation focusing on
 * core functionality. Full match logic with all edge cases will be added incrementally.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Slf4j
@Getter
public final class Match implements MatchInterface {

    // Teams and tactics
    private final Tactic homeTactic;
    private final Tactic awayTactic;
    private final GameSituations homeGameState = new GameSituations();
    private final GameSituations awayGameState = new GameSituations();
    
    // Ball state
    private Position ball = new Position(Constants.FIELD_CENTER);
    private double ballVelocityX = 0;
    private double ballVelocityY = 0;
    private double ballVelocityZ = 0;
    private double ballAltitude = 0;
    private Position visibleBall = new Position(Constants.FIELD_CENTER);
    
    // Player positions
    private Position[] homePositions;
    private Position[] awayPositions;
    private Position[] homePositionsInverted;
    private Position[] awayPositionsInverted;
    
    // Player physics
    private Acceleration[] homeAcceleration;
    private Acceleration[] awayAcceleration;
    private double[] homeEnergy;
    private double[] awayEnergy;
    private double[][] playerDirection = new double[2][11];
    
    // Score and iteration
    private int homeGoals = 0;
    private int awayGoals = 0;
    private int iteration = 0;
    private int realIteration = 0;
    
    // Event flags
    private boolean goal = false;
    private boolean goalpost = false;
    private boolean bouncing = false;
    private boolean whistling = false;
    private boolean kicking = false;
    private boolean cheering = false;
    private boolean setPieceChanged = false;
    private boolean homeKicksOff = false;
    private boolean awayKicksOff = false;
    
    // Kick cooldown tracking
    private final int[][] kickCooldown = new int[2][11];
    private final boolean[][] canKick = new boolean[2][11];
    private int lastKickingTeam = -1;
    private int lastKickingPlayer = -1;
    
    // Game state
    private int matchState = 0;
    private boolean homeStarts = true;
    
    // Trajectory tracking
    private AbstractTrajectory trajectory;
    private double trajectoryX0;
    private double trajectoryY0;
    private double trajectoryT0;
    private double trajectoryAngle0;
    
    // Set pieces and offside
    private boolean offside = false;
    private boolean indirectFreeKick = false;
    private Position[][] kickoffPositions = null;
    
    // Recording
    private final boolean shouldRecord;
    private SavedMatch savedMatch = null;
    
    // Random for physics variation
    private final Random random = new Random();
    
    // Constants for game states
    private static final int STATE_INITIAL = 0;
    private static final int STATE_PHOTO = 2;
    private static final int STATE_LINEUP = 3;
    private static final int STATE_PLAYING = 4;
    private static final int STATE_GOAL = 5;
    
    // Set piece types
    private static final int NO_SET_PIECE = 0;
    private static final int SET_PIECE_GOAL_KICK = 1;
    private static final int SET_PIECE_CORNER = 2;
    private static final int SET_PIECE_THROW_IN = 3;
    
    private int currentSetPiece = NO_SET_PIECE;
    
    // Ball possession tracking
    private int homePossessionPoints = 0;
    private int awayPossessionPoints = 0;

    /**
     * Creates a new match between two tactics.
     * <p>
     * Validates both tactics, initializes all arrays and physics state,
     * and performs the first iteration.
     * </p>
     *
     * @param homeTactic home team's AI tactic
     * @param awayTactic away team's AI tactic
     * @param shouldRecord true to record match for replay
     * @throws Exception if tactics are invalid
     */
    public Match(Tactic homeTactic, Tactic awayTactic, boolean shouldRecord) throws Exception {
        log.info("Creating new match: {} vs {}", 
                homeTactic.getDetail().getTacticName(),
                awayTactic.getDetail().getTacticName());
        
        // Wrap tactics with immutable implementations
        this.homeTactic = new TacticImpl(homeTactic);
        this.awayTactic = new TacticImpl(awayTactic);
        this.shouldRecord = shouldRecord;
        
        // Validate tactics
        TacticValidate.validateDetail("Home team", homeTactic.getDetail());
        TacticValidate.validateDetail("Away team", awayTactic.getDetail());
        
        // Initialize arrays
        initializeArrays();
        
        // Initialize positions
        initializePositions();
        
        // Store player details in game states
        homeGameState.set(new PlayerDetail[][]{
            homeTactic.getDetail().getPlayers(),
            awayTactic.getDetail().getPlayers()
        });
        awayGameState.set(new PlayerDetail[][]{
            awayTactic.getDetail().getPlayers(),
            homeTactic.getDetail().getPlayers()
        });
        
        // Initialize ball trajectory
        trajectory = new FloorTrajectory(0, 0);
        trajectoryX0 = 0;
        trajectoryY0 = 0;
        trajectoryT0 = 0;
        trajectoryAngle0 = 0;
        
        // Create saved match if recording
        if (shouldRecord) {
            savedMatch = new SavedMatch(
                new TacticDetailImpl(homeTactic.getDetail()),
                new TacticDetailImpl(awayTactic.getDetail())
            );
        }
        
        log.info("Match initialized successfully");
        
        // Start match
        iterate();
    }

    /**
     * Initializes all arrays to proper sizes.
     */
    private void initializeArrays() {
        homePositions = new Position[11];
        awayPositions = new Position[11];
        homePositionsInverted = new Position[11];
        awayPositionsInverted = new Position[11];
        
        homeAcceleration = new Acceleration[11];
        awayAcceleration = new Acceleration[11];
        
        homeEnergy = new double[11];
        awayEnergy = new double[11];
        
        for (int i = 0; i < 11; i++) {
            homePositions[i] = new Position();
            awayPositions[i] = new Position();
            homePositionsInverted[i] = new Position();
            awayPositionsInverted[i] = new Position();
            
            homeAcceleration[i] = new Acceleration();
            awayAcceleration[i] = new Acceleration();
            
            homeEnergy[i] = 1.0;
            awayEnergy[i] = 1.0;
            
            playerDirection[0][i] = Math.PI / 2;
            playerDirection[1][i] = Math.PI / 2;
            
            kickCooldown[0][i] = 0;
            kickCooldown[1][i] = 0;
        }
    }

    /**
     * Initializes player positions for kickoff.
     */
    private void initializePositions() throws Exception {
        Position[] homeStart = homeTactic.getStartPositions(homeGameState);
        Position[] homeNoStart = homeTactic.getNoStartPositions(homeGameState);
        Position[] awayStart = awayTactic.getStartPositions(awayGameState);
        Position[] awayNoStart = awayTactic.getNoStartPositions(awayGameState);
        
        // Validate positions
        Position[][] validatedHome = TacticValidate.validatePositions(
            "Home positions", homeStart, homeNoStart);
        Position[][] validatedAway = TacticValidate.validatePositions(
            "Away positions", awayStart, awayNoStart);
        
        kickoffPositions = new Position[][]{
            validatedHome[1],  // Home receiving kickoff positions
            validatedAway[1]   // Away receiving kickoff positions
        };
        
        // Initialize players at their starting positions (home kicks off)
        for (int i = 0; i < 11; i++) {
            homePositions[i] = new Position(homeStart[i]);
            awayPositions[i] = new Position(awayNoStart[i]);
        }
        
        matchState = STATE_PLAYING; // Start playing immediately
        homeKicksOff = true;
    }

    // ========================================================================
    // MatchInterface Implementation
    // ========================================================================

    @Override
    public boolean isGoal() {
        return goal;
    }

    @Override
    public boolean isGoalpost() {
        return goalpost;
    }

    @Override
    public boolean isBouncing() {
        return bouncing;
    }

    @Override
    public boolean isCheering() {
        return cheering;
    }

    @Override
    public boolean isKicking() {
        return kicking;
    }

    @Override
    public boolean isTakingSetPiece() {
        return homeKicksOff || awayKicksOff;
    }

    @Override
    public boolean isWhistling() {
        return whistling;
    }

    @Override
    public double getBallAltitude() {
        return ballAltitude;
    }

    @Override
    public boolean wasRecorded() {
        return shouldRecord;
    }

    @Override
    public boolean isSetPieceChanged() {
        return setPieceChanged;
    }

    @Override
    public TacticDetail getHomeDetail() {
        return homeTactic.getDetail();
    }

    @Override
    public TacticDetail getAwayDetail() {
        return awayTactic.getDetail();
    }

    @Override
    public SavedMatch getSavedMatch() {
        return savedMatch;
    }

    @Override
    public Position getVisibleBallPosition() {
        return visibleBall;
    }

    @Override
    public Position[][] getPositions() {
        Position[][] positions = new Position[3][];
        positions[0] = homePositions;
        positions[1] = awayPositions;
        positions[2] = new Position[]{ball};
        return positions;
    }

    @Override
    public int getHomeGoals() {
        return homeGoals;
    }

    @Override
    public int getAwayGoals() {
        return awayGoals;
    }

    @Override
    public int getIteration() {
        return iteration;
    }

    @Override
    public double getHomePossession() {
        if (homePossessionPoints + awayPossessionPoints == 0) {
            return 0.5;
        }
        return (double) homePossessionPoints / (homePossessionPoints + awayPossessionPoints);
    }

    @Override
    public boolean isOffside() {
        return offside;
    }

    @Override
    public boolean isIndirectFreeKick() {
        return indirectFreeKick;
    }

    // ========================================================================
    // Main Iteration Method
    // ========================================================================

    /**
     * Executes one match iteration (1/60th of a second).
     * <p>
     * This is the heart of the match engine. Each iteration:
     * <ol>
     *   <li>Updates ball physics</li>
     *   <li>Gets commands from tactics</li>
     *   <li>Processes player movements</li>
     *   <li>Handles kicks</li>
     *   <li>Detects goals and events</li>
     *   <li>Updates game state</li>
     * </ol>
     * </p>
     *
     * @throws Exception if an error occurs during iteration
     */
    @Override
    public void iterate() throws Exception {
        // Reset event flags
        goal = false;
        goalpost = false;
        bouncing = false;
        whistling = false;
        kicking = false;
        setPieceChanged = false;
        
        log.debug("Iteration {}: State={}, Score={}:{}", 
                 iteration, matchState, homeGoals, awayGoals);
        
        // Handle different match states
        switch (matchState) {
            case STATE_INITIAL:
                // Entering stadium (could be animated)
                matchState = STATE_LINEUP;
                break;
                
            case STATE_LINEUP:
                // Moving to kickoff positions
                whistling = true;
                matchState = STATE_PLAYING;
                break;
                
            case STATE_PLAYING:
                // Main gameplay
                iteratePlaying();
                break;
                
            case STATE_GOAL:
                // Goal celebration
                handleGoalCelebration();
                break;
        }
        
        // Update iteration counter
        iteration++;
        
        // Update inverted positions
        updateInvertedPositions();
        
        // Update game situations for tactics
        updateGameSituations();
        
        log.trace("Iteration {} complete: ball={}, altitude={}", 
                 iteration, ball, ballAltitude);
    }

    /**
     * Handles main gameplay iteration.
     */
    private void iteratePlaying() throws Exception {
        realIteration++;
        
        // Update ball physics
        updateBallPhysics();
        
        // Update kick cooldowns
        updateKickCooldowns();
        
        // Determine who can kick
        updateCanKick();
        
        // Update game situations
        updateGameSituations();
        
        // Get commands from tactics
        List<Command> homeCommands = homeTactic.execute(homeGameState);
        List<Command> awayCommands = awayTactic.execute(awayGameState);
        
        // Process commands
        processCommands(homeCommands, awayCommands);
        
        // Update player positions and physics
        updatePlayers();
        
        // Check for goals
        checkGoal();
        
        // Update ball possession
        updatePossession();
    }

    /**
     * Updates ball physics based on trajectory.
     */
    private void updateBallPhysics() {
        double time = (iteration - trajectoryT0) / 60.0;
        double radius = trajectory.getX(time) * Constants.TRAJECTORY_VELOCITY_AMPLIFIER;
        ballAltitude = trajectory.getY(time) * Constants.TRAJECTORY_VELOCITY_AMPLIFIER;
        
        ball = new Position(
            trajectoryX0 + radius * Math.cos(trajectoryAngle0),
            trajectoryY0 + radius * Math.sin(trajectoryAngle0)
        );
        
        visibleBall = ball.isInsideGameField(0) ? ball : ball.setInsideGameField();
        
        // Check for bounces
        if (iteration > trajectoryT0 + 1) {
            bouncing = trajectory.isBounce(time - 0.016, time);
        }
    }

    /**
     * Updates kick cooldown timers.
     */
    private void updateKickCooldowns() {
        for (int i = 0; i < 11; i++) {
            if (kickCooldown[0][i] > 0) kickCooldown[0][i]--;
            if (kickCooldown[1][i] > 0) kickCooldown[1][i]--;
        }
    }

    /**
     * Determines which players can kick the ball.
     */
    private void updateCanKick() {
        double ballSpeed = Math.sqrt(ballVelocityX * ballVelocityX + 
                                    ballVelocityY * ballVelocityY);
        double probability = (7.0 - ballSpeed) / 7.0;
        
        for (int i = 0; i < 11; i++) {
            canKick[0][i] = calculateCanKick(homePositions[i], 
                homeTactic.getDetail().getPlayers()[i], kickCooldown[0][i], probability);
            canKick[1][i] = calculateCanKick(awayPositions[i], 
                awayTactic.getDetail().getPlayers()[i], kickCooldown[1][i], probability);
        }
        
        homeGameState.set(canKick[0], canKick[1]);
        awayGameState.set(canKick[1], canKick[0]);
    }

    /**
     * Calculates if a player can kick.
     */
    private boolean calculateCanKick(Position playerPos, PlayerDetail player, 
                                     int cooldown, double probability) {
        if (cooldown > 0) return false;
        
        double maxHeight = player.isGoalKeeper() ? 
                          Constants.GOAL_HEIGHT : Constants.BALL_CONTROL_HEIGHT;
        double maxDistance = player.isGoalKeeper() ?
                            Constants.GOALKEEPER_BALL_CONTROL_DISTANCE : 
                            Constants.BALL_CONTROL_DISTANCE;
        
        return random.nextDouble() < probability &&
               ball.distance(playerPos) <= maxDistance &&
               ballAltitude <= maxHeight;
    }

    /**
     * Processes commands from both teams.
     */
    private void processCommands(List<Command> homeCommands, List<Command> awayCommands) {
        // Process home commands
        if (homeCommands != null) {
            for (Command cmd : homeCommands) {
                if (cmd instanceof CommandMoveTo moveTo) {
                    processMovement(0, moveTo);
                } else if (cmd instanceof CommandHitBall hitBall) {
                    processKick(0, hitBall);
                }
            }
        }
        
        // Process away commands
        if (awayCommands != null) {
            for (Command cmd : awayCommands) {
                if (cmd instanceof CommandMoveTo moveTo) {
                    processMovement(1, moveTo);
                } else if (cmd instanceof CommandHitBall hitBall) {
                    processKick(1, hitBall);
                }
            }
        }
    }

    /**
     * Processes a movement command.
     */
    private void processMovement(int team, CommandMoveTo moveTo) {
        int playerIndex = moveTo.getPlayerIndex();
        if (playerIndex < 0 || playerIndex >= 11) return;
        
        Position[] positions = team == 0 ? homePositions : awayPositions;
        Acceleration[] acceleration = team == 0 ? homeAcceleration : awayAcceleration;
        double[] energy = team == 0 ? homeEnergy : awayEnergy;
        PlayerDetail[] players = team == 0 ? 
            homeTactic.getDetail().getPlayers() : 
            awayTactic.getDetail().getPlayers();
        
        Position target = moveTo.getMoveTo();
        boolean sprint = moveTo.isSprint();
        
        // Calculate movement
        Position newPos = calculateMovement(positions[playerIndex], target, 
            players[playerIndex], acceleration[playerIndex], energy[playerIndex], sprint);
        
        positions[playerIndex] = newPos;
        
        // Update acceleration
        acceleration[playerIndex].update(newPos);
        
        // Update energy
        if (sprint && energy[playerIndex] > Constants.MIN_SPRINT_ENERGY) {
            energy[playerIndex] -= Constants.SPRINT_ENERGY_COST;
        } else {
            energy[playerIndex] += Constants.ENERGY_RECOVERY_RATE;
        }
        energy[playerIndex] = Math.max(Constants.MIN_ENERGY, 
                                       Math.min(Constants.MAX_ENERGY, energy[playerIndex]));
    }

    /**
     * Calculates new player position based on movement.
     */
    private Position calculateMovement(Position current, Position target, 
                                      PlayerDetail player, Acceleration accel, 
                                      double energy, boolean sprint) {
        double angle = current.angle(target);
        double distance = current.distance(target);
        
        double speed = Constants.getSpeed(player.getSpeed());
        double accelFactor = accel.getGlobalAcceleration();
        double sprintMultiplier = sprint && energy > Constants.MIN_SPRINT_ENERGY ? 
                                 Constants.SPRINT_MULTIPLIER : 1.0;
        
        double maxMove = speed * energy * accelFactor * sprintMultiplier;
        double actualMove = Math.min(distance, maxMove);
        
        return current.moveAngle(angle, actualMove);
    }

    /**
     * Processes a kick command (simplified).
     */
    private void processKick(int team, CommandHitBall hitBall) {
        int playerIndex = hitBall.getPlayerIndex();
        if (playerIndex < 0 || playerIndex >= 11) return;
        if (!canKick[team][playerIndex]) return;
        
        kicking = true;
        kickCooldown[team][playerIndex] = Constants.KICK_COOLDOWN_ITERATIONS;
        lastKickingTeam = team;
        lastKickingPlayer = playerIndex;
        
        log.info("Team {} player {} kicks the ball", team, playerIndex);
        
        // Simplified kick physics
        double power = hitBall.getHitPower();
        double angle = hitBall.isAngle() ? hitBall.getAngle() : 0;
        
        if (hitBall.isCoordinate()) {
            Position current = team == 0 ? homePositions[playerIndex] : 
                             awayPositions[playerIndex];
            angle = Math.toDegrees(current.angle(hitBall.getDestiny()));
        }
        
        double velocity = Constants.getKickVelocity(power);
        double vertAngle = Math.toRadians(hitBall.getVerticalAngle());
        
        // Create new trajectory
        ballVelocityX = velocity * Math.cos(Math.toRadians(angle)) * Math.cos(vertAngle);
        ballVelocityY = velocity * Math.sin(Math.toRadians(angle)) * Math.cos(vertAngle);
        ballVelocityZ = velocity * Math.sin(vertAngle);
        
        trajectory = new AirTrajectory(
            Math.sqrt(ballVelocityX * ballVelocityX + ballVelocityY * ballVelocityY),
            ballVelocityZ,
            0,
            ballAltitude
        );
        
        trajectoryX0 = ball.getX();
        trajectoryY0 = ball.getY();
        trajectoryT0 = iteration;
        trajectoryAngle0 = Math.toRadians(angle);
    }

    /**
     * Updates player physics (simplified).
     */
    private void updatePlayers() {
        // Energy recovery
        for (int i = 0; i < 11; i++) {
            homeEnergy[i] = Math.min(Constants.MAX_ENERGY, 
                                    homeEnergy[i] + Constants.ENERGY_RECOVERY_RATE);
            awayEnergy[i] = Math.min(Constants.MAX_ENERGY, 
                                    awayEnergy[i] + Constants.ENERGY_RECOVERY_RATE);
        }
    }

    /**
     * Checks if a goal was scored.
     */
    private void checkGoal() {
        if (Math.abs(ball.getY()) > Constants.FIELD_LENGTH / 2) {
            double goalY = ball.getY() < 0 ? -Constants.FIELD_LENGTH / 2 : 
                          Constants.FIELD_LENGTH / 2;
            
            // Project ball position at goal line
            double projX = (ballVelocityX / ballVelocityY) * (goalY - ball.getY()) + ball.getX();
            double projZ = (ballVelocityZ / ballVelocityY) * (goalY - ball.getY()) + ballAltitude;
            
            if (projZ <= Constants.GOAL_HEIGHT && 
                Math.abs(projX) < Constants.GOAL_WIDTH / 2) {
                // GOAL!
                goal = true;
                cheering = true;
                whistling = true;
                
                if (ball.getY() < 0) {
                    awayGoals++;
                    homeStarts = true;
                    log.info("GOAL! Away scores! {}:{}", homeGoals, awayGoals);
                } else {
                    homeGoals++;
                    homeStarts = false;
                    log.info("GOAL! Home scores! {}:{}", homeGoals, awayGoals);
                }
                
                matchState = STATE_GOAL;
            }
        }
    }

    /**
     * Handles goal celebration and reset.
     */
    private void handleGoalCelebration() {
        // Simplified: immediately reset to kickoff
        ball = new Position(Constants.FIELD_CENTER);
        ballAltitude = 0;
        trajectory = new FloorTrajectory(0, 0);
        trajectoryX0 = 0;
        trajectoryY0 = 0;
        trajectoryT0 = iteration;
        
        matchState = STATE_PLAYING;
    }

    /**
     * Updates ball possession tracking.
     */
    private void updatePossession() {
        double homeMinDist = Double.MAX_VALUE;
        double awayMinDist = Double.MAX_VALUE;
        
        for (int i = 0; i < 11; i++) {
            homeMinDist = Math.min(homeMinDist, ball.distance(homePositions[i]));
            awayMinDist = Math.min(awayMinDist, ball.distance(awayPositions[i]));
        }
        
        if (homeMinDist < awayMinDist) {
            homePossessionPoints++;
        } else {
            awayPossessionPoints++;
        }
    }

    /**
     * Updates inverted positions for away team perspective.
     */
    private void updateInvertedPositions() {
        for (int i = 0; i < 11; i++) {
            homePositionsInverted[i] = homePositions[i].getInvertedPosition();
            awayPositionsInverted[i] = awayPositions[i].getInvertedPosition();
        }
    }

    /**
     * Updates game situations for both tactics.
     */
    private void updateGameSituations() {
        homeGameState.set(ball, ballAltitude, homeGoals, awayGoals, iteration,
            homePositions, awayPositions, homeAcceleration, awayAcceleration,
            homeEnergy, awayEnergy, homeKicksOff, awayKicksOff,
            kickCooldown[0], kickCooldown[1], trajectory, trajectoryX0, trajectoryY0,
            trajectoryT0, trajectoryAngle0, realIteration, false);
        
        awayGameState.set(ball.getInvertedPosition(), ballAltitude, awayGoals, homeGoals, 
            iteration, awayPositionsInverted, homePositionsInverted, awayAcceleration, 
            homeAcceleration, awayEnergy, homeEnergy, awayKicksOff, homeKicksOff,
            kickCooldown[1], kickCooldown[0], trajectory, -trajectoryX0, -trajectoryY0,
            trajectoryT0, trajectoryAngle0 + Math.PI, realIteration, true);
    }
}
