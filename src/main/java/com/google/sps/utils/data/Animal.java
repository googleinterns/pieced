package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;

public class Animal {
    
    // Cross-referenced
    private String common_name;
    private String binomial_name;

    // Provided by Wikipedia
    private int population;
    private String status;

    // Provided by IUCN API
    private int id;
    private String kingdom;
    private String phylum;
    private String class;
    private String order;
    private String family;
    private String genus;
    private String population_trend;
    private boolean marine_system;
    private boolean freshwater_system;
    private boolean terrestrial_system;
    private String taxonomicnotes;
    private Collection<String> countries;
    private Collection<AnimalHistory> history_set;
    private String citation_link;

    public Animal() {
        common_name = null;
        binomial_name = null;
        population = -1;
        status = null;
        id = -1;
        kingdom = null;
        phylum = null;
        class = null;
        order = null;
        family = null;
        genus = null;
        population_trend = null;
        marine_system = false;
        freshwater_system = false;
        terrestrial_system = false;
        taxonomicnotes = null;
        countries = new HashSet<>();
        history_set = new HashSet<>();
        citation_link = "https://apiv3.iucnredlist.org/api/v3/taxonredirect/";
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

    public void setId(int id) {
        this.id = id;
        this.citation_link += id;
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

    public void setPopulationTrend(String population_trend) {
        this.population_trend = population_trend;
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

    public void setTaxonomicNotes(Stirng taxonomicnotes) {
        this.taxonomicnotes = taxonomicnotes;
    }

    public void addCountry(String country) {
        this.countries.add(country);
    }

    public void addHistory(AnimalHistory history) {
        this.history_set.add(history);
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

    public String getPopulationTrend() {
        return population_trend;
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
        return taxonomicnotes;
    }

    public Collection<String> getCountries() {
        return countries;
    }

    public Collection<AnimalHistory> getHistory() {
        return history_set;
    }

    public String getCitationLink() {
        return citation_link;
    }
}