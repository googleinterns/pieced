package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;

public class Animal {
    
    // Cross-referenced
    private String common_name;
    private String binomial_name;
    private String status;
    private boolean isPopulationDecreasing;

    // Provided by Wikipedia
    private int population;
    private String wikipedia_notes;
    private String image_link;

    // Provided by IUCN API
    private int id;
    private Taxonomy taxonomy;
    private boolean marine_system;
    private boolean freshwater_system;
    private boolean terrestrial_system;
    private String taxonomic_notes;
    private Collection<String> countries;
    private Collection<AnimalHistory> history_set;
    private Collection<String> citation_links;

    public Animal() {
        common_name = null;
        binomial_name = null;
        status = null;
        isPopulationDecreasing = false;
        population = -1;
        wikipedia_notes = null;
        image_link = null;
        id = -1;
        taxonomy = null;
        marine_system = false;
        freshwater_system = false;
        terrestrial_system = false;
        taxonomic_notes = null;
        countries = new HashSet<>();
        history_set = new HashSet<>();
        citation_links = new HashSet<>();
    }

    // Wikipedia-side creation
    public Animal(String common_name, String binomial_name, String status, boolean isPopulationDecreasing,
                  int population, String wikipedia_notes, String image_link) {
        this();
        this.common_name = common_name;
        this.binomial_name = binomial_name;
        this.status = status;
        this.isPopulationDecreasing = isPopulationDecreasing;
        this.population = population;
        this.wikipedia_notes = wikipedia_notes;
        this.image_link = image_link;
    }

    // API-side creation
    public Animal(String common_name, String binomial_name, String status, boolean isPopulationDecreasing,
                  int id, Taxonomy taxonomy, boolean marine_system, boolean freshwater_system,
                  boolean terrestrial_system, String taxonomic_notes) {
        this();
        this.common_name = common_name;
        this.binomial_name = binomial_name;
        this.status = status;
        this.isPopulationDecreasing = isPopulationDecreasing;
        this.id = id;
        this.taxonomy = taxonomy;
        this.marine_system = marine_system;
        this.freshwater_system = freshwater_system;
        this.terrestrial_system = terrestrial_system;
        this.taxonomic_notes = taxonomic_notes;
    }

    public void setCommonName(String common_name) {
        this.common_name = common_name;
    }

    public void setBinomialName(String binomial_name) {
        this.binomial_name = binomial_name;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageLink(String link) {
        this.image_link = link;
    }

    public void setId(int id) {
        this.id = id;

        // Currently have the IUCN link automatically added but can change this to be done somewhere else
        this.citation_links.add("https://apiv3.iucnredlist.org/api/v3/taxonredirect/" + id);
    }

    public void setTaxonomy(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    public void setPopulationDecreasing(boolean isPopulationDecreasing) {
        this.isPopulationDecreasing = isPopulationDecreasing;
    }

    public void setMarineSystem(boolean indicator) {
        this.marine_system = indicator;
    }

    public void setFreshwaterSystem(boolean indicator) {
        this.freshwater_system = indicator;
    }

    public void setTerrestrialSystem(boolean indicator) {
        this.terrestrial_system = indicator;
    }

    public void setTaxonomicNotes(String taxonomic_notes) {
        this.taxonomic_notes = taxonomic_notes;
    }

    public void addCountry(String country) {
        this.countries.add(country);
    }

    public void addHistory(AnimalHistory history) {
        this.history_set.add(history);
    }

    public void addCitationLink(String link) {
        this.citation_links.add(link);
    }

    public String getCommonName() {
        return common_name;
    }

    public String getBinomialName() {
        return binomial_name;
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
        return image_link;
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public String getPopulationDecreasing() {
        return isPopulationDecreasing;
    }

    public boolean isMarineSystem() {
        return marine_system;
    }

    public boolean isFreshwaterSystem() {
        return freshwater_system;
    }

    public boolean isTerrestrialSystem() {
        return terrestrial_system;
    }

    public String getTaxonomicnotes() {
        return taxonomic_notes;
    }

    public Collection<String> getCountries() {
        return countries;
    }

    public Collection<AnimalHistory> getHistory() {
        return history_set;
    }

    public Collection<String> getCitationLinks() {
        return citation_links;
    }
}