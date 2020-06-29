package com.google.sps.utils.data;

public class Taxonomy {

    private String kingdom_t;
    private String phylum_t;
    private String class_t;
    private String order_t;
    private String family_t;
    private String genus_t;

    public Taxonomy() {

    }

    public Taxonomy(String kingdom_t, String phylum_t, String class_t, String order_t, String family_t, String genus_t) {
        this.kingdom_t = kingdom_t;
        this.phylum_t = phylum_t;
        this.class_t = class_t;
        this.order_t = order_t;
        this.family_t = family_t;
        this.genus_t = genus_t;
    }

    public void setKingdom(String kingdom) {
        this.kingdom_t = kingdom;
    }

    public void setPhylum(String phylum) {
        this.phylum_t = phylum;
    }

    public void setClass(String class_t) {
        this.class_t = class_t;
    }

    public void setOrder(String order) {
        this.order_t = order;
    }

    public void setFamily(String family) {
        this.family_t = family;
    }

    public void setGenus(String genus) {
        this.genus_t = genus;
    }

    public String getAnimalKingdom() {
        return kingdom_t;
    }

    public String getAnimalPhylum() {
        return phylum_t;
    }

    public String getAnimalClass() {
        return class_t;
    }

    public String getAnimalOrder() {
        return order_t;
    }

    public String getAnimalFamily() {
        return family_t;
    }

    public String getAnimalGenus() {
        return genus_t;
    }
}