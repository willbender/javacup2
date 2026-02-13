package com.javacup.model.engine;

import com.javacup.model.Tactic;
import com.javacup.tactics.masia13.Masia;
import com.javacup.tactics.pistachos.Pistachos;
import com.javacup.tactics.romedal.RomedalTeam;
import com.javacup.tactics.twentythree.Team2313;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parameterized tests for migrated tactics.
 * <p>
 * This test class verifies that the migrated tactics from javacup2013 work correctly
 * in the new architecture. It runs matches between all combinations of migrated tactics
 * and checks that:
 * <ul>
 *   <li>Matches can be created without errors</li>
 *   <li>Matches run to completion without crashes</li>
 *   <li>Goals are scored (not all matches end 0-0)</li>
 *   <li>Tactics execute their logic correctly</li>
 * </ul>
 * </p>
 * <p>
 * The test uses a parameterized approach to test all possible matchups between
 * the four migrated tactics: Masia13, Romedal, TwentyThree, and Pistachos.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@DisplayName("Migrated Tactics Match Tests")
class MigratedTacticsMatchTest {

    private static final Logger logger = LoggerFactory.getLogger(MigratedTacticsMatchTest.class);
    
    /**
     * Provides all possible matchups between migrated tactics.
     * Each argument is a pair of tactics (home, away).
     */
    static Stream<Arguments> tacticMatchups() {
        return Stream.of(
            // Masia13 vs others
            Arguments.of(new Masia(), new RomedalTeam(), "Masia13", "Romedal"),
            Arguments.of(new Masia(), new Team2313(), "Masia13", "TwentyThree"),
            Arguments.of(new Masia(), new Pistachos(), "Masia13", "Pistachos"),
            
            // Romedal vs others (excluding already tested combinations)
            Arguments.of(new RomedalTeam(), new Masia(), "Romedal", "Masia13"),
            Arguments.of(new RomedalTeam(), new Team2313(), "Romedal", "TwentyThree"),
            Arguments.of(new RomedalTeam(), new Pistachos(), "Romedal", "Pistachos"),
            
            // TwentyThree vs others (excluding already tested combinations)
            Arguments.of(new Team2313(), new Masia(), "TwentyThree", "Masia13"),
            Arguments.of(new Team2313(), new RomedalTeam(), "TwentyThree", "Romedal"),
            Arguments.of(new Team2313(), new Pistachos(), "TwentyThree", "Pistachos"),
            
            // Pistachos vs others (excluding already tested combinations)
            Arguments.of(new Pistachos(), new Masia(), "Pistachos", "Masia13"),
            Arguments.of(new Pistachos(), new RomedalTeam(), "Pistachos", "Romedal"),
            Arguments.of(new Pistachos(), new Team2313(), "Pistachos", "TwentyThree")
        );
    }

    @ParameterizedTest(name = "{2} vs {3}")
    @MethodSource("tacticMatchups")
    @DisplayName("Migrated tactics can play a full match")
    void testMigratedTacticsMatch(Tactic homeTactic, Tactic awayTactic, 
                                   String homeName, String awayName) throws Exception {
        logger.info("==========================================");
        logger.info("Starting match: {} vs {}", homeName, awayName);
        logger.info("==========================================");
        
        // Create match
        Match match = new Match(homeTactic, awayTactic, false);
        
        assertNotNull(match, "Match should be created");
        logger.info("Match created successfully");
        
        // Run full match (3600 iterations = 60 seconds at 60 FPS)
        int totalIterations = 3600;
        int logInterval = 600; // Log every 10 seconds
        
        for (int i = 0; i < totalIterations; i++) {
            try {
                match.iterate();
                
                // Log progress periodically
                if ((i + 1) % logInterval == 0) {
                    logger.info("Iteration {}/{}: Score {}:{}, Ball at ({:.1f}, {:.1f}), Altitude {:.2f}m",
                               i + 1, totalIterations,
                               match.getHomeGoals(), match.getAwayGoals(),
                               match.getVisibleBallPosition().getX(),
                               match.getVisibleBallPosition().getY(),
                               match.getBallAltitude());
                }
                
                // Log goals when they happen
                if (match.isGoal()) {
                    logger.info("⚽ GOAL! at iteration {} - Current score: {}:{}",
                               match.getIteration(),
                               match.getHomeGoals(),
                               match.getAwayGoals());
                }
                
            } catch (Exception e) {
                logger.error("Match crashed at iteration {}", i + 1, e);
                fail("Match should not crash during execution: " + e.getMessage());
            }
        }
        
        int finalHomeGoals = match.getHomeGoals();
        int finalAwayGoals = match.getAwayGoals();
        int totalGoals = finalHomeGoals + finalAwayGoals;
        
        logger.info("==========================================");
        logger.info("FINAL SCORE: {} {} - {} {}", 
                   homeName, finalHomeGoals, 
                   finalAwayGoals, awayName);
        logger.info("Total goals: {}", totalGoals);
        logger.info("Home possession: {:.1f}%", match.getHomePossession() * 100);
        logger.info("==========================================");
        
        // Verify match completed
        assertTrue(match.getIteration() >= totalIterations, 
                  "Match should complete all iterations");
        
        // Verify goals are within reasonable range
        assertTrue(finalHomeGoals >= 0, "Home goals should not be negative");
        assertTrue(finalAwayGoals >= 0, "Away goals should not be negative");
        assertTrue(totalGoals < 50, "Total goals should be reasonable (less than 50)");
    }

