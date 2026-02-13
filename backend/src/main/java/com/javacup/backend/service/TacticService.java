package com.javacup.backend.service;

import com.javacup.model.Tactic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing tactics information.
 * <p>
 * This service dynamically discovers available tactics by scanning the
 * com.javacup.tactics package for classes that implement the Tactic interface.
 * Each subdirectory in the tactics package represents a unique team AI implementation.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Service
public class TacticService {

    private static final Logger logger = LoggerFactory.getLogger(TacticService.class);
    private static final String TACTICS_PACKAGE = "com.javacup.tactics";
    
    private final List<String> availableTactics;
    private final Map<String, Class<? extends Tactic>> tacticClassMap;

    /**
     * Constructor that initializes the service by discovering all available tactics.
     * Scans the classpath for classes implementing the Tactic interface under
     * the com.javacup.tactics package.
     */
    public TacticService() {
        this.tacticClassMap = new HashMap<>();
        this.availableTactics = discoverTactics();
        logger.info("Discovered {} tactics: {}", availableTactics.size(), availableTactics);
    }

    /**
     * Discovers all available tactics by scanning the tactics package.
     * Each tactic is identified by its package name (subdirectory name).
     * 
     * @return list of discovered tactic package names
     */
    private List<String> discoverTactics() {
        List<String> tactics = new ArrayList<>();
        
        try {
            ClassPathScanningCandidateComponentProvider scanner = 
                new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AssignableTypeFilter(Tactic.class));
            
            // Scan for all classes implementing Tactic interface
            scanner.findCandidateComponents(TACTICS_PACKAGE).forEach(beanDefinition -> {
                String className = beanDefinition.getBeanClassName();
                if (className != null && className.startsWith(TACTICS_PACKAGE + ".")) {
                    try {
                        // Extract the package name (first level after com.javacup.tactics)
                        String packagePath = className.substring(TACTICS_PACKAGE.length() + 1);
                        if (packagePath.contains(".")) {
                            String tacticPackage = packagePath.substring(0, packagePath.indexOf('.'));
                            if (!tactics.contains(tacticPackage)) {
                                tactics.add(tacticPackage);
                                // Load the class and store it in the map
                                @SuppressWarnings("unchecked")
                                Class<? extends Tactic> tacticClass = 
                                    (Class<? extends Tactic>) Class.forName(className);
                                tacticClassMap.put(tacticPackage, tacticClass);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        logger.error("Error loading tactic class: {}", className, e);
                    }
                }
            });
            
            Collections.sort(tactics);
        } catch (Exception e) {
            logger.error("Error discovering tactics", e);
        }
        
        return tactics;
    }

    /**
     * Returns a list of all available tactic names.
     * <p>
     * These tactics are dynamically discovered from the com.javacup.tactics package.
     * Each tactic name corresponds to a subdirectory/package containing a Tactic implementation.
     * </p>
     * 
     * @return unmodifiable list of tactic names
     */
    public List<String> getAllTactics() {
        return Collections.unmodifiableList(availableTactics);
    }

    /**
     * Returns a set of all available tactic names.
     * <p>
     * These tactics are dynamically discovered from the com.javacup.tactics package.
     * Each tactic name corresponds to a subdirectory/package containing a Tactic implementation.
     * </p>
     * 
     * @return unmodifiable set of tactic names
     */
    public Set<String> getAvailableTactics() {
        return Collections.unmodifiableSet(tacticClassMap.keySet());
    }

    /**
     * Loads and instantiates a tactic by its name.
     * <p>
     * The tactic name corresponds to the package name under com.javacup.tactics.
     * This method uses reflection to instantiate the tactic class using its no-arg constructor.
     * </p>
     * 
     * @param tacticName the name of the tactic to load (e.g., "twentythree", "romedal")
     * @return an instance of the requested tactic
     * @throws TacticNotFoundException if the tactic name is not found or cannot be instantiated
     */
    public Tactic loadTactic(String tacticName) throws TacticNotFoundException {
        logger.debug("Loading tactic: {}", tacticName);
        
        if (tacticName == null || tacticName.trim().isEmpty()) {
            throw new TacticNotFoundException("Tactic name cannot be null or empty");
        }
        
        Class<? extends Tactic> tacticClass = tacticClassMap.get(tacticName);
        
        if (tacticClass == null) {
            String message = String.format("Tactic '%s' not found. Available tactics: %s", 
                    tacticName, String.join(", ", availableTactics));
            logger.warn(message);
            throw new TacticNotFoundException(message);
        }
        
        try {
            Tactic tactic = tacticClass.getDeclaredConstructor().newInstance();
            logger.info("Successfully loaded tactic: {}", tacticName);
            return tactic;
        } catch (Exception e) {
            String message = String.format("Failed to instantiate tactic '%s': %s", 
                    tacticName, e.getMessage());
            logger.error(message, e);
            throw new TacticNotFoundException(message, e);
        }
    }

    /**
     * Exception thrown when a tactic cannot be found or loaded.
     */
    public static class TacticNotFoundException extends Exception {
        public TacticNotFoundException(String message) {
            super(message);
        }

        public TacticNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
