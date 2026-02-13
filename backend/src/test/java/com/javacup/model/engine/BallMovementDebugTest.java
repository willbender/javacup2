package com.javacup.model.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Debug test to understand why the ball isn't moving properly.
 */
@DisplayName("Ball Movement Debug Test")
class BallMovementDebugTest {

    private static final Logger logger = LoggerFactory.getLogger(BallMovementDebugTest.class);

    @Test
    @DisplayName("Debug ball velocity after kick")
    void testBallVelocityAfterKick() throws Exception {
        logger.info("=== DEBUGGING BALL VELOCITY ===");
        
        SimpleTactic home = new SimpleTactic("Home", java.awt.Color.BLUE, java.awt.Color.WHITE);
        SimpleTactic away = new SimpleTactic("Away", java.awt.Color.RED, java.awt.Color.YELLOW);
        
        Match match = new Match(home, away, false);
        
        logger.info("After constructor (first iterate() called):");
        logger.info("  Ball: {}", match.getVisibleBallPosition());
        logger.info("  Iteration: {}", match.getIteration());
        
        // Iterate a few times and watch ball movement closely
        for (int i = 0; i < 20; i++) {
            var prevBall = match.getVisibleBallPosition();
            match.iterate();
            var newBall = match.getVisibleBallPosition();
            double distance = prevBall.distance(newBall);
            
            // Calculate velocity in m/s (assuming 60 iterations per second)
            double velocityMPS = distance * 60.0;
            
            logger.info("Iter {}: Ball at {}, moved {:.4f}m this iteration ({:.2f} m/s)", 
                       match.getIteration(), newBall, distance, velocityMPS);
            
            if (match.isKicking()) {
                logger.info("  >>> KICK! <<<");
            }
        }
        
        logger.info("=== END DEBUG ===");
    }
}
