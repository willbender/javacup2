package com.javacup.model.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the SavedMatch functionality.
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>Matches can be recorded properly</li>
 *   <li>All iteration data is captured</li>
 *   <li>Match data can be serialized to JSON</li>
 *   <li>Saved matches can be replayed</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@DisplayName("SavedMatch Tests")
class SavedMatchTest {

    private static final Logger logger = LoggerFactory.getLogger(SavedMatchTest.class);
    
    private SimpleTactic homeTeam;
    private SimpleTactic awayTeam;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        homeTeam = new SimpleTactic("Barcelona", Color.BLUE, Color.RED);
        awayTeam = new SimpleTactic("Real Madrid", Color.WHITE, Color.BLUE);
        
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Increase nesting depth limit
        objectMapper.getFactory().setStreamReadConstraints(
            com.fasterxml.jackson.core.StreamReadConstraints.builder()
                .maxNestingDepth(2000)
                .build()
        );
        objectMapper.getFactory().setStreamWriteConstraints(
            com.fasterxml.jackson.core.StreamWriteConstraints.builder()
                .maxNestingDepth(2000)
                .build()
        );
    }

    @Test
    @DisplayName("Match can be recorded with iterations")
    void testMatchRecording() throws Exception {
        logger.info("Testing match recording...");
        
        // Create match with recording enabled
        Match match = new Match(homeTeam, awayTeam, true);
        
        assertTrue(match.wasRecorded(), "Match should be recorded");
        assertNotNull(match.getSavedMatch(), "Saved match should exist");
        
        // Run some iterations
        int iterationsToRun = 100;
        for (int i = 0; i < iterationsToRun; i++) {
            match.iterate();
        }
        
        // Finalize the match
        match.finalizeSavedMatch();
        
        SavedMatch savedMatch = match.getSavedMatch();
        assertNotNull(savedMatch, "Saved match should not be null");
        assertTrue(savedMatch.getTotalIterations() > 0, "Should have recorded iterations");
        
        logger.info("Match recorded {} iterations", savedMatch.getTotalIterations());
        logger.info("Final score: {} - {}", savedMatch.getFinalHomeGoals(), savedMatch.getFinalAwayGoals());
        logger.info("Final possession: {:.1f}%", savedMatch.getFinalHomePossession() * 100);
    }

    @Test
    @DisplayName("SavedMatch contains complete match information")
    void testSavedMatchContent() throws Exception {
        logger.info("Testing saved match content...");
        
        Match match = new Match(homeTeam, awayTeam, true);
        
        // Run a short match (200 iterations ~= 3 seconds)
        for (int i = 0; i < 200; i++) {
            match.iterate();
        }
        
        match.finalizeSavedMatch();
        SavedMatch savedMatch = match.getSavedMatch();
        
        // Verify team details are saved
        assertNotNull(savedMatch.getHomeDetail(), "Home team detail should be saved");
        assertNotNull(savedMatch.getAwayDetail(), "Away team detail should be saved");
        assertEquals("Barcelona", savedMatch.getHomeDetail().getTacticName());
        assertEquals("Real Madrid", savedMatch.getAwayDetail().getTacticName());
        
        // Verify iterations are saved
        assertTrue(savedMatch.getTotalIterations() > 0, "Should have saved iterations");
        
        // Verify final results are saved
        assertTrue(savedMatch.getFinalHomeGoals() >= 0, "Home goals should be non-negative");
        assertTrue(savedMatch.getFinalAwayGoals() >= 0, "Away goals should be non-negative");
        assertTrue(savedMatch.getFinalHomePossession() >= 0.0 && 
                  savedMatch.getFinalHomePossession() <= 1.0, 
                  "Possession should be between 0 and 1");
        
        // Verify iteration contains positions
        Iteration firstIteration = savedMatch.getIterationAt(0);
        assertNotNull(firstIteration, "First iteration should exist");
        assertNotNull(firstIteration.getPositions(), "Positions should be saved");
        
        logger.info("Saved match contains {} iterations", savedMatch.getTotalIterations());
    }

    @Test
    @DisplayName("SavedMatch can be serialized to JSON")
    void testJsonSerialization() throws Exception {
        logger.info("Testing JSON serialization...");
        
        Match match = new Match(homeTeam, awayTeam, true);
        
        // Run a short match
        for (int i = 0; i < 150; i++) {
            match.iterate();
        }
        
        match.finalizeSavedMatch();
        SavedMatch savedMatch = match.getSavedMatch();
        
        // Serialize to JSON
        String json = objectMapper.writeValueAsString(savedMatch);
        
        assertNotNull(json, "JSON should not be null");
        assertTrue(json.length() > 0, "JSON should not be empty");
        assertTrue(json.contains("\"homeTeam\""), "JSON should contain homeTeam");
        assertTrue(json.contains("\"awayTeam\""), "JSON should contain awayTeam");
        assertTrue(json.contains("\"iterations\""), "JSON should contain iterations");
        assertTrue(json.contains("\"finalHomeGoals\""), "JSON should contain finalHomeGoals");
        assertTrue(json.contains("\"finalAwayGoals\""), "JSON should contain finalAwayGoals");
        
        logger.info("JSON serialization successful, size: {} characters", json.length());
        logger.info("Sample JSON (first 500 chars):\n{}", json.substring(0, Math.min(500, json.length())));
    }

    @Test
    @DisplayName("SavedMatch JSON can be written to file")
    void testJsonToFile() throws Exception {
        logger.info("Testing JSON file output...");
        
        Match match = new Match(homeTeam, awayTeam, true);
        
        // Run a short match (300 iterations = 5 seconds of game time)
        for (int i = 0; i < 300; i++) {
            match.iterate();
            
            // Log progress every 100 iterations
            if ((i + 1) % 100 == 0) {
                logger.info("Iteration {}: Score {}:{}", 
                           match.getIteration(), 
                           match.getHomeGoals(), 
                           match.getAwayGoals());
            }
        }
        
        match.finalizeSavedMatch();
        SavedMatch savedMatch = match.getSavedMatch();
        
        // Create output directory if it doesn't exist
        String outputDir = "/tmp/javacup-test-output";
        Files.createDirectories(Paths.get(outputDir));
        
        // Write to file
        String outputPath = outputDir + "/saved-match.json";
        File outputFile = new File(outputPath);
        objectMapper.writeValue(outputFile, savedMatch);
        
        assertTrue(outputFile.exists(), "Output file should be created");
        assertTrue(outputFile.length() > 0, "Output file should not be empty");
        
        logger.info("=== Match Saved Successfully ===");
        logger.info("Output file: {}", outputFile.getAbsolutePath());
        logger.info("File size: {} bytes", outputFile.length());
        logger.info("Total iterations: {}", savedMatch.getTotalIterations());
        logger.info("Final score: {} - {}", 
                   savedMatch.getFinalHomeGoals(), 
                   savedMatch.getFinalAwayGoals());
        logger.info("Final possession: Home {:.1f}% - Away {:.1f}%",
                   savedMatch.getFinalHomePossession() * 100,
                   (1 - savedMatch.getFinalHomePossession()) * 100);
        logger.info("================================");
        
        // Note: Deserialization back from JSON would require a no-arg constructor
        // and custom deserializers for TacticDetail. For now, the JSON is successfully
        // written and can be used for other purposes.
        logger.info("JSON file successfully written at: {}", outputPath);
    }

    @Test
    @DisplayName("SavedMatch can replay iterations")
    void testMatchReplay() throws Exception {
        logger.info("Testing match replay...");
        
        Match match = new Match(homeTeam, awayTeam, true);
        
        // Run some iterations
        for (int i = 0; i < 100; i++) {
            match.iterate();
        }
        
        match.finalizeSavedMatch();
        SavedMatch savedMatch = match.getSavedMatch();
        
        // Test replay functionality
        int previousIteration = -1;
        for (int i = 0; i < 50; i++) {
            savedMatch.iterate();
            int currentIteration = savedMatch.getIteration();
            assertTrue(currentIteration > previousIteration, 
                      "Iteration should advance during replay");
            previousIteration = currentIteration;
        }
        
        logger.info("Replay successfully advanced through {} iterations", previousIteration);
    }

    @Test
    @DisplayName("SavedMatch preserves event flags")
    void testEventFlagPreservation() throws Exception {
        logger.info("Testing event flag preservation...");
        
        Match match = new Match(homeTeam, awayTeam, true);
        
        boolean sawKick = false;
        boolean sawBounce = false;
        
        // Run match and look for events
        for (int i = 0; i < 200; i++) {
            match.iterate();
            
            if (match.isKicking()) {
                sawKick = true;
                logger.info("Detected kick at iteration {}", match.getIteration());
            }
            if (match.isBouncing()) {
                sawBounce = true;
                logger.info("Detected bounce at iteration {}", match.getIteration());
            }
            
            if (sawKick && sawBounce) {
                break;
            }
        }
        
        match.finalizeSavedMatch();
        SavedMatch savedMatch = match.getSavedMatch();
        
        // Verify events are saved in iterations
        boolean foundKickInSaved = false;
        for (int i = 0; i < savedMatch.getTotalIterations(); i++) {
            Iteration iter = savedMatch.getIterationAt(i);
            if (iter.isKicking()) {
                foundKickInSaved = true;
                logger.info("Found saved kick at iteration {}", i);
                break;
            }
        }
        
        assertTrue(foundKickInSaved || !sawKick, 
                  "Kick events should be preserved in saved match");
        
        logger.info("Event flags successfully preserved in saved match");
    }

    @Test
    @DisplayName("Long match can be saved to JSON")
    void testLongMatchSave() throws Exception {
        logger.info("Testing long match save...");
        
        Match match = new Match(homeTeam, awayTeam, true);
        
        // Run a longer match (600 iterations = 10 seconds of game time)
        for (int i = 0; i < 600; i++) {
            match.iterate();
            
            if ((i + 1) % 200 == 0) {
                logger.info("Progress: {} iterations completed", i + 1);
            }
        }
        
        match.finalizeSavedMatch();
        SavedMatch savedMatch = match.getSavedMatch();
        
        // Create output directory
        String outputDir = "/tmp/javacup-test-output";
        Files.createDirectories(Paths.get(outputDir));
        
        // Write to file
        String outputPath = outputDir + "/long-match.json";
        File outputFile = new File(outputPath);
        objectMapper.writeValue(outputFile, savedMatch);
        
        assertTrue(outputFile.exists(), "Output file should be created");
        
        logger.info("=== Long Match Saved ===");
        logger.info("Output file: {}", outputFile.getAbsolutePath());
        logger.info("File size: {} KB", outputFile.length() / 1024);
        logger.info("Total iterations: {}", savedMatch.getTotalIterations());
        logger.info("Final score: {} - {}", 
                   savedMatch.getFinalHomeGoals(), 
                   savedMatch.getFinalAwayGoals());
        logger.info("========================");
    }
}
