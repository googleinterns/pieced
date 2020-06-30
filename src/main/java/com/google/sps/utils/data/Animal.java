package com.google.sps.utils.data;

import com.google.sps.utils.data.AnimalHistory;
import com.google.sps.utils.data.Taxonomy;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;

public class Animal {
    
    // Cross-referenced IUCN/Wikipedia
    private String commonName;
    private String binomialName;
    private String status;
    private boolean isPopulationDecreasing;

    // Provided by Wikipedia
    private int population;
    private String wikipediaNotes;
    private String imageLink;

    // Provided by GBIF
    private Taxonomy taxonomy;

    // Provided by IUCN API
    private int id;
    private boolean marineSystem;
    private boolean freshwaterSystem;
    private boolean terrestrialSystem;
    private String taxonomicNotes;
    private Collection<String> countries;
    private Collection<AnimalHistory> historySet;
    private Collection<String> citationLinks;

    public Animal() {
        commonName = null;
        binomialName = null;
        status = null;
        isPopulationDecreasing = false;
        population = -1;
        wikipediaNotes = null;
        imageLink = null;
        id = -1;
        taxonomy = null;
        marineSystem = false;
        freshwaterSystem = false;
        terrestrialSystem = false;
        taxonomicNotes = null;
        countries = new HashSet<>();
        historySet = new HashSet<>();
        citationLinks = new HashSet<>();
    }

    // Wikipedia-side creation
    public Animal(String commonName, String binomialName, String status, boolean isPopulationDecreasing,
                  int population, String wikipediaNotes, String imageLink) {
        this();
        this.commonName = commonName;
        this.binomialName = binomialName;
        this.status = status;
        this.isPopulationDecreasing = isPopulationDecreasing;
        this.population = population;
        this.wikipediaNotes = wikipediaNotes;
        this.imageLink = imageLink;
    }

    // IUCN-side creation
    public Animal(String commonName, String binomialName, String status, boolean isPopulationDecreasing,
                  int id, Taxonomy taxonomy, boolean marineSystem, boolean freshwaterSystem,
                  boolean terrestrialSystem, String taxonomicNotes) {
        this();
        this.commonName = commonName;
        this.binomialName = binomialName;
        this.status = status;
        this.isPopulationDecreasing = isPopulationDecreasing;
        this.id = id;
        this.taxonomy = taxonomy;
        this.marineSystem = marineSystem;
        this.freshwaterSystem = freshwaterSystem;
        this.terrestrialSystem = terrestrialSystem;
        this.taxonomicNotes = taxonomicNotes;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public void setBinomialName(String binomialName) {
        this.binomialName = binomialName;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageLink(String link) {
        this.imageLink = link;
    }

    public void setId(int id) {
        this.id = id;

        // Currently have the IUCN link automatically added but can change this to be done somewhere else
        this.citationLinks.add("https://apiv3.iucnredlist.org/api/v3/taxonredirect/" + id);
    }

    public void setTaxonomy(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    public void setPopulationDecreasing(boolean isPopulationDecreasing) {
        this.isPopulationDecreasing = isPopulationDecreasing;
    }

    public void setMarineSystem(boolean indicator) {
        this.marineSystem = indicator;
    }

    public void setFreshwaterSystem(boolean indicator) {
        this.freshwaterSystem = indicator;
    }

    public void setTerrestrialSystem(boolean indicator) {
        this.terrestrialSystem = indicator;
    }

    public void setTaxonomicNotes(String taxonomicNotes) {
        this.taxonomicNotes = taxonomicNotes;
    }

    public void addCountry(String country) {
        this.countries.add(country);
    }

    public void addHistory(AnimalHistory history) {
        this.historySet.add(history);
    }

    public void addCitationLink(String link) {
        this.citationLinks.add(link);
    }

    public String getCommonName() {
        return commonName;
    }

    public String getBinomialName() {
        return binomialName;
    }

    public int getPopulation() {
        return population;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public boolean getPopulationDecreasing() {
        return isPopulationDecreasing;
    }

    public boolean isMarineSystem() {
        return marineSystem;
    }

    public boolean isFreshwaterSystem() {
        return freshwaterSystem;
    }

    public boolean isTerrestrialSystem() {
        return terrestrialSystem;
    }

    public String getTaxonomicnotes() {
        return taxonomicNotes;
    }

    public Collection<String> getCountries() {
        return countries;
    }

    public Collection<AnimalHistory> getHistory() {
        return historySet;
    }

    public Collection<String> getCitationLinks() {
        return citationLinks;
    }
}