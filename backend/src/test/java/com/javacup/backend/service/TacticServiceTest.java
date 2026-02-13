package com.javacup.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TacticService.
 * <p>
 * Tests the business logic for tactics management.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
class TacticServiceTest {

    private TacticService tacticService;

    @BeforeEach
    void setUp() {
        tacticService = new TacticService();
    }

    /**
     * Tests that getAllTactics returns a non-null list.
     */
    @Test
    void testGetAllTacticsReturnsNonNull() {
        List<String> tactics = tacticService.getAllTactics();
        assertNotNull(tactics, "Tactics list should not be null");
    }

    /**
     * Tests that getAllTactics returns a non-empty list.
     */
    @Test
    void testGetAllTacticsReturnsNonEmpty() {
        List<String> tactics = tacticService.getAllTactics();
        assertFalse(tactics.isEmpty(), "Tactics list should not be empty");
    }

    /**
     * Tests that getAllTactics returns expected number of tactics.
     * Note: The count depends on tactics present in com.javacup.tactics package.
     */
    @Test
    void testGetAllTacticsReturnsExpectedCount() {
        List<String> tactics = tacticService.getAllTactics();
        // Should return at least 1 tactic and not more than 100
        assertTrue(tactics.size() >= 1, "Should return at least 1 tactic");
        assertTrue(tactics.size() <= 100, "Should not return more than 100 tactics");
    }

    /**
     * Tests that getAllTactics contains specific known tactics from the migrated set.
     */
    @Test
    void testGetAllTacticsContainsKnownTactics() {
        List<String> tactics = tacticService.getAllTactics();
        
        // Check for at least some of the migrated tactics
        assertTrue(tactics.contains("masia13") || tactics.contains("pistachos") || 
                   tactics.contains("romedal") || tactics.contains("twentythree"), 
                   "Should contain at least one of the migrated tactics");
    }

    /**
     * Tests that all tactic names are non-null and non-empty.
     */
    @Test
    void testAllTacticNamesAreValid() {
        List<String> tactics = tacticService.getAllTactics();
        
        for (String tactic : tactics) {
            assertNotNull(tactic, "Tactic name should not be null");
            assertFalse(tactic.trim().isEmpty(), "Tactic name should not be empty");
        }
    }

    /**
     * Tests that there are no duplicate tactic names.
     */
    @Test
    void testNoDuplicateTacticNames() {
        List<String> tactics = tacticService.getAllTactics();
        long distinctCount = tactics.stream().distinct().count();
        
        assertEquals(tactics.size(), distinctCount, "All tactic names should be unique");
    }

    /**
     * Tests that tactic names follow expected naming conventions.
     * Tactic names are package names and should be lowercase without spaces.
     */
    @Test
    void testTacticNamesFollowConventions() {
        List<String> tactics = tacticService.getAllTactics();
        
        for (String tactic : tactics) {
            // Check that names don't contain spaces
            assertFalse(tactic.contains(" "), 
                       "Tactic name '" + tactic + "' should not contain spaces");
            
            // Check that names are valid package identifiers (lowercase, alphanumeric, underscores)
            assertTrue(tactic.matches("[a-z][a-z0-9_]*"), 
                      "Tactic name '" + tactic + "' should be a valid package identifier (lowercase alphanumeric with underscores)");
        }
    }
}
