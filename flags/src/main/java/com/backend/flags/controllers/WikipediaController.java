package com.backend.flags.controllers;

import com.backend.flags.services.WikipediaService;
import org.springframework.web.bind.annotation.*;
import com.backend.flags.models.WikipediaSummary; 

@RestController
@RequestMapping("/api/wikipedia")
@CrossOrigin(origins = "*")
public class WikipediaController {

    private final WikipediaService wikipediaService;

    public WikipediaController(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    @GetMapping("/{country}")
    public WikipediaSummary getCountryInfo(@PathVariable String country) {
        return wikipediaService.getCountrySummary(country);
    }
}