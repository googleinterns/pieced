package com.google.sps.utils.data;

public class Taxonomy {

    private String kingdom;
    private String phylum;
    private String class;
    private String order;
    private String family;
    private String genus;

    public Taxonomy();

    public Taxonomy(String kingdom, String phylum, String class, String order, String family, String genus) {
        this.kingdom = kingdom;
        this.phylum = phylum;
        this.class = class;
        this.order = order;
        this.family = family;
        this.genus = genus;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public void setClass(String class) {
        this.class = class;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getKingdom() {
        return kingdom;
    }

    public String getPhylum() {
        return phylum;
    }

    public String getClass() {
        return class;
    }

    public String getOrder() {
        return order;
    }

    public String getFamily() {
        return family;
    }

    public String getGenus() {
        return genus;
    }
}