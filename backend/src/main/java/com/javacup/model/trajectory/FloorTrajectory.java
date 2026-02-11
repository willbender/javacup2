package com.javacup.model.trajectory;

import lombok.Getter;

/**
 * Models ball trajectory rolling on the ground with friction.
 * <p>
 * This class implements ground friction physics where the ball gradually
 * decelerates due to ground resistance until it stops. This is a terminal
 * trajectory - it doesn't chain to another segment.
 * </p>
 * <p>
 * <strong>Physics Model:</strong> Quadratic deceleration due to friction
 * <ul>
 *   <li>X(t) = vx0 * t - k * t² / 2 + x0</li>
 *   <li>Ball stops when velocity reaches zero: t = vx0 / k</li>
 *   <li>Y(t) always returns 0 (ball is on ground)</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Physics Constant:</strong>
 * <ul>
 *   <li>k = 4.0 (ground friction coefficient)</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
public class FloorTrajectory extends AbstractTrajectory {

    /**
     * Ground friction coefficient (4.0).
     * <p>
     * This value determines how quickly the ball decelerates on the ground.
     * Higher values mean faster deceleration.
     * </p>
     */
    private static final double GROUND_FRICTION = 4.0;
    
    /**
     * Final horizontal position where ball stops (meters).
     */
    private final double finalX;

    /**
     * Creates a ground trajectory segment.
     * <p>
     * The ball rolls on the ground with initial velocity and gradually
     * decelerates due to friction until it stops.
     * </p>
     *
     * @param vx0 initial velocity in meters per iteration
     * @param x0 initial horizontal position in meters
     */
    public FloorTrajectory(double vx0, double x0) {
        super(vx0, 0, x0, 0);  // vy0=0 and y0=0 since ball is on ground
        this.finalX = getX(dt);  // Calculate where ball will stop
    }

    @Override
    public double getX(double t) {
        if (t > dt) {
            // Ball has stopped, return final position
            return finalX;
        } else {
            // Quadratic deceleration formula: X(t) = vx0*t - k*t²/2 + x0
            return vx0 * t - GROUND_FRICTION * t * t / 2.0 + x0;
        }
    }

    @Override
    public double getY(double t) {
        // Ball is always on the ground
        return 0;
    }

    @Override
    protected double calculateDuration() {
        // Ball stops when velocity becomes zero
        // From V(t) = vx0 - k*t = 0, we get t = vx0/k
        return vx0 / GROUND_FRICTION;
    }
}
