package com.curso.swplanetsapi.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlanetRepository extends CrudRepository<Planet, Long> {
    Optional<Planet> findByName(String name);
}
