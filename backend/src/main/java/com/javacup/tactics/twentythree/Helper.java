/**
 * 
 */
package com.javacup.tactics.twentythree;

import com.javacup.model.PlayerDetail;
import com.javacup.model.command.CommandMoveTo;
import com.javacup.model.engine.GameSituations;
import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;

/**
 * @author willBender
 * 
 */
public class Helper {

	private static double distanciaMinimaMarca = 0.97;// 0.98;
	private static double distanciaMarca2 = Constants.LARGE_AREA_WIDTH + 2;

	public enum CUADRANTES {
		I, II, III, IV
	}

	public static final Position PUNTO_DISPARO_IZQ = new Position(Constants.TOP_GOAL_LEFT_POST.getX() + 2,
			Constants.TOP_GOAL_LEFT_POST.getY());
	public static final Position PUNTO_DISPARO_DER = new Position(Constants.TOP_GOAL_RIGHT_POST.getX() - 2,
			Constants.TOP_GOAL_RIGHT_POST.getY());

	private static Position[] rectaIntersecArquero = new Position[] {
			new Position(Constants.BOTTOM_GOAL_CENTER.getX() + (Constants.SMALL_AREA_LENGTH / 2),
					Constants.BOTTOM_GOAL_CENTER.getY() + Constants.SMALL_AREA_WIDTH),
			new Position(Constants.BOTTOM_GOAL_CENTER.getX() - (Constants.SMALL_AREA_LENGTH / 2),
					Constants.BOTTOM_GOAL_CENTER.getY() + Constants.SMALL_AREA_WIDTH) };

	public static CUADRANTES getCuadranteBalon(Position balonPos) {
		if (balonPos.getY() > 0) {
			if (balonPos.getX() > 0) {
				return CUADRANTES.III;
			}
			return CUADRANTES.II;
		}
		if (balonPos.getX() > 0) {
			return CUADRANTES.IV;
		}
		return CUADRANTES.I;
	}

	public static Position obtenerPuntoMejorMarcaArco(GameSituations sp) {
		Position posMiArquero = sp.myPlayers()[0];
		double dIzquierda = posMiArquero.distance(Constants.BOTTOM_GOAL_LEFT_POST);
		double dDerecha = posMiArquero.distance(Constants.BOTTOM_GOAL_RIGHT_POST);
		double distanciaEspacio;
		double angulo;
		Position pResultado;
		if (dIzquierda > dDerecha) {
			distanciaEspacio = dIzquierda / 2;
			angulo = posMiArquero.angle(Constants.BOTTOM_GOAL_LEFT_POST);
			pResultado = posMiArquero.moveAngle(angulo, distanciaEspacio);
		} else {
			distanciaEspacio = dDerecha / 2;
			angulo = posMiArquero.angle(Constants.BOTTOM_GOAL_RIGHT_POST);
			pResultado = posMiArquero.moveAngle(angulo, distanciaEspacio);
		}
		return pResultado;
	}

	public static Position obtenerPuntoMejorDisparoArco(GameSituations sp) {
		int arqueroRival = 0;
		for (int i = 0; i < sp.rivalPlayersDetail().length; i++) {
			if (sp.rivalPlayersDetail()[i].isGoalKeeper()) {
				arqueroRival = i;
				break;
			}
		}
		Position posSuArquero = sp.rivalPlayers()[arqueroRival];
		double dIzquierda = posSuArquero.distance(Constants.TOP_GOAL_LEFT_POST);
		double dDerecha = posSuArquero.distance(Constants.TOP_GOAL_RIGHT_POST);
		double distanciaEspacio;
		double angulo;
		Position pResultado;
		if (dIzquierda > dDerecha) {
			distanciaEspacio = dIzquierda / 2;
			angulo = posSuArquero.angle(Constants.TOP_GOAL_LEFT_POST);
			pResultado = posSuArquero.moveAngle(angulo, distanciaEspacio);
		} else {
			distanciaEspacio = dDerecha / 2;
			angulo = posSuArquero.angle(Constants.TOP_GOAL_RIGHT_POST);
			pResultado = posSuArquero.moveAngle(angulo, distanciaEspacio);
		}
		return pResultado;
	}

	public static double getDistanciaMarca(GameSituations sp, int idRival) {
		PlayerDetail detalleRival = sp.rivalPlayersDetail()[idRival];
		double diferenciaLimitesRemate = Constants.MAX_KICK_VELOCITY - Constants.MIN_KICK_VELOCITY;
		double remateRival = detalleRival.getPower();
		double factorMarca = diferenciaLimitesRemate * remateRival;
		double distMarca = Constants.MIN_KICK_VELOCITY + factorMarca;
		return distMarca;
	}

