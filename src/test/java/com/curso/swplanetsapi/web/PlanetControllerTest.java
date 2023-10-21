package com.curso.swplanetsapi.web;

import com.curso.swplanetsapi.domain.Planet;
import com.curso.swplanetsapi.domain.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.curso.swplanetsapi.common.PlanetConstants.PLANET;
import static com.curso.swplanetsapi.common.PlanetConstants.PLANETS;
import static com.curso.swplanetsapi.common.PlanetConstants.TATOOINE;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
        when(service.get(1L)).thenReturn(Optional.of(PLANET));
        mockMvc.perform(get("/planets/1"))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() throws Exception {
        mockMvc.perform(get("/planets/1"))
             .andExpect(status().isNotFound());
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() throws Exception {
        when(service.getByName(PLANET.getName())).thenReturn(Optional.of(PLANET));
        mockMvc.perform(get("/planets/name/" + PLANET.getName()))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsEmpty() throws Exception {
        final String name = "Unexisting name";
        mockMvc.perform(get("/planets/name/" + name))
             .andExpect(status().isNotFound());
    }

    @Test
    public void listPlanets_ReturnsFilteredPlanets() throws Exception {
        when(service.list(null, null)).thenReturn(PLANETS);
        when(service.list(TATOOINE.getClimate(), TATOOINE.getTerrain())).thenReturn(List.of(TATOOINE));
        mockMvc.perform(get("/planets"))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$", hasSize(3)));
        mockMvc.perform(get("/planets?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$", hasSize(1)))
             .andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() throws Exception {
        when(service.list(null, null)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/planets"))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase() throws Exception {
        mockMvc.perform(delete("/planets/1"))
             .andExpect(status().isNoContent());
    }

    @Test
    public void removePlanet_WithUnexistingId_ReturnsNotFound() throws Exception {
        final Long planetId = 1L;
        doThrow(new EmptyResultDataAccessException(1)).when(service).remove(planetId);
        mockMvc.perform(delete("/planets/" + planetId))
             .andExpect(status().isNotFound());
    }
}
