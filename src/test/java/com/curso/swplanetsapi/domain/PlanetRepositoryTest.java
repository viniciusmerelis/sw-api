package com.curso.swplanetsapi.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static com.curso.swplanetsapi.common.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class PlanetRepositoryTest {
    @Autowired
    private PlanetRepository repository;
    @Autowired
    private TestEntityManager manager;

    @AfterEach
    public void afterEach() {
        PLANET.setId(null);
    }

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = repository.save(PLANET);
        Planet sut = manager.find(Planet.class, planet.getId());
        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(planet.getName());
        assertThat(sut.getClimate()).isEqualTo(planet.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(planet.getTerrain());
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");
        assertThatThrownBy(() -> repository.save(emptyPlanet));
        assertThatThrownBy(() -> repository.save(invalidPlanet));
    }

    @Test
    public void createPlanet_WithExistingName_ThrowsException() {
        Planet planet = manager.persistFlushFind(PLANET);
        manager.detach(planet);
        planet.setId(null);
        assertThatThrownBy(() -> repository.save(planet));
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        Planet planet = manager.persistFlushFind(PLANET);
        Optional<Planet> planetOpt = repository.findById(planet.getId());
        assertThat(planetOpt).isNotEmpty();
        assertThat(planetOpt.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() {
        Optional<Planet> planetOpt = repository.findById(1L);
        assertThat(planetOpt).isEmpty();
    }
}
