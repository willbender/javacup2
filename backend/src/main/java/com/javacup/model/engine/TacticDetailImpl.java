package com.javacup.model.engine;

import com.javacup.model.PlayerDetail;
import com.javacup.model.TacticDetail;
import com.javacup.model.UniformStyle;
import lombok.Getter;

import java.awt.Color;
import java.io.Serializable;

/**
 * Immutable implementation of TacticDetail interface.
 * <p>
 * This class creates an immutable copy of a tactic's configuration,
 * preventing modifications during match execution. All fields are
 * copied and stored as final values.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
final class TacticDetailImpl implements TacticDetail, Serializable {

    private static final long serialVersionUID = 1L;

    // Team information
    private final String tacticName;
    private final String country;
    private final String coach;
    
    // Primary uniform colors
    private final Color shirtColor;
    private final Color shortsColor;
    private final Color socksColor;
    private final Color shirtLineColor;
    private final Color goalKeeper;
    private final UniformStyle style;
    
    // Secondary uniform colors
    private final Color shirtColor2;
    private final Color shortsColor2;
    private final Color socksColor2;
    private final Color shirtLineColor2;
    private final Color goalKeeper2;
    private final UniformStyle style2;
    
    // Player roster
    private final PlayerImpl[] players = new PlayerImpl[11];

    /**
     * Creates an immutable copy of a TacticDetail.
     * <p>
     * All values are copied and stored as final fields, making this
     * implementation completely immutable.
     * </p>
     *
     * @param detail tactic detail to copy
     */
    TacticDetailImpl(TacticDetail detail) {
        // Copy team information
        this.tacticName = detail.getTacticName();
        this.country = detail.getCountry();
        this.coach = detail.getCoach();
        
        // Copy primary uniform
        this.shirtColor = detail.getShirtColor();
        this.shortsColor = detail.getShortsColor();
        this.socksColor = detail.getSocksColor();
        this.shirtLineColor = detail.getShirtLineColor();
        this.goalKeeper = detail.getGoalKeeper();
        this.style = detail.getStyle();
        
        // Copy secondary uniform
        this.shirtColor2 = detail.getShirtColor2();
        this.shortsColor2 = detail.getShortsColor2();
        this.socksColor2 = detail.getSocksColor2();
        this.shirtLineColor2 = detail.getShirtLineColor2();
        this.goalKeeper2 = detail.getGoalKeeper2();
        this.style2 = detail.getStyle2();
        
        // Copy and validate all players
        for (int i = 0; i < 11; i++) {
            this.players[i] = new PlayerImpl(detail.getPlayers()[i]);
        }
    }

    @Override
    public PlayerDetail[] getPlayers() {
        return players;
    }
}
