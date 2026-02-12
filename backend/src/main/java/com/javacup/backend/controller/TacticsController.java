package com.javacup.backend.controller;

import com.javacup.backend.service.TacticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for tactics management.
 * <p>
 * This controller provides endpoints for retrieving information about
 * available tactics (team AI strategies) that can be used in matches.
 * No authentication is required to access these endpoints.
 * </p>
 * 
 * <h2>Available Endpoints:</h2>
 * <ul>
 *   <li>GET /api/tactics - List all available tactics</li>
 * </ul>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/api")
public class TacticsController {

    private final TacticService tacticService;

    @Autowired
    public TacticsController(TacticService tacticService) {
        this.tacticService = tacticService;
    }

    /**
     * Lists all available tactics.
     * <p>
     * Returns a list of tactic names that can be selected for a match.
     * Each tactic represents a unique team AI implementation with its own
     * strategy, team configuration, and player behavior.
     * </p>
     * 
     * <p><strong>Example Response:</strong></p>
     * <pre>
     * {
     *   "tactics": ["Ciclones", "JGTeam", "Ander", "Cucaracha", ...],
     *   "count": 23
     * }
     * </pre>
     * 
     * @return ResponseEntity containing the list of tactics and count
     */
    @GetMapping("/tactics")
    public ResponseEntity<Map<String, Object>> listTactics() {
        List<String> tactics = tacticService.getAllTactics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("tactics", tactics);
        response.put("count", tactics.size());
        
        return ResponseEntity.ok(response);
    }
}
