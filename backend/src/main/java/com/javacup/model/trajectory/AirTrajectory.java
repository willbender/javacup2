package com.javacup.model.trajectory;

import lombok.Getter;

/**
 * Models aerial ball trajectory with gravity and air resistance.
 * <p>
 * This class implements realistic physics for a ball moving through the air:
 * <ul>
 *   <li><strong>Horizontal motion:</strong> Air resistance causes exponential decay</li>
 *   <li><strong>Vertical motion:</strong> Gravity pulls ball down in parabolic arc</li>
 *   <li><strong>Bouncing:</strong> When ball hits ground, creates new aerial segment with reduced velocity</li>
 *   <li><strong>Transition to ground:</strong> Low bounces transition to ground roll</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Physics Constants:</strong>
 * <ul>
 *   <li>g = 9.8 m/s² (gravity acceleration)</li>
 *   <li>k = 0.7 (air resistance coefficient)</li>
 *   <li>n = 1.5 (drag exponent)</li>
 *   <li>ay = 2.3 (vertical velocity adjustment factor)</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
public class AirTrajectory extends AbstractTrajectory {

    /**
     * Gravity acceleration constant (9.8 m/s²).
     */
    private static final double GRAVITY = 9.8;
    
    /**
     * Air resistance coefficient (0.7).
     */
    private static final double AIR_RESISTANCE = 0.7;
    
    /**
     * Drag exponent for air resistance formula (1.5).
     */
    private static final double DRAG_EXPONENT = 1.5;
    
    /**
     * Vertical velocity adjustment factor (2.3).
     */
    private static final double VERTICAL_ADJUSTMENT = 2.3;
    
    /**
     * Minimum vertical velocity to create another aerial bounce (0.1 m/iter).
     * Below this, ball transitions to ground roll.
     */
    private static final double MIN_BOUNCE_VELOCITY = 0.1;
    
    /**
     * Time delta for velocity calculation (0.1 iterations).
     */
    private static final double VELOCITY_DELTA = 0.1;
    
    /**
     * Velocity multiplier for bounce (4x for horizontal, 3x for vertical).
     */
    private static final double BOUNCE_VELOCITY_MULTIPLIER_X = 4.0;
    private static final double BOUNCE_VELOCITY_MULTIPLIER_Y = 3.0;
    
    /**
     * Ground roll velocity multiplier (2x of combined velocity).
     */
    private static final double GROUND_VELOCITY_MULTIPLIER = 2.0;
    
    /**
     * Next trajectory segment after this one ends.
     */
    private final AbstractTrajectory nextTrajectory;

    /**
     * Creates an aerial trajectory segment.
     * <p>
     * Automatically calculates when the ball hits the ground and creates
     * the next trajectory segment (either another bounce or ground roll).
     * </p>
     *
     * @param vx0 initial horizontal velocity in meters per iteration
     * @param vy0 initial vertical velocity in meters per iteration
     * @param x0 initial horizontal position in meters
     * @param y0 initial vertical height in meters
     */
    public AirTrajectory(double vx0, double vy0, double x0, double y0) {
        super(vx0, vy0, x0, y0);
        
        // Calculate position and velocity at end of this segment (when ball hits ground)
        double nextX0 = getX(dt);
        double nextY0 = getY(dt);
        
        // Estimate velocity at ground impact using finite differences
        double nextVx0 = (getX(dt) - getX(dt - VELOCITY_DELTA)) * BOUNCE_VELOCITY_MULTIPLIER_X;
        double nextVy0 = (getY(dt - VELOCITY_DELTA) - getY(dt)) * BOUNCE_VELOCITY_MULTIPLIER_Y;
        
        // Determine next segment type based on vertical velocity
        if (nextVy0 > MIN_BOUNCE_VELOCITY) {
            // Significant vertical velocity: create another bounce
            nextTrajectory = new AirTrajectory(nextVx0, nextVy0, nextX0, nextY0);
        } else {
            // Low vertical velocity: transition to ground roll
            double groundVelocity = Math.sqrt(nextVx0 * nextVx0 + nextVy0 * nextVy0) * 
                                   GROUND_VELOCITY_MULTIPLIER;
            nextTrajectory = new FloorTrajectory(groundVelocity, nextX0);
        }
    }

    @Override
    public double getX(double t) {
        if (t > dt && nextTrajectory != null) {
            return nextTrajectory.getX(t - dt);
        } else {
            // Horizontal distance with air resistance: X(t) = vx0 * (1 - e^(-k*n*t)) / k + vx0 * t + x0
            return vx0 * (1 - Math.exp(-AIR_RESISTANCE * DRAG_EXPONENT * t)) / AIR_RESISTANCE + 
                   vx0 * t + 
                   x0;
        }
    }

    @Override
    public double getY(double t) {
        if (t > dt && nextTrajectory != null) {
            return nextTrajectory.getY(t - dt);
        } else {
            // Vertical parabolic motion with gravity: Y(t) = y0 + vy0*ay*t - g*t²/2
            return y0 + vy0 * VERTICAL_ADJUSTMENT * t - GRAVITY * t * t / 2.0;
        }
    }

    @Override
    protected double calculateDuration() {
        // Solve for when Y(t) = 0 (ball hits ground)
        // Using quadratic formula for: -g*t²/2 + vy0*ay*t + y0 = 0
        double a = GRAVITY / 2.0;
        double b = vy0 * VERTICAL_ADJUSTMENT;
        double c = y0;
        
        double discriminant = b * b + 4 * a * c;
        
        if (discriminant > 0) {
            // Two solutions: ta (negative, ignored) and tb (positive, when ball lands)
            double sqrtDiscriminant = Math.sqrt(discriminant);
            double ta = (b + sqrtDiscriminant) / (2 * a);
            double tb = (b - sqrtDiscriminant) / (2 * a);
            return Math.max(ta, tb);  // Return positive root
        } else {
            // No real solution: ball never hits ground (shouldn't happen in normal gameplay)
            return 0;
        }
    }
}
