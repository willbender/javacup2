package com.javacup.model.util;

/**
 * Constants for the football match simulation.
 * <p>
 * Contains all game parameters including:
 * <ul>
 *   <li>Field dimensions (in meters)</li>
 *   <li>Player movement and energy parameters</li>
 *   <li>Ball physics constants</li>
 *   <li>Match configuration (iterations, timing)</li>
 *   <li>Predefined field positions</li>
 * </ul>
 * All measurements use meters as the unit, and iterations run at 60 per second.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // ============================================================================
    // Field Dimensions (in meters)
    // ============================================================================
    
    /**
     * Playing field length (105 meters).
     */
    public static final double FIELD_LENGTH = 105.0;
    
    /**
     * Playing field width (68 meters).
     */
    public static final double FIELD_WIDTH = 68.0;
    
    /**
     * Total grass area length including margins (113 meters).
     */
    public static final double TOTAL_LENGTH = 113.0;
    
    /**
     * Total grass area width including margins (73 meters).
     */
    public static final double TOTAL_WIDTH = 73.0;
    
    /**
     * Goal width amplification factor (1.2x for gameplay).
     */
    public static final double GOAL_AMPLIFICATION = 1.2;
    
    /**
     * Goal width (7.32 × 1.2 = 8.784 meters).
     */
    public static final double GOAL_WIDTH = 7.32 * GOAL_AMPLIFICATION;
    
    /**
     * Goal height (5 meters, actually 2.44m in real football but amplified for gameplay).
     */
    public static final double GOAL_HEIGHT = 5.0;
    
    /**
     * Large penalty area length (40.32 meters).
     */
    public static final double LARGE_AREA_LENGTH = 40.32;
    
    /**
     * Large penalty area width/depth (16.5 meters).
     */
    public static final double LARGE_AREA_WIDTH = 16.5;
    
    /**
     * Distance from penalty spot to goal line (11 meters).
     */
    public static final double PENALTY_DISTANCE = 11.0;
    
    /**
     * Penalty arc radius (9.15 meters).
     */
    public static final double PENALTY_ARC_RADIUS = 9.15;
    
    /**
     * Small penalty area (goal area) length (18.32 meters).
     */
    public static final double SMALL_AREA_LENGTH = 18.32;
    
    /**
     * Small penalty area (goal area) width/depth (5.5 meters).
     */
    public static final double SMALL_AREA_WIDTH = 5.5;
    
    /**
     * Center circle radius (9.15 meters).
     */
    public static final double CENTER_CIRCLE_RADIUS = 9.15;

    // ============================================================================
    // Other Dimensions
    // ============================================================================
    
    /**
     * Minimum separation between players (0.75 meters).
     */
    public static final double PLAYER_SEPARATION = 0.75;
    
    /**
     * Maximum height at which field players can control the ball (3 meters).
     * Goalkeeper can control up to GOAL_HEIGHT (5 meters).
     */
    public static final double BALL_CONTROL_HEIGHT = 3.0;
    
    /**
     * Maximum distance at which players can control/kick the ball (1 meter).
     */
    public static final double BALL_CONTROL_DISTANCE = 1.0;
    
    /**
     * Maximum distance at which goalkeeper can control the ball (1.2 meters).
     */
    public static final double GOALKEEPER_BALL_CONTROL_DISTANCE = 1.2;

    // ============================================================================
    // Match Configuration
    // ============================================================================
    
    /**
     * Default rendering scale (pixels per meter).
     */
    public static final double RENDERING_SCALE = 15.0;
    
    /**
     * Total iterations in a match (3600 = 60 seconds at 60 iterations/second).
     */
    public static final int TOTAL_ITERATIONS = 3600;
    
    /**
     * Initial credits per team for distributing player attributes.
     */
    public static final double INITIAL_CREDITS = 27.5;
    
    /**
     * Frames per second for rendering (20 FPS).
     */
    public static final int FPS = 20;
    
    /**
     * Delay between frames in milliseconds (1000/FPS).
     */
    public static final int FRAME_DELAY = 1000 / FPS;

    // ============================================================================
    // Player Movement Parameters
    // ============================================================================
    
    /**
     * Minimum player speed (0.25 meters per iteration).
     */
    public static final double MIN_SPEED = 0.25;
    
    /**
     * Maximum player speed (0.5 meters per iteration).
     */
    public static final double MAX_SPEED = 0.5;
    
    /**
     * Sprint speed multiplier (1.2x normal speed).
     */
    public static final double SPRINT_MULTIPLIER = 1.2;
    
    /**
     * Minimum energy required to sprint (0.8 on scale of 0-1).
     */
    public static final double MIN_SPRINT_ENERGY = 0.8;
    
    /**
     * Extra energy consumed per iteration when sprinting (0.02).
     */
    public static final double SPRINT_ENERGY_COST = 0.02;
    
    /**
     * Passive energy recovery rate per iteration (0.00001).
     */
    public static final double ENERGY_RECOVERY_RATE = 0.00001;
    
    /**
     * Minimum energy level a player can have (0.55).
     */
    public static final double MIN_ENERGY = 0.55;
    
    /**
     * Maximum energy level (1.0 = full energy).
     */
    public static final double MAX_ENERGY = 1.0;
    
    /**
     * Energy multiplier for shot power (1.25).
     */
    public static final double ENERGY_SHOT_FACTOR = 1.25;
    
    /**
     * Minimum acceleration when changing direction on Y-axis (0.70).
     */
    public static final double MIN_ACCELERATION_Y = 0.70;
    
    /**
     * Minimum acceleration when changing direction on X-axis (0.90).
     */
    public static final double MIN_ACCELERATION_X = 0.90;
    
    /**
     * Acceleration increment per iteration when maintaining direction (0.04).
     */
    public static final double ACCELERATION_INCREMENT = 0.04;

    // ============================================================================
    // Ball Kicking Parameters
    // ============================================================================
    
    /**
     * Minimum kick velocity (1.2 meters per iteration).
     */
    public static final double MIN_KICK_VELOCITY = 1.2;
    
    /**
     * Maximum kick velocity (2.4 meters per iteration).
     */
    public static final double MAX_KICK_VELOCITY = 2.4;
    
    /**
     * Minimum angular error factor for kicks (0.1).
     */
    public static final double MIN_ERROR = 0.1;
    
    /**
     * Maximum angular error factor for kicks (0.3).
     */
    public static final double MAX_ERROR = 0.3;
    
    /**
     * Default vertical angle for high kicks (30 degrees).
     */
    public static final double VERTICAL_ANGLE = 30.0;
    
    /**
     * Maximum vertical angle allowed for kicks (60 degrees).
     */
    public static final double MAX_VERTICAL_ANGLE = 60.0;
    
    /**
     * Trajectory velocity amplification factor (20).
     */
    public static final double TRAJECTORY_VELOCITY_AMPLIFIER = 20.0;

    // ============================================================================
    // Match Timing and Rules
    // ============================================================================
    
    /**
     * Cooldown iterations before a player can kick again (10 iterations).
     */
    public static final int KICK_COOLDOWN_ITERATIONS = 10;
    
    /**
     * Maximum iterations allowed for taking a set piece (100).
     * If exceeded, the opposing team gets possession.
     */
    public static final int MAX_SET_PIECE_ITERATIONS = 100;
    
    /**
     * Ball radius for calculating goalpost collisions (0.3 meters).
     */
    public static final double BALL_RADIUS = 0.3;
    
    /**
     * Minimum distance opponents must be from ball during set pieces (15 meters).
     */
    public static final double SET_PIECE_DISTANCE = 15.0;
    
    /**
     * Camera follow smoothness factor (20 = smoother movement).
     */
    public static final double CAMERA_SMOOTHNESS = 20.0;

    // ============================================================================
    // Deprecated Constants
    // ============================================================================
    
    /**
     * @deprecated Not used in current implementation
     */
    @Deprecated
    public static final double BALL_AIR_VELOCITY_FACTOR = 0.97;
    
    /**
     * @deprecated Not used in current implementation
     */
    @Deprecated
    public static final double BALL_GROUND_VELOCITY_FACTOR = 0.93;
    
    /**
     * @deprecated Not used in current implementation
     */
    @Deprecated
    public static final double BALL_BOUNCE_HEIGHT_FACTOR = 0.6;
    
    /**
     * @deprecated Not used in current implementation
     */
    @Deprecated
    public static final double GRAVITY_CONSTANT = 0.12;

    // ============================================================================
    // Predefined Field Positions
    // ============================================================================
    
    /**
     * Center of the field (0, 0).
     */
    public static final Position FIELD_CENTER = new Position(0, 0);
    
    /**
     * Top-left corner of center circle.
     */
    public static final Position CENTER_CIRCLE_TOP_LEFT = 
        new Position(-CENTER_CIRCLE_RADIUS, -CENTER_CIRCLE_RADIUS);
    
    /**
     * Top-left corner of total grass area.
     */
    public static final Position TOTAL_AREA_TOP_LEFT = 
        new Position(-TOTAL_WIDTH / 2.0, -TOTAL_LENGTH / 2.0);
    
    /**
     * Dimensions of total grass area.
     */
    public static final Position TOTAL_AREA_DIMENSIONS = 
        new Position(TOTAL_WIDTH, TOTAL_LENGTH);
    
    /**
     * Top-left corner of playing field.
     */
    public static final Position FIELD_TOP_LEFT = 
        new Position(-FIELD_WIDTH / 2.0, -FIELD_LENGTH / 2.0);
    
    /**
     * Dimensions of playing field.
     */
    public static final Position FIELD_DIMENSIONS = 
        new Position(FIELD_WIDTH, FIELD_LENGTH);
    
    /**
     * Center of top goal (0, -52.5).
     */
    public static final Position TOP_GOAL_CENTER = 
        new Position(0, -FIELD_LENGTH / 2);
    
    /**
     * Center of bottom goal (0, 52.5).
     */
    public static final Position BOTTOM_GOAL_CENTER = 
        new Position(0, FIELD_LENGTH / 2);
    
    /**
     * Left post of top goal.
     */
    public static final Position TOP_GOAL_LEFT_POST = 
        new Position(-GOAL_WIDTH / 2, -FIELD_LENGTH / 2);
    
    /**
     * Right post of top goal.
     */
    public static final Position TOP_GOAL_RIGHT_POST = 
        new Position(GOAL_WIDTH / 2, -FIELD_LENGTH / 2);
    
    /**
     * Left post of bottom goal.
     */
    public static final Position BOTTOM_GOAL_LEFT_POST = 
        new Position(-GOAL_WIDTH / 2, FIELD_LENGTH / 2);
    
    /**
     * Right post of bottom goal.
     */
    public static final Position BOTTOM_GOAL_RIGHT_POST = 
        new Position(GOAL_WIDTH / 2, FIELD_LENGTH / 2);
    
    /**
     * Top penalty spot.
     */
    public static final Position TOP_PENALTY_SPOT = 
        new Position(0, -FIELD_LENGTH / 2 + PENALTY_DISTANCE);
    
    /**
     * Bottom penalty spot.
     */
    public static final Position BOTTOM_PENALTY_SPOT = 
        new Position(0, FIELD_LENGTH / 2 - PENALTY_DISTANCE);
    
    /**
     * Top-left corner position.
     */
    public static final Position TOP_LEFT_CORNER = new Position(FIELD_TOP_LEFT);
    
    /**
     * Top-right corner position.
     */
    public static final Position TOP_RIGHT_CORNER = 
        FIELD_TOP_LEFT.movePosition(FIELD_WIDTH, 0);
    
    /**
     * Bottom-left corner position.
     */
    public static final Position BOTTOM_LEFT_CORNER = 
        FIELD_TOP_LEFT.movePosition(0, FIELD_LENGTH);
    
    /**
     * Bottom-right corner position.
     */
    public static final Position BOTTOM_RIGHT_CORNER = 
        BOTTOM_LEFT_CORNER.movePosition(FIELD_WIDTH, 0);

    // ============================================================================
    // Helper Methods
    // ============================================================================
    
    /**
     * Converts a speed factor [0-1] to actual speed in meters per iteration.
     *
     * @param factor speed factor (0.0 = slowest, 1.0 = fastest)
     * @return speed in meters per iteration (range: 0.25-0.5)
     */
    public static double getSpeed(double factor) {
        return MIN_SPEED + (MAX_SPEED - MIN_SPEED) * factor;
    }
    
    /**
     * Converts a power factor [0-1] to actual kick velocity in meters per iteration.
     *
     * @param factor power factor (0.0 = weakest, 1.0 = strongest)
     * @return kick velocity in meters per iteration (range: 1.2-2.4)
     */
    public static double getKickVelocity(double factor) {
        return MIN_KICK_VELOCITY + (MAX_KICK_VELOCITY - MIN_KICK_VELOCITY) * factor;
    }
    
    /**
     * Converts a precision factor [0-1] to angular error.
     * <p>
     * Lower error means more accurate kicks.
     * </p>
     *
     * @param precision precision factor (0.0 = least precise, 1.0 = most precise)
     * @return angular error in radians (range: 0.1-0.3, lower is better)
     */
    public static double getAngularError(double precision) {
        return MAX_ERROR - (MAX_ERROR - MIN_ERROR) * precision;
    }
}
