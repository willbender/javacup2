package com.javacup.tactics.masia13.model;

import com.javacup.model.util.Constants;

public class PlayerSpecifications {
	
    public static final double GOALKEEPER_HEIGHT_INSIDE_AREA = Constants.GOAL_HEIGHT;
    public static final double GOALKEEPER_CONTROL_DISTANCE_INSIDE_AREA = Constants.GOALKEEPER_BALL_CONTROL_DISTANCE;
    public static final double PLAYER_HEIGHT = Constants.BALL_CONTROL_HEIGHT;
    public static final double CONTROL_DISTANCE = Constants.BALL_CONTROL_DISTANCE;
    public static final double PLAYER_WIDTH = Constants.PLAYER_SEPARATION;
    
    public static final double MAX_SPEED_SHOOT = Constants.MAX_KICK_VELOCITY;
    
    public static final double MIN_ENERGY = Constants.MIN_ENERGY;
    public static final double ENERGY_RATE = Constants.ENERGY_RECOVERY_RATE;
    public static final double MIN_ENERGY_SPRINT = Constants.MIN_SPRINT_ENERGY;
    
    public static final double MIN_ACELERATION_X = Constants.MIN_ACCELERATION_X;
    public static final double MIN_ACELERATION_Y = Constants.MIN_ACCELERATION_Y;
    public static final double MIN_ACELERATION = MIN_ACELERATION_X*MIN_ACELERATION_Y;
    public static final double ACELERATION_RATE = Constants.ACCELERATION_INCREMENT;
    
}
