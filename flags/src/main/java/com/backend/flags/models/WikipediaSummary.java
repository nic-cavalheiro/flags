package com.backend.flags.models;

public class WikipediaSummary {

    private String title;
    private String source;
    private String shortDescription; // Novo campo

    public WikipediaSummary() {}

    public WikipediaSummary(String title, String source, String shortDescription) {
        this.title = title;
        this.source = source;
        this.shortDescription = shortDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Override
    public String toString() {
        return "WikipediaSummary{" +
                "title='" + title + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                '}';
    }
}
