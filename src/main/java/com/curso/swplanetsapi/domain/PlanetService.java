package com.curso.swplanetsapi.domain;

import org.springframework.stereotype.Service;

@Service
public class PlanetService {
    private PlanetRepository repository;

    public PlanetService(PlanetRepository repository) {
        this.repository = repository;
    }

    public Planet create(Planet planet) {
        return repository.save(planet);
    }
}
