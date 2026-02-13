package com.javacup.tactics.pistachos;

import java.util.List;

import com.javacup.model.command.Command;
import com.javacup.model.command.CommandMoveTo;
import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;
import com.javacup.model.engine.GameSituations;

public class PosicionJugador {
	private GameSituations sp;
	private List<Command> comandos;
	private final int index = 0;
	private final DatosPartido data;
	
	public PosicionJugador(List<Command> comandos, DatosPartido data) {
		super();		
		this.comandos = comandos;
		this.data = data;
	}
	
	public void execute(GameSituations sp){
		this.sp = sp;
		Position pos = newPos();
		comandos.add(new CommandMoveTo(index, pos));
	}

	
	private Position newPos() {
		if(sp.isStarts() && !inArea(sp.ballPosition()))
			return move(sp.ballPosition(), Integer.MAX_VALUE);
		int iterBallOpponent = data.getOpponentIterToBall();
		if(iterBallOpponent == -1)
			iterBallOpponent = Integer.MAX_VALUE;
		int i = 0; 
		while(i < iterBallOpponent){
			if(data.getPosBall(i).isInsideGameField(0)){
				boolean inArea = inArea(data.getPosBall(i));
				double dist = sp.myPlayers()[index].distance(data.getPosBall(i));
				if(dist <= i*Constants.MAX_SPEED + Constants.GOALKEEPER_BALL_CONTROL_DISTANCE && data.getZBall(i) <= (inArea? Constants.GOAL_HEIGHT : Constants.BALL_CONTROL_HEIGHT)){
					return data.getPosBall(i);//.moverAngulo(data.getPosBall(i).angulo(Constants.BOTTOM_GOAL_CENTER), inArea?Constants.GOALKEEPER_BALL_CONTROL_DISTANCE:Constants.BALL_CONTROL_DISTANCE, data.getPosBall(i).distancia(Constants.BOTTOM_GOAL_CENTER));
				}
			}else
				return move(data.getPosBall(i).setInsideGameField(), Integer.MAX_VALUE);
			i++;
		}
		return move(data.getPosBall(iterBallOpponent), iterBallOpponent);
	}

	private Position move(Position posBall, int iter) {
		Position pos = Constants.BOTTOM_GOAL_CENTER.moveAngle(Constants.BOTTOM_GOAL_CENTER.angle(posBall), Constants.GOAL_WIDTH/2, Constants.BOTTOM_GOAL_CENTER.distance(posBall));
		int iter1 = (int) Math.ceil((sp.myPlayers()[index].distance(pos) - Constants.GOALKEEPER_BALL_CONTROL_DISTANCE)/Constants.MAX_SPEED);
		while(iter<iter1){
			pos = pos.moveAngle(pos.angle(Constants.BOTTOM_GOAL_CENTER), Constants.MAX_KICK_VELOCITY);
			if(!pos.isInsideGameField(0)){
				pos = pos.moveAngle(pos.angle(Constants.BOTTOM_GOAL_CENTER), Constants.MAX_KICK_VELOCITY, pos.distance(Constants.BOTTOM_GOAL_CENTER));
				break;
			}
			iter1 = (int) Math.ceil((sp.myPlayers()[index].distance(pos) - Constants.GOALKEEPER_BALL_CONTROL_DISTANCE)/Constants.MAX_SPEED);
		}
		return pos;
	}

	private boolean inArea(Position pos) {
		if(Math.abs(pos.getX()) <= Constants.LARGE_AREA_LENGTH/2 &&
				pos.getY() <= Constants.BOTTOM_GOAL_CENTER.getY()+Constants.LARGE_AREA_WIDTH)
			return true;
		return false;
	}
}
