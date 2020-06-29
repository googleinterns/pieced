package com.google.sps.utils.data;

public class Taxonomy {

    private String kingdom;
    private String phylum;
    private String class_t;
    private String order;
    private String family;
    private String genus;

    public Taxonomy() {
        kingdom = null;
    }

    public Taxonomy(String kingdom, String phylum, String class_t, String order, String family, String genus) {
        this.kingdom = kingdom;
        this.phylum = phylum;
        this.class_t = class_t;
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

    public void setclass_t(String class_t) {
        this.class_t = class_t;
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

    public String getclass_t() {
        return class_t;
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