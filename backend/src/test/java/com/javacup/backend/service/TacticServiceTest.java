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
     */
    @Test
    void testGetAllTacticsReturnsExpectedCount() {
        List<String> tactics = tacticService.getAllTactics();
        assertEquals(23, tactics.size(), "Should return 23 tactics");
    }

    /**
     * Tests that getAllTactics contains specific known tactics.
     */
    @Test
    void testGetAllTacticsContainsKnownTactics() {
        List<String> tactics = tacticService.getAllTactics();
        
        assertTrue(tactics.contains("Ciclones"), "Should contain Ciclones");
        assertTrue(tactics.contains("JGTeam"), "Should contain JGTeam");
        assertTrue(tactics.contains("Ander"), "Should contain Ander");
        assertTrue(tactics.contains("Jhontona"), "Should contain Jhontona");
        assertTrue(tactics.contains("Valedores"), "Should contain Valedores");
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
     */
    @Test
    void testTacticNamesFollowConventions() {
        List<String> tactics = tacticService.getAllTactics();
        
        for (String tactic : tactics) {
            // Check that names start with uppercase
            assertTrue(Character.isUpperCase(tactic.charAt(0)), 
                      "Tactic name '" + tactic + "' should start with uppercase letter");
            
            // Check that names don't contain spaces
            assertFalse(tactic.contains(" "), 
                       "Tactic name '" + tactic + "' should not contain spaces");
        }
    }
}
