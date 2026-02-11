package com.javacup.model.engine;

import com.javacup.model.util.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Data snapshot of a single match iteration for replay purposes.
 * <p>
 * This class stores all relevant match state for a single iteration,
 * allowing matches to be saved and replayed. Uses compressed storage
 * (16-bit integers with 1/256 precision) to minimize memory footprint.
 * </p>
 * <p>
 * This is an internal class used by the match engine for saving matches.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class Iteration implements Serializable {

    private static final long serialVersionUID = 756445962537556468L;
    
    /**
     * Compression factor for position storage (256 = 1/256 meter precision).
     */
    private static final double COMPRESSION_FACTOR = 256.0;

    // Match events
    private final boolean goal;
    private final boolean goalpost;
    private final boolean bouncing;
    private final boolean cheering;
    private final boolean kicking;
    private final boolean takingSetPiece;
    private final boolean whistling;
    private final boolean setPieceChanged;
    private final boolean offside;
    private final boolean indirectFreeKick;
    
    // Ball state (compressed)
    private final short ballHeight;
    private final short visibleBallX;
    private final short visibleBallY;
    
    // Player positions (compressed: [teams][players][x/y])
    // positions[0] = home team, positions[1] = away team, positions[2][0] = ball
    private final short[][][] positions;
    
    // Match state
    private final int iteration;
    private final int homeGoals;
    private final int awayGoals;
    private final short homePossessionPercent;

    /**
     * Creates an iteration snapshot from current match state.
     * <p>
     * Compresses positions to 16-bit integers with 1/256 meter precision
     * to reduce memory usage for match replays.
     * </p>
     *
     * @param goal true if a goal was scored this iteration
     * @param goalpost true if ball hit goalpost
     * @param bouncing true if ball is bouncing
     * @param cheering true if crowd is cheering
     * @param kicking true if a player is kicking
     * @param takingSetPiece true if a set piece is being taken
     * @param whistling true if referee is whistling
     * @param setPieceChanged true if set piece situation changed
     * @param offside true if offside occurred
     * @param indirectFreeKick true if indirect free kick awarded
     * @param ballHeight ball altitude in meters
     * @param visibleBall visible ball position
     * @param playerPositions positions[0] = home team, positions[1] = away team, positions[2][0] = actual ball
     * @param iteration current iteration number
     * @param homeGoals home team score
     * @param awayGoals away team score
     * @param homePossessionPercent home team ball possession (0.0-1.0)
     */
    public Iteration(boolean goal, boolean goalpost, boolean bouncing, boolean cheering,
                     boolean kicking, boolean takingSetPiece, boolean whistling,
                     boolean setPieceChanged, boolean offside, boolean indirectFreeKick,
                     double ballHeight, Position visibleBall, Position[][] playerPositions,
                     int iteration, int homeGoals, int awayGoals, double homePossessionPercent) {
        
        // Store event flags
        this.goal = goal;
        this.goalpost = goalpost;
        this.bouncing = bouncing;
        this.cheering = cheering;
        this.kicking = kicking;
        this.takingSetPiece = takingSetPiece;
        this.whistling = whistling;
        this.setPieceChanged = setPieceChanged;
        this.offside = offside;
        this.indirectFreeKick = indirectFreeKick;
        
        // Compress ball position and height
        this.ballHeight = compress(ballHeight);
        this.visibleBallX = compress(visibleBall.getX());
        this.visibleBallY = compress(visibleBall.getY());
        
        // Compress player positions
        this.positions = new short[3][][];
        this.positions[0] = new short[11][2]; // Home team
        this.positions[1] = new short[11][2]; // Away team
        this.positions[2] = new short[1][2];  // Ball
        
        for (int i = 0; i < 11; i++) {
            // Home team
            this.positions[0][i][0] = compress(playerPositions[0][i].getX());
            this.positions[0][i][1] = compress(playerPositions[0][i].getY());
            // Away team
            this.positions[1][i][0] = compress(playerPositions[1][i].getX());
            this.positions[1][i][1] = compress(playerPositions[1][i].getY());
        }
        
        // Ball position
        this.positions[2][0][0] = compress(playerPositions[2][0].getX());
        this.positions[2][0][1] = compress(playerPositions[2][0].getY());
        
        // Store match state
        this.iteration = iteration;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.homePossessionPercent = compress(homePossessionPercent);
    }
    
    /**
     * Compresses a double value to a short for storage.
     *
     * @param value value to compress
     * @return compressed value
     */
    private static short compress(double value) {
        return (short) (value * COMPRESSION_FACTOR);
    }
    
    /**
     * Decompresses a short value back to a double.
     *
     * @param compressed compressed value
     * @return decompressed value
     */
    public static double decompress(short compressed) {
        return compressed / COMPRESSION_FACTOR;
    }
}