    @ParameterizedTest(name = "Short match: {2} vs {3}")
    @MethodSource("tacticMatchups")
    @DisplayName("Migrated tactics can play a short match (10 seconds)")
    void testShortMatch(Tactic homeTactic, Tactic awayTactic,
                        String homeName, String awayName) throws Exception {
        logger.info("Short match: {} vs {}", homeName, awayName);
        
        Match match = new Match(homeTactic, awayTactic, false);
        
        // Run 600 iterations (10 seconds)
        for (int i = 0; i < 600; i++) {
            match.iterate();
        }
        
        logger.info("Short match completed: {} {} - {} {}", 
                   homeName, match.getHomeGoals(),
                   match.getAwayGoals(), awayName);
        
        assertTrue(match.getIteration() >= 600, "Match should complete");
        assertTrue(match.getHomeGoals() >= 0 && match.getAwayGoals() >= 0, 
                  "Goals should be valid");
    }

    @DisplayName("At least some matches should have goals scored")
    @ParameterizedTest(name = "Goal scoring test: {2} vs {3}")
    @MethodSource("tacticMatchups")
    void testGoalsAreScored(Tactic homeTactic, Tactic awayTactic,
                            String homeName, String awayName) throws Exception {
        logger.info("Testing goal scoring: {} vs {}", homeName, awayName);
        
        Match match = new Match(homeTactic, awayTactic, false);
        
        boolean goalScored = false;
        int maxIterations = 3600;
        
        for (int i = 0; i < maxIterations; i++) {
            match.iterate();
            
            if (match.getHomeGoals() > 0 || match.getAwayGoals() > 0) {
                goalScored = true;
                logger.info("✓ Goal scored at iteration {} in {} vs {}", 
                           match.getIteration(), homeName, awayName);
                break;
            }
        }
        
        int totalGoals = match.getHomeGoals() + match.getAwayGoals();
        logger.info("Final score {} vs {}: {} - {} (Total: {})", 
                   homeName, awayName, 
                   match.getHomeGoals(), match.getAwayGoals(), totalGoals);
        
        // Note: We don't assert goalScored here because some matches legitimately 
        // could end 0-0, but we log it for analysis. The aggregate test below will
        // check that goals are scored across all matches.
    }

    @DisplayName("Aggregate test: Goals should be scored across all matches")
    @org.junit.jupiter.api.Test
    void testAggregateGoalScoring() throws Exception {
        logger.info("===========================================");
        logger.info("AGGREGATE GOAL SCORING TEST");
        logger.info("===========================================");
        
        int totalMatches = 0;
        int matchesWithGoals = 0;
        int totalGoalsScored = 0;
        
        // Test all matchups
        Stream<Arguments> matchups = tacticMatchups();
        
        for (Arguments args : (Iterable<Arguments>) matchups::iterator) {
            Tactic home = (Tactic) args.get()[0];
            Tactic away = (Tactic) args.get()[1];
            String homeName = (String) args.get()[2];
            String awayName = (String) args.get()[3];
            
            Match match = new Match(home, away, false);
            
            // Run full match
            for (int i = 0; i < 3600; i++) {
                match.iterate();
            }
            
            int matchGoals = match.getHomeGoals() + match.getAwayGoals();
            totalMatches++;
            totalGoalsScored += matchGoals;
            
            if (matchGoals > 0) {
                matchesWithGoals++;
            }
            
            logger.info("{} vs {}: {} - {} (Total: {})", 
                       homeName, awayName,
                       match.getHomeGoals(), match.getAwayGoals(), matchGoals);
        }
        
        logger.info("===========================================");
        logger.info("AGGREGATE RESULTS:");
        logger.info("Total matches: {}", totalMatches);
        logger.info("Matches with goals: {}", matchesWithGoals);
        logger.info("Matches without goals: {}", totalMatches - matchesWithGoals);
        logger.info("Total goals scored: {}", totalGoalsScored);
        logger.info("Average goals per match: {:.2f}", (double) totalGoalsScored / totalMatches);
        logger.info("Percentage of matches with goals: {:.1f}%", 
                   (double) matchesWithGoals / totalMatches * 100);
        logger.info("===========================================");
        
        // Assert that at least half of the matches have goals
        // This ensures the tactics are working properly
        assertTrue(matchesWithGoals >= totalMatches / 2,
                  String.format("At least half of matches should have goals. " +
                               "Only %d out of %d matches had goals. " +
                               "This suggests a problem with the migration.",
                               matchesWithGoals, totalMatches));
        
        // Assert that some goals were scored overall
        assertTrue(totalGoalsScored > 0,
                  "No goals were scored in any match! This indicates a serious problem " +
                  "with the migration of either the core engine or the tactics.");
    }
}
