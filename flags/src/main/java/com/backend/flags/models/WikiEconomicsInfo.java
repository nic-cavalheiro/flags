package com.backend.flags.models;

public class WikiEconomicsInfo {
    private String country;
    private String section;
    private String content;

    public WikiEconomicsInfo() {}

    public WikiEconomicsInfo(String country, String section, String content) {
        this.country = country;
        this.section = section;
        this.content = content;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "WikiEconomicInfo{" +
                "country='" + country + '\'' +
                ", section='" + section + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
