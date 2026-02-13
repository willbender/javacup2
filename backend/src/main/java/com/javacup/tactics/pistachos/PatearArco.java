package com.javacup.tactics.pistachos;

import java.util.List;

import com.javacup.model.command.Command;
import com.javacup.model.command.CommandHitBall;
import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;
import com.javacup.model.engine.GameSituations;

public class PatearArco {
	private GameSituations sp;
	private List<Command> comandos;
	private DatosPartido data;
	
	public PatearArco(List<Command> comandos, DatosPartido data) {
		this.comandos = comandos;
		this.data = data;
	}
	
	private boolean isShootSituation(double power, MentalidadEquipo mentality) {
		double factor = Math.abs(Math.sin(sp.ballPosition().angle(Constants.TOP_GOAL_CENTER)));
		factor = 1 - (1 - factor)*(1 - factor);
		return sp.ballPosition().distance(Constants.TOP_GOAL_CENTER) <= getThreshold(mentality)*factor*Constants.getKickVelocity(power)/Constants.MAX_KICK_VELOCITY;
	}

	private double getThreshold(MentalidadEquipo mentality) {
		if(mentality ==MentalidadEquipo.Normal)
			return 20;
		if(mentality ==MentalidadEquipo.Offensive)
			return 24;
		if(mentality ==MentalidadEquipo.Mostasera)
			return 35;
		if(mentality ==MentalidadEquipo.Contragolpe)
			return 30;
		
		return 28;
	}

	void execute(GameSituations sp,  int[] canShoot, MentalidadEquipo mentality){
		this.sp = sp;
		double power = 1;
		for (int i = 0; i < canShoot.length; i++) {
			if(sp.myPlayersDetail()[canShoot[i]].getPower() < power)
				power = sp.myPlayersDetail()[canShoot[i]].getPower();
		}
		if(isShootSituation(power, mentality)){
			double maxVertAngle = Constants.MAX_VERTICAL_ANGLE*Math.PI/180;
			double angleVert = 0;
			double angle = sp.ballPosition().angle(Constants.TOP_GOAL_RIGHT_POST.movePosition(-Constants.BALL_RADIUS, 0));
			angle = Math.min(angle + 0.5*Constants.getAngularError(1.0)/2*Math.PI, sp.ballPosition().angle(Constants.TOP_GOAL_CENTER));
			double maxAngle = sp.ballPosition().angle(Constants.TOP_GOAL_LEFT_POST.movePosition(Constants.BALL_RADIUS, 0));
			maxAngle = Math.max(maxAngle - 0.5*Constants.getAngularError(1.0)/2*Math.PI, sp.ballPosition().angle(Constants.TOP_GOAL_CENTER));
			ShootInfo shoot = new ShootInfo();
			while(angle <= maxAngle){
				angleVert = 0;
				while(angleVert <= maxVertAngle){
					shoot = compareShoot(shoot, evaluateShoot(power, angle, angleVert));
					angleVert += Math.PI/180;
				}				
				angle += Math.PI/180;
			}
			for (int player : canShoot) {
	        	comandos.add(new CommandHitBall(player, shoot.angle*180/Math.PI, 1, shoot.angleVert*180/Math.PI));
	        }			
        }		
	}
		
	
	private ShootInfo compareShoot(ShootInfo shoot1, ShootInfo shoot2) {
		if(shoot1.fitness >= shoot2.fitness)
			return shoot1;
		return shoot2;
	}

	private ShootInfo evaluateShoot(double power, double angle, double angleVert) {
		MovimientoPelota trajectory = new MovimientoPelota(sp.ballPosition(), Constants.getKickVelocity(power), angle, angleVert);
		double fitness = 0, f;
		for (int i = 0; i < trajectory.length; i++) {
			if(i == 0)
				f = calculateFitness(trajectory.positions[i], trajectory.z[i], i + 1, sp.ballPosition(), 0);
			else
				f = calculateFitness(trajectory.positions[i], trajectory.z[i], i + 1, trajectory.positions[i-1], trajectory.z[i-1]);
			fitness += f;
			if(f >1 ||f == -1)
				break;
		}
		return new ShootInfo(angle, angleVert, fitness);
	}


	private double calculateFitness(Position position, double z, int iter, Position last, double lastZ) {
		int oppIterToBall = data.calculateIterToBall(position, z, sp.rivalPlayers(), sp.rivalPlayersDetail(), sp.rivalIterationsToKick());
		double Dx = position.getX()-last.getX();
		double Dy = position.getY()-last.getY();
		if(!position.isInsideGameField(0)){
			if(position.getY() > Constants.FIELD_LENGTH/2){
				double posX = (Dx / Dy) * (Constants.FIELD_LENGTH/2 - position.getY()) + position.getX();
	            double Dz = z - lastZ;
				double posZ = (Dz  / Dy) * (Constants.FIELD_LENGTH/2 - position.getY()) + z;
				if(posZ <= Constants.GOAL_HEIGHT &&
					Math.abs(posX) < Constants.GOAL_WIDTH / 2 - Constants.BALL_RADIUS &&
					z - Dz <= Constants.GOAL_HEIGHT){
					double fvel = Math.sqrt(Dx*Dx + Dy*Dy)/Constants.MAX_KICK_VELOCITY;
					double fiter = Math.max(0, Math.min(1, (oppIterToBall - iter)/75.0));
					double fx = Math.max(0, Math.min(1, 1 - Math.abs(posX) /( Constants.GOAL_WIDTH / 2 - Constants.BALL_RADIUS)));
					return 1 + .40*fvel + .45*fiter + .15*fx;
				}
			}
			return -1;
		}
		if(oppIterToBall <= iter){
			double fdist = Math.max(0, Math.min(1, position.getY()/Constants.TOP_GOAL_CENTER.getY()));
			double fiter = Math.max(0.0, 1 - (iter - oppIterToBall)/5.0);
			double fvel = Math.max(0, Math.min(1, Math.sqrt(Dx*Dx + Dy*Dy)/Constants.MAX_KICK_VELOCITY));
			return -1 + (.3*fdist + .1*fiter + .6*fvel);
		}	
		return 0;
	}

	
	class ShootInfo{
		public double angle = 0;
		public double angleVert = 0;
		public double fitness = Double.NEGATIVE_INFINITY; 
		
		
		public ShootInfo(double angle, double angleVert, double fitness) {
			super();
			this.angle = angle;
			this.angleVert = angleVert;
			this.fitness = fitness;
		}

		public ShootInfo(){			
		}
	}
}
