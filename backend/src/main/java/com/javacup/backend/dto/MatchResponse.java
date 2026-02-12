package com.javacup.backend.dto;

import com.javacup.model.util.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for match result response.
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponse {
    
    /**
     * Home team name.
     */
    private String homeTeamName;
    
    /**
     * Away team name.
     */
    private String awayTeamName;
    
    /**
     * Home team goals scored.
     */
    private int homeGoals;
    
    /**
     * Away team goals scored.
     */
    private int awayGoals;
    
    /**
     * Total iterations executed in the match.
     */
    private int totalIterations;
    
    /**
     * Home team possession percentage (0.0 to 1.0).
     */
    private double homePossession;
    
    /**
     * Away team possession percentage (0.0 to 1.0).
     */
    private double awayPossession;
    
    /**
     * Final ball position.
     */
    private PositionDTO finalBallPosition;
    
    /**
     * Final home team player positions.
     */
    private List<PositionDTO> finalHomePositions;
    
    /**
     * Final away team player positions.
     */
    private List<PositionDTO> finalAwayPositions;
    
    /**
     * Match result summary.
     */
    private String result;
    
    /**
     * DTO for position information.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionDTO {
        private double x;
        private double y;
        
        public static PositionDTO from(Position position) {
            return PositionDTO.builder()
                .x(position.getX())
                .y(position.getY())
                .build();
        }
    }
}
