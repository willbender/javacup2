package com.javacup.tactics.pistachos;

import com.javacup.model.trajectory.AbstractTrajectory;
import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;
import com.javacup.model.trajectory.AirTrajectory;

class MovimientoPelota{
	public Position[] positions;
	public double[] z;
	public int length;
	
	public MovimientoPelota(Position ball, double velocity,
			double angle, double angleVert) {
		length = 75;
		positions = new Position[length];
		z = new double[length];
		AbstractTrajectory t = new AirTrajectory(velocity*Math.cos(angleVert), velocity*Math.sin(angleVert), 0, 0);
		for (int i = 0; i < length; i++) {
			double radio = t.getX((double)(i + 1)/60d)* Constants.TRAJECTORY_VELOCITY_AMPLIFIER;
			positions[i] = new Position(ball.getX() + radio*Math.cos(angle), ball.getY() + radio*Math.sin(angle));
			z[i] = t.getY((double)(i + 1)/60d)* Constants.TRAJECTORY_VELOCITY_AMPLIFIER * 2;
		}
	}	
}
