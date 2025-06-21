package com.backend.flags.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
public class WikipediaSummary {

    private String title;
    private String extract;
}

