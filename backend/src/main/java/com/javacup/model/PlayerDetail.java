package com.javacup.model;

import java.awt.Color;

/**
 * Interface defining the configuration and attributes of an individual player.
 * <p>
 * This interface provides access to player information including:
 * <ul>
 *   <li>Visual attributes (name, colors, jersey number)</li>
 *   <li>Role (goalkeeper or field player)</li>
 *   <li>Performance attributes (speed, power, precision)</li>
 * </ul>
 * Teams have limited "credits" to distribute among all players and their attributes.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public interface PlayerDetail {

    /**
     * Gets the player's name.
     *
     * @return player name
     */
    String getPlayerName();

    /**
     * Gets the player's skin color for visual rendering.
     *
     * @return skin color
     */
    Color getSkinColor();

    /**
     * Gets the player's hair color for visual rendering.
     *
     * @return hair color
     */
    Color getHairColor();

    /**
     * Gets the player's jersey number.
     *
     * @return jersey number (1-99)
     */
    int getNumber();

    /**
     * Checks if this player is the goalkeeper.
     *
     * @return true if goalkeeper, false if field player
     */
    boolean isGoalKeeper();

    /**
     * Gets the player's speed attribute.
     * <p>
     * This affects how fast the player moves across the field.
     * Higher values result in faster movement (0.25-0.5 meters per iteration).
     * </p>
     *
     * @return speed factor in range [0.0-1.0]
     */
    double getSpeed();

    /**
     * Gets the player's kick power attribute.
     * <p>
     * This affects the strength of kicks.
     * Higher values result in faster ball velocity (1.2-2.4 meters per iteration).
     * </p>
     *
     * @return power factor in range [0.0-1.0]
     */
    double getPower();

    /**
     * Gets the player's kick precision attribute.
     * <p>
     * This affects the accuracy of kicks.
     * Higher values result in less angular error when kicking.
     * </p>
     *
     * @return precision factor in range [0.0-1.0]
     */
    double getPrecision();
}
