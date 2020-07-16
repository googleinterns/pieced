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
  private String wikipediaNotes;
  private String imageLink;
  private String citationLink;

  // Provided by GBIF
  private TaxonomicPath taxonomicPath;

  public Species() {
    commonName = null;
    binomialName = null;
    status = null;
    trend = PopulationTrend.UNKNOWN;
    population = -1;
    wikipediaNotes = null;
    imageLink = null;
    citationLink = null;
    taxonomicPath = null;
  }

  // Wikipedia-side creation
  public Species(String commonName, String binomialName, String status, PopulationTrend trend,
                long population, String wikipediaNotes, String imageLink, String citationLink) {
    this();
    this.commonName = commonName;
    this.binomialName = binomialName;
    this.status = status;
    this.trend = trend;
    this.population = population;
    this.wikipediaNotes = wikipediaNotes;
    this.imageLink = imageLink;
    this.citationLink = citationLink;
  }

  public void setTaxonomicPath(TaxonomicPath taxonomicPath) {
    this.taxonomicPath = taxonomicPath;
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