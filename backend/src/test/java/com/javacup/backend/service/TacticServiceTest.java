package com.javacup.backend.service;

import com.javacup.model.Tactic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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

    /**
     * Tests that getAvailableTactics returns a non-null set.
     */
    @Test
    void testGetAvailableTacticsReturnsNonNull() {
        Set<String> tactics = tacticService.getAvailableTactics();
        assertNotNull(tactics, "Available tactics set should not be null");
    }

    /**
     * Tests that getAvailableTactics returns a non-empty set.
     */
    @Test
    void testGetAvailableTacticsReturnsNonEmpty() {
        Set<String> tactics = tacticService.getAvailableTactics();
        assertFalse(tactics.isEmpty(), "Available tactics set should not be empty");
    }

    /**
     * Tests that getAvailableTactics returns the same tactics as getAllTactics.
     */
    @Test
    void testGetAvailableTacticsMatchesGetAllTactics() {
        Set<String> availableTactics = tacticService.getAvailableTactics();
        List<String> allTactics = tacticService.getAllTactics();
        
        assertEquals(allTactics.size(), availableTactics.size(), 
                    "Available tactics count should match all tactics count");
        
        for (String tactic : allTactics) {
            assertTrue(availableTactics.contains(tactic), 
                      "Available tactics should contain: " + tactic);
        }
    }

    /**
     * Tests that loadTactic successfully loads a valid tactic.
     */
    @Test
    void testLoadTacticWithValidName() {
        List<String> tactics = tacticService.getAllTactics();
        assertFalse(tactics.isEmpty(), "Should have at least one tactic to test");
        
        String tacticName = tactics.get(0);
        
        try {
            Tactic tactic = tacticService.loadTactic(tacticName);
            assertNotNull(tactic, "Loaded tactic should not be null");
        } catch (TacticService.TacticNotFoundException e) {
            fail("Should not throw TacticNotFoundException for valid tactic: " + tacticName);
        }
    }

    /**
     * Tests that loadTactic returns an instance that implements Tactic interface.
     */
    @Test
    void testLoadTacticReturnsCorrectType() {
        List<String> tactics = tacticService.getAllTactics();
        assertFalse(tactics.isEmpty(), "Should have at least one tactic to test");
        
        String tacticName = tactics.get(0);
        
        try {
            Tactic tactic = tacticService.loadTactic(tacticName);
            assertTrue(tactic instanceof Tactic, 
                      "Loaded object should be an instance of Tactic");
        } catch (TacticService.TacticNotFoundException e) {
            fail("Should not throw TacticNotFoundException for valid tactic: " + tacticName);
        }
    }

    /**
     * Tests that loadTactic can load all available tactics.
     */
    @Test
    void testLoadTacticForAllAvailableTactics() {
        List<String> tactics = tacticService.getAllTactics();
        
        for (String tacticName : tactics) {
            try {
                Tactic tactic = tacticService.loadTactic(tacticName);
                assertNotNull(tactic, "Loaded tactic should not be null for: " + tacticName);
                assertNotNull(tactic.getDetail(), "Tactic detail should not be null for: " + tacticName);
            } catch (TacticService.TacticNotFoundException e) {
                fail("Should be able to load tactic: " + tacticName + ", error: " + e.getMessage());
            }
        }
    }

    /**
     * Tests that loadTactic throws TacticNotFoundException for invalid tactic name.
     */
    @Test
    void testLoadTacticWithInvalidName() {
        String invalidTacticName = "nonexistent_tactic_12345";
        
        TacticService.TacticNotFoundException exception = assertThrows(
                TacticService.TacticNotFoundException.class,
                () -> tacticService.loadTactic(invalidTacticName),
                "Should throw TacticNotFoundException for invalid tactic name"
        );
        
        assertTrue(exception.getMessage().contains(invalidTacticName), 
                  "Exception message should contain the invalid tactic name");
    }

    /**
     * Tests that loadTactic throws TacticNotFoundException for null tactic name.
     */
    @Test
    void testLoadTacticWithNullName() {
        TacticService.TacticNotFoundException exception = assertThrows(
                TacticService.TacticNotFoundException.class,
                () -> tacticService.loadTactic(null),
                "Should throw TacticNotFoundException for null tactic name"
        );
        
        assertTrue(exception.getMessage().contains("null") || 
                   exception.getMessage().contains("empty"), 
                  "Exception message should mention null or empty");
    }

    /**
     * Tests that loadTactic throws TacticNotFoundException for empty tactic name.
     */
    @Test
    void testLoadTacticWithEmptyName() {
        TacticService.TacticNotFoundException exception = assertThrows(
                TacticService.TacticNotFoundException.class,
                () -> tacticService.loadTactic(""),
                "Should throw TacticNotFoundException for empty tactic name"
        );
        
        assertTrue(exception.getMessage().contains("empty") || 
                   exception.getMessage().contains("null"), 
                  "Exception message should mention empty or null");
    }

    /**
     * Tests that loadTactic throws TacticNotFoundException for whitespace-only tactic name.
     */
    @Test
    void testLoadTacticWithWhitespaceOnlyName() {
        TacticService.TacticNotFoundException exception = assertThrows(
                TacticService.TacticNotFoundException.class,
                () -> tacticService.loadTactic("   "),
                "Should throw TacticNotFoundException for whitespace-only tactic name"
        );
        
        assertTrue(exception.getMessage().contains("empty") || 
                   exception.getMessage().contains("null"), 
                  "Exception message should mention empty or null");
    }

    /**
     * Tests that loadTactic creates a new instance each time it's called.
     */
    @Test
    void testLoadTacticCreatesNewInstances() {
        List<String> tactics = tacticService.getAllTactics();
        assertFalse(tactics.isEmpty(), "Should have at least one tactic to test");
        
        String tacticName = tactics.get(0);
        
        try {
            Tactic tactic1 = tacticService.loadTactic(tacticName);
            Tactic tactic2 = tacticService.loadTactic(tacticName);
            
            assertNotSame(tactic1, tactic2, 
                         "loadTactic should create new instances, not return the same object");
        } catch (TacticService.TacticNotFoundException e) {
            fail("Should not throw TacticNotFoundException for valid tactic: " + tacticName);
        }
    }
}
