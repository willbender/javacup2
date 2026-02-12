package com.javacup.backend.service;

import com.javacup.model.Tactic;
import com.javacup.model.engine.SimpleTactic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.*;

/**
 * Service for managing and loading tactics by name.
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Service
@Slf4j
public class TacticService {

    private final Map<String, TacticFactory> tactics = new HashMap<>();

    /**
     * Constructor that registers available tactics.
     */
    public TacticService() {
        // Register example tactics
        registerTactic("SimpleTactic", () -> new SimpleTactic("SimpleTactic", Color.BLUE, Color.WHITE));
        registerTactic("DefaultHome", () -> new SimpleTactic("DefaultHome", Color.BLUE, Color.WHITE));
        registerTactic("DefaultAway", () -> new SimpleTactic("DefaultAway", Color.RED, Color.YELLOW));
        
        log.info("TacticService initialized with {} tactics", tactics.size());
    }

    /**
     * Loads a tactic by name.
     *
     * @param name the name of the tactic
     * @return the tactic instance
     * @throws TacticNotFoundException if the tactic doesn't exist
     */
    public Tactic loadTactic(String name) {
        log.debug("Loading tactic: {}", name);
        
        TacticFactory factory = tactics.get(name);
        if (factory == null) {
            log.warn("Tactic not found: {}", name);
            throw new TacticNotFoundException("Tactic not found: " + name);
        }
        
        return factory.create();
    }

    /**
     * Gets all available tactic names.
     *
     * @return set of tactic names
     */
    public Set<String> getAvailableTactics() {
        return Collections.unmodifiableSet(tactics.keySet());
    }

    /**
     * Checks if a tactic exists.
     *
     * @param name the name of the tactic
     * @return true if the tactic exists
     */
    public boolean tacticExists(String name) {
        return tactics.containsKey(name);
    }

    /**
     * Registers a tactic factory.
     *
     * @param name the name of the tactic
     * @param factory the factory to create tactic instances
     */
    private void registerTactic(String name, TacticFactory factory) {
        tactics.put(name, factory);
        log.debug("Registered tactic: {}", name);
    }

    /**
     * Functional interface for creating tactic instances.
     */
    @FunctionalInterface
    private interface TacticFactory {
        Tactic create();
    }

    /**
     * Exception thrown when a tactic is not found.
     */
    public static class TacticNotFoundException extends RuntimeException {
        public TacticNotFoundException(String message) {
            super(message);
        }
    }
}
