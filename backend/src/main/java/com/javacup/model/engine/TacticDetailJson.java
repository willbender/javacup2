package com.javacup.model.engine;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javacup.model.TacticDetail;

/**
 * JSON-friendly representation of TacticDetail for serialization.
 * <p>
 * This class provides a simplified view of team configuration that can be
 * easily serialized to JSON without circular references or complex AWT objects.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, 
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                isGetterVisibility = JsonAutoDetect.Visibility.NONE)
class TacticDetailJson {
    
    @JsonProperty("teamName")
    private final String teamName;
    
    @JsonProperty("country")
    private final String country;
    
    @JsonProperty("coach")
    private final String coach;
    
    @JsonProperty("players")
    private final PlayerJson[] players;
    
    /**
     * Creates a JSON-friendly version of TacticDetail.
     *
     * @param detail the tactic detail to convert
     */
    public TacticDetailJson(TacticDetail detail) {
        this.teamName = detail.getTacticName();
        this.country = detail.getCountry();
        this.coach = detail.getCoach();
        
        // Convert players
        this.players = new PlayerJson[11];
        for (int i = 0; i < 11; i++) {
            this.players[i] = new PlayerJson(detail.getPlayers()[i]);
        }
    }
    
    /**
     * JSON-friendly representation of a player.
     */
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, 
                    getterVisibility = JsonAutoDetect.Visibility.NONE,
                    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    static class PlayerJson {
        @JsonProperty("name")
        private final String name;
        
        @JsonProperty("number")
        private final int number;
        
        @JsonProperty("isGoalkeeper")
        private final boolean isGoalkeeper;
        
        @JsonProperty("speed")
        private final double speed;
        
        @JsonProperty("power")
        private final double power;
        
        @JsonProperty("precision")
        private final double precision;
        
        public PlayerJson(com.javacup.model.PlayerDetail player) {
            this.name = player.getPlayerName();
            this.number = player.getNumber();
            this.isGoalkeeper = player.isGoalKeeper();
            this.speed = player.getSpeed();
            this.power = player.getPower();
            this.precision = player.getPrecision();
        }
    }
}
