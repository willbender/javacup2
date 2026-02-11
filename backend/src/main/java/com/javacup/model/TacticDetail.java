package com.javacup.model;

import java.awt.Color;

/**
 * Interface defining team configuration and visual appearance.
 * <p>
 * This interface provides access to:
 * <ul>
 *   <li>Team identification (name, country, coach)</li>
 *   <li>Primary uniform colors and style</li>
 *   <li>Secondary (alternate) uniform colors and style</li>
 *   <li>Player roster and attributes</li>
 * </ul>
 * Teams must define two complete uniform sets (home and away kits).
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public interface TacticDetail {

    /**
     * Gets the team name.
     *
     * @return team name
     */
    String getTacticName();

    /**
     * Gets the team's country.
     *
     * @return country name
     */
    String getCountry();

    /**
     * Gets the coach's name.
     *
     * @return coach name
     */
    String getCoach();

    // Primary Uniform (Home Kit)

    /**
     * Gets the primary shirt color.
     *
     * @return primary shirt color
     */
    Color getShirtColor();

    /**
     * Gets the primary shorts color.
     *
     * @return primary shorts color
     */
    Color getShortsColor();

    /**
     * Gets the primary shirt line/stripe color.
     * <p>
     * This color is used for stripes or patterns defined by the uniform style.
     * </p>
     *
     * @return primary shirt line color
     */
    Color getShirtLineColor();

    /**
     * Gets the primary socks color.
     *
     * @return primary socks color
     */
    Color getSocksColor();

    /**
     * Gets the primary goalkeeper jersey color.
     *
     * @return primary goalkeeper color
     */
    Color getGoalKeeper();

    /**
     * Gets the primary uniform style.
     *
     * @return primary uniform style pattern
     */
    UniformStyle getStyle();

    // Secondary Uniform (Away Kit)

    /**
     * Gets the secondary shirt color.
     *
     * @return secondary shirt color
     */
    Color getShirtColor2();

    /**
     * Gets the secondary shorts color.
     *
     * @return secondary shorts color
     */
    Color getShortsColor2();

    /**
     * Gets the secondary shirt line/stripe color.
     *
     * @return secondary shirt line color
     */
    Color getShirtLineColor2();

    /**
     * Gets the secondary socks color.
     *
     * @return secondary socks color
     */
    Color getSocksColor2();

    /**
     * Gets the secondary goalkeeper jersey color.
     *
     * @return secondary goalkeeper color
     */
    Color getGoalKeeper2();

    /**
     * Gets the secondary uniform style.
     *
     * @return secondary uniform style pattern
     */
    UniformStyle getStyle2();

    /**
     * Gets the array of player details for the team.
     * <p>
     * Must return exactly 11 PlayerDetail objects, one for each player.
     * Exactly one player must be designated as the goalkeeper.
     * </p>
     *
     * @return array of 11 PlayerDetail objects defining player characteristics
     */
    PlayerDetail[] getPlayers();
}
