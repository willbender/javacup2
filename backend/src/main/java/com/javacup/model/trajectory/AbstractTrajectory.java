package com.javacup.model.trajectory;

import lombok.Getter;

/**
 * Abstract base class for ball trajectory segments.
 * <p>
 * This class models ball trajectories using a segmented approach where the complete
 * ball path is divided into segments, each with its own physics model. Segments
 * can chain together recursively, allowing transitions such as:
 * <ul>
 *   <li>Aerial trajectory → bounce → aerial trajectory</li>
 *   <li>Aerial trajectory → ground roll</li>
 *   <li>Ground roll → stop</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Physics Model:</strong> Each segment calculates horizontal (X) and
 * vertical (Y) positions based on initial conditions and time. The segment is
 * valid for a specific duration (dt), after which the next segment takes over.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
public abstract class AbstractTrajectory {

    /**
     * Initial horizontal velocity component (meters per iteration).
     */
    protected final double vx0;
    
    /**
     * Initial vertical velocity component (meters per iteration).
     */
    protected final double vy0;
    
    /**
     * Initial horizontal position (meters).
     */
    protected final double x0;
    
    /**
     * Initial vertical height (meters).
     */
    protected final double y0;
    
    /**
     * Duration for which this trajectory segment is valid (iterations).
     */
    protected final double dt;

    /**
     * Creates a trajectory segment with given initial conditions.
     *
     * @param vx0 initial horizontal velocity in meters per iteration
     * @param vy0 initial vertical velocity in meters per iteration
     * @param x0 initial horizontal position in meters
     * @param y0 initial vertical height in meters
     */
    protected AbstractTrajectory(double vx0, double vy0, double x0, double y0) {
        this.vx0 = vx0;
        this.vy0 = vy0;
        this.x0 = x0;
        this.y0 = y0;
        this.dt = calculateDuration();
    }

    /**
     * Gets the trajectory segment that is active at a specific time.
     * <p>
     * This method recursively navigates through chained trajectory segments
     * to find the segment responsible for the given time.
     * </p>
     *
     * @param time time in iterations from trajectory start
     * @return trajectory segment active at that time
     */
    public AbstractTrajectory getTrajectory(double time) {
        if (this instanceof AirTrajectory airTrajectory) {
            if (time < dt) {
                return this;
            } else {
                return airTrajectory.getNextTrajectory().getTrajectory(time - dt);
            }
        } else {
            // Floor trajectory is terminal
            return this;
        }
    }

    /**
     * Calculates horizontal distance traveled at time t.
     * <p>
     * This is the cumulative horizontal distance from the trajectory's origin.
     * </p>
     *
     * @param t time in iterations since trajectory segment started
     * @return horizontal distance in meters
     */
    public abstract double getX(double t);

    /**
     * Calculates vertical height at time t.
     * <p>
     * Height is measured from ground level (0 = on ground, positive = in air).
     * </p>
     *
     * @param t time in iterations since trajectory segment started
     * @return vertical height in meters
     */
    public abstract double getY(double t);

    /**
     * Calculates the duration for which this trajectory segment is valid.
     * <p>
     * For aerial trajectories, this is when the ball hits the ground.
     * For ground trajectories, this is when the ball stops rolling.
     * </p>
     *
     * @return duration in iterations
     */
    protected abstract double calculateDuration();

    /**
     * Checks if a bounce occurred between two time points.
     * <p>
     * A bounce is detected when both time points are in aerial trajectories
     * but in different trajectory segments (indicating ground contact).
     * </p>
     *
     * @param t0 start time in iterations
     * @param t1 end time in iterations
     * @return true if ball bounced between t0 and t1
     */
    public boolean isBounce(double t0, double t1) {
        AbstractTrajectory trajectory0 = getTrajectory(t0);
        AbstractTrajectory trajectory1 = getTrajectory(t1);
        return (trajectory0 instanceof AirTrajectory && 
                trajectory1 instanceof AirTrajectory && 
                trajectory0 != trajectory1);
    }
}
