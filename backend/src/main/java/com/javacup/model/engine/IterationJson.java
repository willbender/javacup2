package com.javacup.model.engine;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON-friendly representation of an Iteration for serialization.
 * <p>
 * This class provides a simplified view of iteration data that can be
 * easily serialized to JSON without complex objects or circular references.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, 
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                isGetterVisibility = JsonAutoDetect.Visibility.NONE)
class IterationJson {
    
    @JsonProperty("iteration")
    private final int iteration;
    
    @JsonProperty("homeGoals")
    private final int homeGoals;
    
    @JsonProperty("awayGoals")
    private final int awayGoals;
    
    @JsonProperty("homePossession")
    private final double homePossession;
    
    @JsonProperty("goal")
    private final boolean goal;
    
    @JsonProperty("kicking")
    private final boolean kicking;
    
    @JsonProperty("whistling")
    private final boolean whistling;
    
    @JsonProperty("ballHeight")
    private final double ballHeight;
    
    @JsonProperty("ballX")
    private final double ballX;
    
    @JsonProperty("ballY")
    private final double ballY;
    
    @JsonProperty("homePlayerX")
    private final double[] homePlayerX;
    
    @JsonProperty("homePlayerY")
    private final double[] homePlayerY;
    
    @JsonProperty("awayPlayerX")
    private final double[] awayPlayerX;
    
    @JsonProperty("awayPlayerY")
    private final double[] awayPlayerY;
    
    /**
     * Creates a JSON-friendly version of an Iteration.
     *
     * @param iteration the iteration to convert
     */
    public IterationJson(Iteration iteration) {
        this.iteration = iteration.getIteration();
        this.homeGoals = iteration.getHomeGoals();
        this.awayGoals = iteration.getAwayGoals();
        this.homePossession = Iteration.decompress(iteration.getHomePossessionPercent());
        
        // Event flags
        this.goal = iteration.isGoal();
        this.kicking = iteration.isKicking();
        this.whistling = iteration.isWhistling();
        
        // Ball state
        this.ballHeight = Iteration.decompress(iteration.getBallHeight());
        this.ballX = Iteration.decompress(iteration.getVisibleBallX());
        this.ballY = Iteration.decompress(iteration.getVisibleBallY());
        
        // Player positions
        short[][][] positions = iteration.getPositions();
        
        this.homePlayerX = new double[11];
        this.homePlayerY = new double[11];
        this.awayPlayerX = new double[11];
        this.awayPlayerY = new double[11];
        
        for (int i = 0; i < 11; i++) {
            this.homePlayerX[i] = Iteration.decompress(positions[0][i][0]);
            this.homePlayerY[i] = Iteration.decompress(positions[0][i][1]);
            this.awayPlayerX[i] = Iteration.decompress(positions[1][i][0]);
            this.awayPlayerY[i] = Iteration.decompress(positions[1][i][1]);
        }
    }
}
