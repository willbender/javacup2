package com.javacup.model.command;

import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;
import lombok.Getter;

/**
 * Command that directs a player to kick the ball.
 * <p>
 * This command supports three kicking modes:
 * <ol>
 *   <li><strong>Dribble forward:</strong> Player advances with ball under control</li>
 *   <li><strong>Kick to coordinate:</strong> Kick toward a specific field position</li>
 *   <li><strong>Kick at angle:</strong> Kick in a specific direction (0-360 degrees)</li>
 * </ol>
 * </p>
 * <p>
 * <strong>Kick Execution Requirements:</strong>
 * <ul>
 *   <li>Player must be within 1.0m of the ball</li>
 *   <li>Ball must be below 2.0m height (5.0m for goalkeeper)</li>
 *   <li>Cooldown timer must have expired (30 iterations since last kick)</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Parameters:</strong>
 * <ul>
 *   <li><code>power</code>: 0.0 to 1.0 (percentage of player's max power)</li>
 *   <li><code>angle</code>: 0° to 360° (0° = right, 90° = forward, 180° = left, 270° = backward)</li>
 *   <li><code>verticalAngle</code>: 0° to 60° (0° = ground kick, 60° = steep lob)</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
public final class CommandHitBall extends Command {

    /**
     * Index of the player that will execute this command (0-10).
     */
    private final int playerIndex;
    
    /**
     * Target position for coordinate-based kicks.
     */
    private final Position destiny;
    
    /**
     * Kick direction angle for angle-based kicks (0-360 degrees).
     */
    private final double angle;
    
    /**
     * Kick power as a percentage of player's maximum power (0.0-1.0).
     */
    private final double hitPower;
    
    /**
     * Vertical launch angle in degrees (0-60).
     * 0° = ground kick, higher values create aerial trajectories.
     */
    private final double verticalAngle;
    
    /**
     * True if kick direction is specified by angle, false if by coordinates.
     */
    private final boolean isAngle;
    
    /**
     * True if kick destination is specified by coordinates, false if by angle.
     */
    private final boolean isCoordinate;
    
    /**
     * True if this is a dribble-forward command (player advances with ball).
     */
    private final boolean isForwardBall;

    /**
     * Creates a dribble-forward command.
     * <p>
     * The player advances with the ball under control instead of kicking it away.
     * </p>
     *
     * @param playerIndex index of the player (0-10)
     */
    public CommandHitBall(int playerIndex) {
        this.playerIndex = playerIndex;
        this.destiny = new Position();
        this.angle = 0;
        this.hitPower = 0;
        this.verticalAngle = 0;
        this.isAngle = false;
        this.isCoordinate = false;
        this.isForwardBall = true;
    }

    /**
     * Creates a kick-to-coordinate command with optional high kick.
     *
     * @param playerIndex index of the player (0-10)
     * @param destiny target position on the field
     * @param power kick power (0.0-1.0, clamped automatically)
     * @param highKick true for high kick (30° vertical angle), false for ground kick
     */
    public CommandHitBall(int playerIndex, Position destiny, double power, boolean highKick) {
        this.playerIndex = playerIndex;
        this.destiny = new Position(destiny);
        this.angle = 0;
        this.hitPower = Math.max(0, Math.min(1, power));
        this.verticalAngle = highKick ? Constants.VERTICAL_ANGLE : 0;
        this.isAngle = false;
        this.isCoordinate = true;
        this.isForwardBall = false;
    }

    /**
     * Creates a kick-to-coordinate command with custom vertical angle.
     *
     * @param playerIndex index of the player (0-10)
     * @param destiny target position on the field
     * @param power kick power (0.0-1.0, clamped automatically)
     * @param verticalAngle vertical launch angle in degrees (0-60, clamped automatically)
     */
    public CommandHitBall(int playerIndex, Position destiny, double power, double verticalAngle) {
        this.playerIndex = playerIndex;
        this.destiny = new Position(destiny);
        this.angle = 0;
        this.hitPower = Math.max(0, Math.min(1, power));
        this.verticalAngle = Math.max(0, Math.min(Constants.MAX_VERTICAL_ANGLE, verticalAngle));
        this.isAngle = false;
        this.isCoordinate = true;
        this.isForwardBall = false;
    }

    /**
     * Creates a kick-at-angle command with optional high kick.
     *
     * @param playerIndex index of the player (0-10)
     * @param angle kick direction angle in degrees (0-360)
     * @param power kick power (0.0-1.0, clamped automatically)
     * @param highKick true for high kick (30° vertical angle), false for ground kick
     */
    public CommandHitBall(int playerIndex, double angle, double power, boolean highKick) {
        this.playerIndex = playerIndex;
        this.destiny = new Position();
        this.angle = angle;
        this.hitPower = Math.max(0, Math.min(1, power));
        this.verticalAngle = highKick ? Constants.VERTICAL_ANGLE : 0;
        this.isAngle = true;
        this.isCoordinate = false;
        this.isForwardBall = false;
    }

    /**
     * Creates a kick-at-angle command with custom vertical angle.
     *
     * @param playerIndex index of the player (0-10)
     * @param angle kick direction angle in degrees (0-360)
     * @param power kick power (0.0-1.0, clamped automatically)
     * @param verticalAngle vertical launch angle in degrees (0-60, clamped automatically)
     */
    public CommandHitBall(int playerIndex, double angle, double power, double verticalAngle) {
        this.playerIndex = playerIndex;
        this.destiny = new Position();
        this.angle = angle;
        this.hitPower = Math.max(0, Math.min(1, power));
        this.verticalAngle = Math.max(0, Math.min(Constants.MAX_VERTICAL_ANGLE, verticalAngle));
        this.isAngle = true;
        this.isCoordinate = false;
        this.isForwardBall = false;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.HIT_BALL;
    }
    
    @Override
    public String toString() {
        if (isForwardBall) {
            return String.format("HitBall(player=%d, mode=dribble)", playerIndex);
        } else if (isCoordinate) {
            return String.format("HitBall(player=%d, dest=%s, power=%.2f, vertAngle=%.1f°)", 
                playerIndex, destiny, hitPower, verticalAngle);
        } else {
            return String.format("HitBall(player=%d, angle=%.1f°, power=%.2f, vertAngle=%.1f°)", 
                playerIndex, angle, hitPower, verticalAngle);
        }
    }
}
