package com.backend.flags.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artwork {
    private String title;
    private String author;
    private String date;
    private String style;
    private String historicalPeriod;
    private String city;
    private String imageUrl;
}
