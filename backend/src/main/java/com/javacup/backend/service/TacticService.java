package com.javacup.backend.service;

import com.javacup.model.Tactic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    /**
     * Constructor that initializes the service by discovering all available tactics.
     * Scans the classpath for classes implementing the Tactic interface under
     * the com.javacup.tactics package.
     */
    public TacticService() {
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
                    // Extract the package name (first level after com.javacup.tactics)
                    String packagePath = className.substring(TACTICS_PACKAGE.length() + 1);
                    if (packagePath.contains(".")) {
                        String tacticPackage = packagePath.substring(0, packagePath.indexOf('.'));
                        if (!tactics.contains(tacticPackage)) {
                            tactics.add(tacticPackage);
                        }
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
}
