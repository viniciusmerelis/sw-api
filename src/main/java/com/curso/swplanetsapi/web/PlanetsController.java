package com.curso.swplanetsapi.web;

import com.curso.swplanetsapi.domain.Planet;
import com.curso.swplanetsapi.domain.PlanetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planets")
public class PlanetsController {

    @Autowired
    private PlanetService service;

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody Planet planet) {
        Planet planetCreated = service.create(planet);
        return ResponseEntity.status(HttpStatus.CREATED).body(planetCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> get(@PathVariable Long id) {
        return service.get(id).map(planet -> ResponseEntity.ok(planet))
             .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
