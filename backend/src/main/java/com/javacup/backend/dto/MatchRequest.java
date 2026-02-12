package com.javacup.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for match creation request.
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {
    
    /**
     * Name of the home team's tactic.
     */
    private String homeTacticName;
    
    /**
     * Name of the away team's tactic.
     */
    private String awayTacticName;
}
