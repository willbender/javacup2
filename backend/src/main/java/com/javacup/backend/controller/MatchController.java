package com.javacup.backend.controller;

import com.javacup.backend.dto.MatchRequest;
import com.javacup.backend.service.TacticService;
import com.javacup.model.Tactic;
import com.javacup.model.engine.Match;
import com.javacup.model.engine.SavedMatch;
import com.javacup.model.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for match operations.
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Slf4j
public class MatchController {

    private final TacticService tacticService;

    /**
     * Runs a match between two tactics.
     *
     * @param request the match request with tactic names
     * @return the saved match with all match data
     */
    @PostMapping("/run")
    public ResponseEntity<?> runMatch(@RequestBody MatchRequest request) {
        log.info("Running match: {} vs {}", request.getHomeTacticName(), request.getAwayTacticName());
        
        try {
            // Validate both tactics exist
            List<String> missingTactics = new ArrayList<>();
            
            if (!tacticService.getAllTactics().contains(request.getHomeTacticName())) {
                missingTactics.add(request.getHomeTacticName());
            }
            
            if (!tacticService.getAllTactics().contains(request.getAwayTacticName())) {
                missingTactics.add(request.getAwayTacticName());
            }
            
            if (!missingTactics.isEmpty()) {
                String message = "Tactic(s) not found: " + String.join(", ", missingTactics);
                log.warn(message);
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", message);
                errorResponse.put("missingTactics", missingTactics);
                errorResponse.put("availableTactics", tacticService.getAllTactics());
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Load tactics
            Tactic homeTactic = tacticService.loadTactic(request.getHomeTacticName());
            Tactic awayTactic = tacticService.loadTactic(request.getAwayTacticName());
            
            // Create and run match with recording enabled
            Match match = new Match(homeTactic, awayTactic, true);
            
            // Run for full match duration (60 seconds * 60 iterations per second = 3600 iterations)
            // Match constructor already runs the first iteration, so we run TOTAL_ITERATIONS - 1 more
            for (int i = 1; i < Constants.TOTAL_ITERATIONS; i++) {
                match.iterate();
            }
            
            // Finalize match
            match.finalizeSavedMatch();
            
            // Get the saved match with all data
            SavedMatch savedMatch = match.getSavedMatch();
            
            log.info("Match completed: {} {} - {} {}", 
                    savedMatch.getHomeDetail().getTacticName(), savedMatch.getFinalHomeGoals(),
                    savedMatch.getFinalAwayGoals(), savedMatch.getAwayDetail().getTacticName());
            
            return ResponseEntity.ok(savedMatch);
            
        } catch (TacticService.TacticNotFoundException e) {
            log.error("Tactic not found", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("availableTactics", tacticService.getAllTactics());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error running match", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Gets available tactics.
     *
     * @return list of available tactic names
     */
    @GetMapping("/tactics")
    public ResponseEntity<Map<String, Object>> getAvailableTactics() {
        log.debug("Getting available tactics");
        
        Set<String> tactics = tacticService.getAvailableTactics();
        Map<String, Object> response = new HashMap<>();
        response.put("tactics", tactics);
        response.put("count", tactics.size());
        
        return ResponseEntity.ok(response);
    }
}
