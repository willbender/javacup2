package com.javacup.model.engine;

import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Models player acceleration physics during direction changes.
 * <p>
 * This class implements realistic momentum physics where players cannot instantly
 * change direction at full speed. When a player changes direction:
 * <ul>
 *   <li>Acceleration drops to minimum (0.7 for Y-axis, 0.9 for X-axis)</li>
 *   <li>Gradually recovers toward 1.0 if direction is maintained</li>
 *   <li>Recovery rate: 0.04 per iteration</li>
 * </ul>
 * This creates realistic turning physics where players must slow down when changing direction.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Slf4j
@Getter
public class Acceleration {

    /**
     * Current acceleration factor for X-axis (0.0-1.0).
     * <p>
     * 1.0 = full speed, lower values = reduced speed due to direction change.
     * </p>
     */
    private double accelerationX;
    
    /**
     * Current acceleration factor for Y-axis (0.0-1.0).
     */
    private double accelerationY;
    
    /**
     * Previous position used to calculate direction changes.
     */
    private Position previousPosition;
    
    /**
     * Previous movement direction on X-axis (-1, 0, or 1).
     */
    private double previousDirectionX;
    
    /**
     * Previous movement direction on Y-axis (-1, 0, or 1).
     */
    private double previousDirectionY;

    /**
     * Creates acceleration tracker with full acceleration (1.0).
     */
    public Acceleration() {
        this.accelerationX = 1.0;
        this.accelerationY = 1.0;
        this.previousPosition = new Position();
        this.previousDirectionX = 0;
        this.previousDirectionY = 0;
    }

    /**
     * Creates acceleration tracker with specified initial values.
     *
     * @param accelerationX initial X-axis acceleration (0.0-1.0)
     * @param accelerationY initial Y-axis acceleration (0.0-1.0)
     */
    public Acceleration(double accelerationX, double accelerationY) {
        this.accelerationX = Math.max(0, Math.min(1, accelerationX));
        this.accelerationY = Math.max(0, Math.min(1, accelerationY));
        this.previousPosition = new Position();
        this.previousDirectionX = 0;
        this.previousDirectionY = 0;
    }

    /**
     * Updates acceleration based on current position.
     * <p>
     * Compares current position to previous position to determine if direction
     * changed. If direction changed, reduces acceleration. If maintained,
     * gradually increases acceleration toward 1.0.
     * </p>
     *
     * @param currentPosition player's current position
     */
    public void update(Position currentPosition) {
        // Calculate movement delta
        double dx = currentPosition.getX() - previousPosition.getX();
        double dy = currentPosition.getY() - previousPosition.getY();
        
        // Calculate current direction (-1, 0, or 1)
        double currentDirectionX = (dx == 0) ? 0 : Math.signum(dx);
        double currentDirectionY = (dy == 0) ? 0 : Math.signum(dy);
        
        // Update X-axis acceleration
        if (currentDirectionX != previousDirectionX) {
            // Direction changed: drop to minimum
            accelerationX = Constants.MIN_ACCELERATION_X;
            log.trace("X-axis direction changed, acceleration reset to {}", accelerationX);
        } else {
            // Direction maintained: gradually recover
            accelerationX += Constants.ACCELERATION_INCREMENT;
            if (accelerationX > 1.0) {
                accelerationX = 1.0;
            }
        }
        
        // Update Y-axis acceleration
        if (currentDirectionY != previousDirectionY) {
            // Direction changed: drop to minimum
            accelerationY = Constants.MIN_ACCELERATION_Y;
            log.trace("Y-axis direction changed, acceleration reset to {}", accelerationY);
        } else {
            // Direction maintained: gradually recover
            accelerationY += Constants.ACCELERATION_INCREMENT;
            if (accelerationY > 1.0) {
                accelerationY = 1.0;
            }
        }
        
        // Store current state for next update
        previousPosition = new Position(currentPosition);
        previousDirectionX = currentDirectionX;
        previousDirectionY = currentDirectionY;
    }

    /**
     * Gets the combined acceleration factor from both axes.
     * <p>
     * This is the product of X and Y acceleration factors, representing
     * the overall movement penalty from direction changes.
     * </p>
     *
     * @return combined acceleration factor (0.0-1.0)
     */
    public double getGlobalAcceleration() {
        return accelerationX * accelerationY;
    }

    /**
     * Resets acceleration to maximum values.
     */
    public void reset() {
        accelerationX = 1.0;
        accelerationY = 1.0;
        previousDirectionX = 0;
        previousDirectionY = 0;
        log.debug("Acceleration reset to maximum");
    }
}
