package com.backend.flags.models;

public class WikipediaSummary {

    private String title;
    private String extract;

    public WikipediaSummary() {}

    // Construtor que aceita título e extrato
    public WikipediaSummary(String title, String extract) {
        this.title = title;
        this.extract = extract;
    }

    // Getters e setters
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

    @Override
    public String toString() {
        return "WikipediaSummary{" +
                "title='" + title + '\'' +
                ", extract='" + extract + '\'' +
                '}';
    }
}

