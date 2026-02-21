package com.backend.flags.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WikiGeographyInfo {
    private String country;
    private String section;
    private String mapUrl;
    private String globeUrl; // AI generated: orthographic globe
}
