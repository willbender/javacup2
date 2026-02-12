/**
 * 
 */
package com.javacup.tactics.twentythree;

import com.javacup.model.util.Position;

/**
 * Clase que contendra la posicion para un rival en una iteracion dada
 * 
 * @author willBender
 * 
 */
public class PosicionJugador extends PosicionIteracion {

	/**
	 * El rival puede patear en esta iteracion
	 */
	private Boolean	puedePatear;

	public PosicionJugador(Position posicion, Integer iteracion, Boolean puedePatear) {
		setPosicion(posicion);
		setIteracion(iteracion);
		this.puedePatear = puedePatear;
	}

	/**
	 * @return the puedePatear
	 */
	public Boolean getPuedePatear() {
		return puedePatear;
	}

	/**
	 * @param puedePatear
	 *            the puedePatear to set
	 */
	public void setPuedePatear(Boolean puedePatear) {
		this.puedePatear = puedePatear;
	}

}