	public static CommandMoveTo getMovimientoGoleador(GameSituations sp, int idxJugador, Util23 util23) {
		int idxArqueroRival = 0;
		double distMarca = 0.99;
		if (idxJugador == 10) {
			distMarca = distanciaMarca2;
		}
		for (int i = 0; i < sp.rivalPlayersDetail().length; i++) {
			if (sp.rivalPlayersDetail()[i].isGoalKeeper()) {
				idxArqueroRival = i;
				break;
			}
		}
		double anguloMarca = sp.rivalPlayers()[idxArqueroRival].angle(sp.ballPosition());
		if (idxJugador == 10) {
			anguloMarca = Constants.TOP_GOAL_CENTER.angle(sp.ballPosition());
		}
		Position pResult;
		if (idxJugador == 10 && sp.ballPosition().distance(Constants.TOP_GOAL_CENTER) <= distanciaMarca2) {
			pResult = sp.ballPosition();
		} else if (idxJugador == 10) {
			pResult = Constants.TOP_GOAL_CENTER.moveAngle(anguloMarca, distMarca);
		} else {
			pResult = sp.rivalPlayers()[idxArqueroRival].moveAngle(anguloMarca, distMarca);
		}
		CommandMoveTo result = new CommandMoveTo(idxJugador, pResult);
		return result;
	}

	public static CommandMoveTo marcar(int idxMiJugador, int idxRival, int rivalMasCercano, boolean balonAMiArco,
			HistorialMarcas historialMarcas, GameSituations sp, Util23 util23, int rivalMasPeligro) {
		Position pMarca;
		double distanciaMarca = distanciaMinimaMarca;
		Position pMio = sp.myPlayers()[idxMiJugador];
		Position pRival = historialMarcas.predecirPosicionRival(sp, idxRival, 0);
		double anguloMarca;
		anguloMarca = pRival.angle(Helper.obtenerPuntoMejorMarcaArco(sp));
		double distanciaRival = pRival.distance(Constants.BOTTOM_GOAL_CENTER);
		boolean mioMasCercano = idxMiJugador == sp.ballPosition().nearestIndex(sp.myPlayers());
		boolean romedal = false;
		if (sp.rivalPlayersDetail()[0].getPlayerName().equalsIgnoreCase("Ospina")
				|| sp.rivalPlayersDetail()[0].getPlayerName().equalsIgnoreCase("0")) {
			romedal = true;
			distanciaMarca = 0.98;// 1.2;
			if(sp.rivalPlayersDetail()[idxRival].getNumber()==10){
				distanciaMarca = 1.2;
			}
		}
		if (!romedal) {
			if (distanciaRival > (Constants.FIELD_LENGTH / 2) + Constants.CENTER_CIRCLE_RADIUS
					&& idxRival != rivalMasPeligro
					&& (idxRival != rivalMasCercano || (pMio.distance(sp.ballPosition()) < pRival.distance(sp
							.ballPosition()) && sp.ballAltitude() <= Constants.BALL_CONTROL_HEIGHT))) {
				anguloMarca = pRival.angle(sp.ballPosition());
				distanciaMarca = 0.75;
			}
		}
		boolean suyoMasCercano = idxRival == sp.ballPosition().nearestIndex(sp.rivalPlayers());
		if (sp.isRivalStarts() && suyoMasCercano) {
			anguloMarca = pRival.angle(Helper.obtenerPuntoMejorMarcaArco(sp));
			distanciaMarca = Constants.SET_PIECE_DISTANCE + 2;
		}
		pMarca = pRival.moveAngle(anguloMarca, distanciaMarca);
		CommandMoveTo marcar = null;
		boolean distanciaMenorMio = sp.ballPosition().distance(pMio) < sp.ballPosition().distance(pRival);
		boolean balonControlable = sp.ballAltitude() <= Constants.BALL_CONTROL_HEIGHT;
		
		if (!romedal && distanciaMenorMio && balonControlable && mioMasCercano || (mioMasCercano && sp.isStarts())) {
			marcar = new CommandMoveTo(idxMiJugador, sp.ballPosition(),false);
		} else {
			marcar = new CommandMoveTo(idxMiJugador, pMarca.setInsideGameField(),false);
		}
		return marcar;
	}

