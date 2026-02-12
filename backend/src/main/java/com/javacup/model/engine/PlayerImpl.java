package com.javacup.model.engine;

import com.javacup.model.PlayerDetail;
import lombok.Getter;

import java.awt.Color;
import java.io.Serializable;

/**
 * Immutable implementation of PlayerDetail interface.
 * <p>
 * This class stores player configuration with validated attributes.
 * All attribute values are automatically clamped to the valid range [0.0-1.0].
 * </p>
 * <p>
 * This class is package-private and used internally by the match engine
 * to store validated player data.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
class PlayerImpl implements PlayerDetail, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Player's name.
     */
    private final String playerName;
    
    /**
     * Player's skin color for rendering.
     */
    private final Color skinColor;
    
    /**
     * Player's hair color for rendering.
     */
    private final Color hairColor;
    
    /**
     * Player's jersey number (1-99).
     */
    private final int number;
    
    /**
     * Speed attribute (0.0-1.0).
     */
    private final double speed;
    
    /**
     * Kick power attribute (0.0-1.0).
     */
    private final double power;
    
    /**
     * Kick precision attribute (0.0-1.0).
     */
    private final double precision;
    
    /**
     * True if this player is the goalkeeper.
     */
    private final boolean isGoalKeeper;

    /**
     * Creates a player implementation by copying from another PlayerDetail.
     * <p>
     * All attributes are validated and clamped to valid ranges.
     * </p>
     *
     * @param detail player detail to copy from
     */
    public PlayerImpl(PlayerDetail detail) {
        this.playerName = detail.getPlayerName();
        this.number = detail.getNumber();
        this.skinColor = detail.getSkinColor();
        this.hairColor = detail.getHairColor();
        this.speed = clamp(detail.getSpeed());
        this.power = clamp(detail.getPower());
        this.precision = clamp(detail.getPrecision());
        this.isGoalKeeper = detail.isGoalKeeper();
    }

    /**
     * Creates a player implementation with all parameters.
     * <p>
     * All attributes are validated and clamped to valid ranges.
     * </p>
     *
     * @param name player name
     * @param number jersey number (1-99)
     * @param skinColor skin color for rendering
     * @param hairColor hair color for rendering
     * @param speed speed attribute (clamped to 0.0-1.0)
     * @param power kick power attribute (clamped to 0.0-1.0)
     * @param precision kick precision attribute (clamped to 0.0-1.0)
     * @param isGoalKeeper true if goalkeeper
     */
    public PlayerImpl(String name, int number, Color skinColor, Color hairColor,
                      double speed, double power, double precision, boolean isGoalKeeper) {
        this.playerName = name;
        this.number = number;
        this.skinColor = skinColor;
        this.hairColor = hairColor;
        this.speed = clamp(speed);
        this.power = clamp(power);
        this.precision = clamp(precision);
        this.isGoalKeeper = isGoalKeeper;
    }

    /**
     * Clamps a value to the range [0.0, 1.0].
     *
     * @param value value to clamp
     * @return clamped value
     */
    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    @Override
    public boolean isGoalKeeper() {
        return isGoalKeeper;
    }
}
