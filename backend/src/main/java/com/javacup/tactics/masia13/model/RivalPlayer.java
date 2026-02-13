package com.javacup.tactics.masia13.model;

import static com.javacup.tactics.masia13.model.FieldSpecifications.MY_GOAL_CENTER;

import com.javacup.model.PlayerDetail;
import com.javacup.tactics.masia13.util.Position;

public class RivalPlayer extends Player{

	public RivalPlayer(int index, PlayerDetail playerDetail) {
		super(index, playerDetail);
	}
	
	@Override
	public void update(double x, double y, double energy, double aceleration) {
		this.lastMove = new Position(x, y);
		updateAceleration = true;
		super.update(x, y, energy, aceleration);
	}
	
	@Override
	protected boolean insideArea(Position ball) {
		return ball.insideRivalPenaltyArea();
	}

	@Override
	public Iterable<Position> trajectoryToGoal(Position from, int iterationsToFrom) {
		return trajectory(from, iterationsToFrom, MY_GOAL_CENTER);
	}	

}
