package com.javacup.backend.controller;

import com.javacup.backend.dto.MatchRequest;
import com.javacup.backend.service.TacticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(MatchController.class)
@Import(TacticService.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRunMatchSuccess() throws Exception {
        // Use actual tactics registered in TacticService
        MatchRequest request = new MatchRequest("twentythree", "pistachos");
        
        // Execute and verify - SavedMatch structure
        mockMvc.perform(post("/api/match/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeTeam.teamName").value("TwentyThree FC"))
                .andExpect(jsonPath("$.awayTeam.teamName").value("Los Pistachos"))
                .andExpect(jsonPath("$.finalHomeGoals").isNumber())
                .andExpect(jsonPath("$.finalAwayGoals").isNumber())
                .andExpect(jsonPath("$.finalHomePossession").isNumber())
                .andExpect(jsonPath("$.iterations").isArray())
                .andExpect(jsonPath("$.iterations", hasSize(greaterThan(0))));
    }

    @Test
    void testRunMatchWithNonExistentHomeTactic() throws Exception {
        // Use a tactic that doesn't exist
        MatchRequest request = new MatchRequest("NonExistent", "SimpleTactic");
        
        // Execute and verify
        mockMvc.perform(post("/api/match/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("NonExistent")))
                .andExpect(jsonPath("$.missingTactics").isArray())
                .andExpect(jsonPath("$.availableTactics").isArray());
    }

    @Test
    void testRunMatchWithNonExistentAwayTactic() throws Exception {
        // Use a tactic that doesn't exist
        MatchRequest request = new MatchRequest("SimpleTactic", "NonExistent");
        
        // Execute and verify
        mockMvc.perform(post("/api/match/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("NonExistent")))
                .andExpect(jsonPath("$.missingTactics").isArray())
                .andExpect(jsonPath("$.availableTactics").isArray());
    }

    @Test
    void testRunMatchWithBothTacticsNonExistent() throws Exception {
        // Use tactics that don't exist
        MatchRequest request = new MatchRequest("NonExistent1", "NonExistent2");
        
        // Execute and verify
        mockMvc.perform(post("/api/match/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("not found")))
                .andExpect(jsonPath("$.missingTactics").isArray())
                .andExpect(jsonPath("$.missingTactics", hasSize(2)))
                .andExpect(jsonPath("$.availableTactics").isArray());
    }

    @Test
    void testGetAvailableTactics() throws Exception {
        // Execute and verify - TacticService has 3 registered tactics
        mockMvc.perform(get("/api/match/tactics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tactics").isArray())
                .andExpect(jsonPath("$.tactics", hasSize(4)))
                .andExpect(jsonPath("$.count").value(4));
    }
}
