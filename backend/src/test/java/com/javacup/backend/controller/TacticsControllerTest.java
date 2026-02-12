package com.javacup.backend.controller;

import com.javacup.backend.service.TacticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for TacticsController.
 * <p>
 * Tests the REST endpoints for tactics management without authentication.
 * Uses MockMvc for testing Spring MVC controllers.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@WebMvcTest(TacticsController.class)
class TacticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TacticService tacticService;

    /**
     * Tests that the /api/tactics endpoint returns a list of tactics.
     */
    @Test
    void testListTactics() throws Exception {
        // Setup mock data
        List<String> mockTactics = Arrays.asList(
            "Ciclones",
            "JGTeam",
            "Ander",
            "Cucaracha",
            "DyMCupcakes"
        );
        
        when(tacticService.getAllTactics()).thenReturn(mockTactics);

        // Execute and verify
        mockMvc.perform(get("/api/tactics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tactics").isArray())
                .andExpect(jsonPath("$.tactics", hasSize(5)))
                .andExpect(jsonPath("$.tactics[0]").value("Ciclones"))
                .andExpect(jsonPath("$.tactics[1]").value("JGTeam"))
                .andExpect(jsonPath("$.tactics[2]").value("Ander"))
                .andExpect(jsonPath("$.tactics[3]").value("Cucaracha"))
                .andExpect(jsonPath("$.tactics[4]").value("DyMCupcakes"))
                .andExpect(jsonPath("$.count").value(5));
    }

    /**
     * Tests that the response contains the count field.
     */
    @Test
    void testListTacticsContainsCount() throws Exception {
        List<String> mockTactics = Arrays.asList("Tactic1", "Tactic2", "Tactic3");
        when(tacticService.getAllTactics()).thenReturn(mockTactics);

        mockMvc.perform(get("/api/tactics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.count").value(3));
    }

    /**
     * Tests that an empty list is handled correctly.
     */
    @Test
    void testListTacticsEmptyList() throws Exception {
        List<String> emptyList = Arrays.asList();
        when(tacticService.getAllTactics()).thenReturn(emptyList);

        mockMvc.perform(get("/api/tactics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tactics").isArray())
                .andExpect(jsonPath("$.tactics", hasSize(0)))
                .andExpect(jsonPath("$.count").value(0));
    }

    /**
     * Tests that the endpoint returns proper JSON content type.
     */
    @Test
    void testListTacticsContentType() throws Exception {
        List<String> mockTactics = Arrays.asList("Tactic1");
        when(tacticService.getAllTactics()).thenReturn(mockTactics);

        mockMvc.perform(get("/api/tactics"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    /**
     * Tests that the tactics array contains all expected tactics.
     */
    @Test
    void testListTacticsContainsAllTactics() throws Exception {
        List<String> mockTactics = Arrays.asList(
            "Ciclones",
            "JGTeam",
            "Ander"
        );
        when(tacticService.getAllTactics()).thenReturn(mockTactics);

        mockMvc.perform(get("/api/tactics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tactics", containsInAnyOrder("Ciclones", "JGTeam", "Ander")));
    }
}
