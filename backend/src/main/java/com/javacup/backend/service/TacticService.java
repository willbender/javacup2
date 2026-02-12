package com.javacup.backend.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service for managing tactics information.
 * <p>
 * This service provides information about available tactics that can be used
 * in matches. Each tactic represents a team with its own AI strategy and
 * configuration.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Service
public class TacticService {

    /**
     * Returns a list of all available tactic names.
     * <p>
     * These tactics are available for match selection. Each tactic name
     * represents a unique team AI implementation from the 2013 competition.
     * </p>
     * 
     * @return list of tactic names
     */
    public List<String> getAllTactics() {
        return Arrays.asList(
            "Ciclones",
            "JGTeam",
            "Ander",
            "Cucaracha",
            "DyMCupcakes",
            "Elaga",
            "Enavas",
            "Espinete",
            "FelipeMoraTeam",
            "Frioleros",
            "Jhontona",
            "Kpacha",
            "Masia13",
            "Novena",
            "Pistachos",
            "Romedal",
            "SitiosTactic2",
            "TheShadows",
            "Toulousains",
            "TwentyThree",
            "Txami",
            "Valedores",
            "AdamTeam"
        );
    }
}
