package com.backend.flags.models;

public class WikipediaSummary {

    private String title;
    private String extract;

    // Construtores
    public WikipediaSummary() {}

    public WikipediaSummary(String title, String extract) {
        this.title = title;
        this.extract = extract;
    }

    // Getters e Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtract() {
        return extract;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }

    // Override de toString, se necessário
    @Override
    public String toString() {
        return "WikipediaSummary{" +
                "title='" + title + '\'' +
                ", extract='" + extract + '\'' +
                '}';
    }
}
