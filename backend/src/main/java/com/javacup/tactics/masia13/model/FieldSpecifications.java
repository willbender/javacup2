package com.javacup.tactics.masia13.model;

import com.javacup.model.util.Constants;
import com.javacup.tactics.masia13.util.Position;

public class FieldSpecifications {
	
    public static final double FIELD_HEIGHT = Constants.FIELD_LENGTH;
    public static final double FIELD_WIDTH = Constants.FIELD_WIDTH;
    
    public static final double GOAL_WIDTH = Constants.GOAL_WIDTH;
    public static final double GOAL_HEIGHT = Constants.GOAL_HEIGHT;
    
    public static final double PENALTY_AREA_WIDTH = Constants.LARGE_AREA_LENGTH;
    public static final double PENALTY_AREA_HEIGHT = Constants.LARGE_AREA_WIDTH;
    
    public static final double PENALTY_DISTANCE = Constants.PENALTY_DISTANCE;
    public static final double PENALTY_RADIUS = Constants.PENALTY_ARC_RADIUS;
    
    public static final double KICK_DISTANCE = Constants.SET_PIECE_DISTANCE;
    
    public static final double GOAL_AREA_WIDTH = Constants.SMALL_AREA_LENGTH;
    public static final double GOAL_AREA_HEIGHT = Constants.SMALL_AREA_WIDTH;
    
    public static final Position MY_PENALTY = new Position(Constants.BOTTOM_PENALTY_SPOT.getX(), Constants.BOTTOM_PENALTY_SPOT.getY());
    public static final Position RIVAL_PENALTY = new Position(Constants.TOP_PENALTY_SPOT.getX(), Constants.TOP_PENALTY_SPOT.getY());
    
    public static final Position MY_GOAL_LEFT_POST = new Position(Constants.BOTTOM_GOAL_LEFT_POST.getX(), Constants.BOTTOM_GOAL_LEFT_POST.getY());
    public static final Position MY_GOAL_RIGHT_POST = new Position(Constants.BOTTOM_GOAL_RIGHT_POST.getX(), Constants.BOTTOM_GOAL_RIGHT_POST.getY());
    public static final Position MY_GOAL_CENTER = new Position(Constants.BOTTOM_GOAL_CENTER.getX(), Constants.BOTTOM_GOAL_CENTER.getY());
    
    public static final Position RIVAL_GOAL_LEFT_POST = new Position(Constants.TOP_GOAL_LEFT_POST.getX(), Constants.TOP_GOAL_LEFT_POST.getY());
    public static final Position RIVAL_GOAL_RIGHT_POST = new Position(Constants.TOP_GOAL_RIGHT_POST.getX(), Constants.TOP_GOAL_RIGHT_POST.getY());
    public static final Position RIVAL_GOAL_CENTER = new Position(Constants.TOP_GOAL_CENTER.getX(), Constants.TOP_GOAL_CENTER.getY());
    
    public static final Position FIELD_CENTER = new Position(Constants.FIELD_CENTER.getX(), Constants.FIELD_CENTER.getY());
    
    public static final Position BOTTOM_LEFT_CORNER  = new Position(Constants.BOTTOM_LEFT_CORNER.getX(), Constants.BOTTOM_LEFT_CORNER.getY());
    public static final Position BOTTOM_RIGHT_CORNER  = new Position(Constants.BOTTOM_RIGHT_CORNER.getX(), Constants.BOTTOM_RIGHT_CORNER.getY());
    public static final Position TOP_LEFT_CORNER  = new Position(Constants.TOP_LEFT_CORNER.getX(), Constants.TOP_LEFT_CORNER.getY());
    public static final Position TOP_RIGHT_CORNER  = new Position(Constants.TOP_RIGHT_CORNER.getX(), Constants.TOP_RIGHT_CORNER.getY());
    
}
