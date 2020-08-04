package com.google.sps.utils.data;

import com.google.sps.utils.data.TaxonomicPath;
import com.google.sps.utils.data.PopulationTrend;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;

public class Species {
    // Cross-referenced IUCN/Wikipedia
    private String commonName;
    private String binomialName;
    private String status;
    private PopulationTrend trend;

    // Provided by Wikipedia
    private long population;
    private String imageLink;
    private String citationLink;

    // Provided by GBIF
    private TaxonomicPath taxonomicPath;
    private String geoData;

    // Provided by Knowledge Graph API
    private String wikipediaNotes;
    
    public static class Builder {
        private String commonName = null;
        private String binomialName = null;
        private String status = null;
        private PopulationTrend trend = PopulationTrend.UNKNOWN;

        private long population = -1;
        private String wikipediaNotes = null;
        private String imageLink = null;
        private String citationLink = null;

        private TaxonomicPath taxonomicPath = null;
        private String geoData = null;

        public Builder() {}

        public Builder withCommonName(String commonName) {
            this.commonName = commonName;
            return this;
        }
        
        public Builder withBinomialName(String binomialName) {
            this.binomialName = binomialName;
            return this;
        }
        
        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }
        
        public Builder withPopulationTrend(PopulationTrend trend) {
            this.trend = trend;
            return this;
        }
        
        public Builder withPopulation(long population) {
            this.population = population;
            return this;
        }
        
        public Builder withWikipediaNotes(String wikipediaNotes) {
            this.wikipediaNotes = wikipediaNotes;
            return this;
        }
        
        public Builder withImageLink(String imageLink) {
            this.imageLink = imageLink;
            return this;
        }
        
        public Builder withCitationLink(String citationLink) {
            this.citationLink = citationLink;
            return this;
        }
        
        public Builder withTaxonomicPath(TaxonomicPath taxonomicPath) {
            this.taxonomicPath = taxonomicPath;
            return this;
        }

        // Overloaded method to construct TaxonomicPath w/o external reference
        public Builder withTaxonomicPath(String kingdom, String phylum, String class, String order, String family, String genus) {
            this.taxonomicPath = new TaxonomicPath(kingdom, phylum, class, order, family, genus);
            return this;
        }

        public Builder withGeoData(String geoData) {
            this.geoData = geoData;
            return this;
        }

        public Species build() {
            Species s = new Species();
            s.commonName = this.commonName;
            s.binomialName = this.binomialName;
            s.status = this.status;
            s.trend = this.trend;
            s.population = this.population;
            s.wikipediaNotes = this.wikipediaNotes;
            s.imageLink = this.imageLink;
            s.citationLink = this.citationLink;
            s.geoData = this.geoData;

            return s;
        }      
    }
  
    private Species() {
        commonName = null;
        binomialName = null;
        status = null;
        trend = PopulationTrend.UNKNOWN;
        population = -1;
        wikipediaNotes = null;
        imageLink = null;
        citationLink = null;
        taxonomicPath = null;
        geoData = null;
    }

    public void setTaxonomicPath(TaxonomicPath taxonomicPath) {
        this.taxonomicPath = taxonomicPath;
    }

    public void setNotes(String wikipediaNotes) {
        this.wikipediaNotes = wikipediaNotes;
    }

    public void setGeoData(String geoData) {
        this.geoData = geoData;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getBinomialName() {
        return binomialName;
    }

    public long getPopulation() {
        return population;
    } 
  
    public String getStatus() {
        return status;
    }
    
    public String getImageLink() {
        return imageLink;
    }

    public TaxonomicPath getTaxonomicPath() {
        return taxonomicPath;
    }

    public PopulationTrend getTrend() {
        return trend;
    }

    public String getWikipediaNotes() {
        return wikipediaNotes;
    }

    public String getCitationLink() {
        return citationLink;
    }

    public boolean hasNecessaryInfo() {
        if (binomialName == null || binomialName.equals("")) {
            return false;
        }

        if (population < 0) {
            return false;
        }

        if (imageLink == null || imageLink.equals("")) {
            return false;
        }
        
        return true;
    }
}