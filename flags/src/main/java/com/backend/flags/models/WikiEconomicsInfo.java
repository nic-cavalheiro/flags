package com.backend.flags.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
public class WikiEconomicsInfo {
    private String country;
    private String section;
    private String content;
}

