package com.curso.swplanetsapi.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.curso.swplanetsapi.common.PlanetConstants.INVALID_PLANET;
import static com.curso.swplanetsapi.common.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {
    @InjectMocks
    private PlanetService service;
    @Mock
    private PlanetRepository repository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        when(repository.save(PLANET)).thenReturn(PLANET);
        Planet sut = service.create(PLANET);
        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        when(repository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);
        assertThatThrownBy(() -> service.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        when(repository.findById(1L)).thenReturn(Optional.of(PLANET));
        Optional<Planet> sut = service.get(1L);
        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        Optional<Planet> sut = service.get(1L);
        assertThat(sut).isEmpty();
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        when(repository.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));
        Optional<Planet> sut = service.getByName(PLANET.getName());
        assertThat(sut).isNotEmpty();
        assertThat(sut.get().getName()).isEqualTo("Naboo");
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsEmpty() {
        final String name = "Unexisting name";
        when(repository.findByName(name)).thenReturn(Optional.empty());
        Optional<Planet> sut = service.getByName(name);
        assertThat(sut).isEmpty();
    }

    @Test
    public void listPlanets_ReturnsAllPlanets() {
        List<Planet> planets = new ArrayList<>(){{add(PLANET);}};
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));
        when(repository.findAll(query)).thenReturn(planets);
        List<Planet> sut = service.list(PLANET.getClimate(), PLANET.getTerrain());
        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.get(0)).isEqualTo(PLANET);
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() {
        when(repository.findAll(any())).thenReturn(Collections.emptyList());
        List<Planet> sut = service.list(PLANET.getClimate(), PLANET.getTerrain());
        assertThat(sut).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_doesNotThrowAnyException() {
        assertThatCode(() -> service.remove(1L)).doesNotThrowAnyException();
    }

    @Test
    public void removePlanet_WithUnexistingId_ThrowsException() {
        doThrow(new RuntimeException()).when(repository).deleteById(99L);
        assertThatThrownBy(() -> service.remove(99L)).isInstanceOf(RuntimeException.class);
    }
}