	public static Position getPosicionArquero(Position balonPos, Position pRivalMasCercano, Position pMiArquero,
			double alturaBalon, Position... balonposAntA) {
		if (pMiArquero.distance(balonPos) < pRivalMasCercano.distance(balonPos) && alturaBalon < Constants.GOAL_HEIGHT) {
			return balonPos;
		}
		Position balonposAnt = (balonposAntA == null || balonposAntA.length < 1) ? Constants.BOTTOM_GOAL_CENTER
				: balonposAntA[0];
		Position[] rectaIntersecArquero = getLineaInterseccionArquero(balonPos);
		Position posIdeal = Position.intersection(rectaIntersecArquero[0], rectaIntersecArquero[1], balonPos,
				balonposAnt);
		// posIdeal = posIdeal.setPosicion(posIdeal.getX() * -1, posIdeal.getY()
		// * -1);
		if (posIdeal == null) {
			posIdeal = Constants.BOTTOM_LEFT_CORNER;
		}
		if ((posIdeal.getX() > rectaIntersecArquero[0].getX() || posIdeal.getX() < rectaIntersecArquero[1].getX())) {
			posIdeal = Position.intersection(rectaIntersecArquero[0], rectaIntersecArquero[1], balonPos,
					Constants.BOTTOM_GOAL_CENTER);
			if (posIdeal == null) {
				posIdeal = Constants.BOTTOM_LEFT_CORNER;
			}
			if ((posIdeal.getX() > rectaIntersecArquero[0].getX() || posIdeal.getX() < rectaIntersecArquero[1].getX())) {
				posIdeal = Constants.BOTTOM_GOAL_CENTER;
				switch (getCuadranteBalon(balonPos)) {
				case I:
					posIdeal = new Position(Constants.BOTTOM_GOAL_LEFT_POST.getX(), Constants.BOTTOM_GOAL_LEFT_POST.getY()
							+ (Constants.SMALL_AREA_WIDTH / 3));
					break;
				case IV:
					posIdeal = new Position(Constants.BOTTOM_GOAL_RIGHT_POST.getX(), Constants.BOTTOM_GOAL_RIGHT_POST.getY()
							+ (Constants.SMALL_AREA_WIDTH / 3));
					break;
				default:
					posIdeal = Constants.BOTTOM_GOAL_CENTER;
					break;
				}
			}
		}
		return posIdeal;
	}

	public static Position[] getLineaInterseccionArquero(Position balonPos) {
		double nuevoY = balonPos.distance(Constants.BOTTOM_GOAL_CENTER) / 25;
		if (nuevoY > 1) {
			return rectaIntersecArqueroDefault;
		}
		Position[] ret = new Position[] {
				new Position(rectaIntersecArqueroDefault[0].getX(), Constants.BOTTOM_GOAL_CENTER.getY()
						+ (Constants.SMALL_AREA_WIDTH * nuevoY)),
				new Position(rectaIntersecArqueroDefault[1].getX(), Constants.BOTTOM_GOAL_CENTER.getY()
						+ (Constants.SMALL_AREA_WIDTH * nuevoY)), };
		return ret;
	}

	private static Position[] rectaIntersecArqueroDefault = new Position[] {
			new Position(Constants.BOTTOM_GOAL_CENTER.getX() + (Constants.GOAL_WIDTH / 2), Constants.BOTTOM_GOAL_CENTER.getY()
					+ Constants.SMALL_AREA_WIDTH),
			new Position(Constants.BOTTOM_GOAL_CENTER.getX() - (Constants.GOAL_WIDTH / 2), Constants.BOTTOM_GOAL_CENTER.getY()
					+ Constants.SMALL_AREA_WIDTH) };

	public static Position getPositionDisparo(int indicador) {
		if (indicador % 3 == 0) {
			return PUNTO_DISPARO_DER;
		}
		if (indicador % 3 == 1) {
			return Constants.TOP_GOAL_CENTER;
		}
		return PUNTO_DISPARO_IZQ;
	}

	public static Position[] getRectaArqueroByPosBalon(Position posBalon) {
		if (posBalon.getY() > 0) {
			return rectaIntersecArquero;
		} else {
			double multiplicador = (Constants.FIELD_LENGTH / 2) - (posBalon.getY() * -1);
			if (multiplicador != 0) {
				multiplicador = multiplicador / (Constants.FIELD_LENGTH / 2);
			}
			Position res[] = new Position[] {
					new Position(Constants.BOTTOM_GOAL_CENTER.getX() + (Constants.SMALL_AREA_LENGTH / 2),
							Constants.BOTTOM_GOAL_CENTER.getY() + (Constants.SMALL_AREA_WIDTH * multiplicador)),
					new Position(Constants.BOTTOM_GOAL_CENTER.getX() - (Constants.SMALL_AREA_LENGTH / 2),
							Constants.BOTTOM_GOAL_CENTER.getY() + (Constants.SMALL_AREA_WIDTH) * multiplicador) };
			return res;
		}
	}

}