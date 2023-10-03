package com.curso.swplanetsapi.web;

import com.curso.swplanetsapi.domain.Planet;
import com.curso.swplanetsapi.domain.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.curso.swplanetsapi.common.PlanetConstants.PLANET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PlanetService service;

    @Test
    public void createPlanet_WithValidData_ReturnsCreated() throws Exception {
        when(service.create(PLANET)).thenReturn(PLANET);
        mockMvc.perform(post("/planets")
                  .content(objectMapper.writeValueAsString(PLANET))
                  .contentType(MediaType.APPLICATION_JSON))
             .andExpect(status().isCreated())
             .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");
        mockMvc.perform(post("/planets")
                  .content(objectMapper.writeValueAsString(emptyPlanet))
                  .contentType(MediaType.APPLICATION_JSON))
             .andExpect(status().isBadRequest());
        mockMvc.perform(post("/planets")
                  .content(objectMapper.writeValueAsString(invalidPlanet))
                  .contentType(MediaType.APPLICATION_JSON))
             .andExpect(status().isBadRequest());
    }

    @Test
    public void createPlanet_WithExistingName_ReturnsConflict() throws Exception {
        when(service.create(any())).thenThrow(DataIntegrityViolationException.class);
        mockMvc.perform(post("/planets")
                  .content(objectMapper.writeValueAsString(PLANET))
                  .contentType(MediaType.APPLICATION_JSON))
             .andExpect(status().isConflict());
    }
}
