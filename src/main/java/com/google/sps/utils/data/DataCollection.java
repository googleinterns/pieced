// This class retrieves data from the GBIF.org species API. License: CC0 & CC BY.

package com.google.sps.utils.data;

import com.google.sps.utils.data.Species;
import com.google.sps.utils.data.PopulationTrend;
import com.google.sps.utils.data.SpeciesAPIRetrieval;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataCollection {
  private static final String API_URL = "https://api.gbif.org/v1/species/match?name=";
  private static final String LIST_URL = "https://en.wikipedia.org/wiki/Lists_of_organisms_by_population";
  private static final String LIST_CONTENT_CLASS = "mw-parser-output";
  private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
  private static KeyFactory keyFactory = datastore.newKeyFactory().setKind("Species");

  public static void main (String[] args) throws IOException {
    List<String> urls = parseListofPages();
    for (String url: urls) {
      parseSpeciesTable(url);
    }
  }

  /**
   * Scrapes LIST_URL to get a list of all the URLs with information about species
   * Page Structure:
   * <ul><li>
   *   <a>Subheading</a>
   *   <ul>
   *     <li><a href="LINK"></a></li>
   *     <li><a href="LINK"></a></li>
   *   </ul>
   * </li></ul>
   *
   * @return List of all the URLs to go to
   */
  private static List<String> parseListofPages() throws IOException {
    Document doc = Jsoup.connect(LIST_URL).get();
    Elements content = doc.getElementsByClass(LIST_CONTENT_CLASS);
    Elements listItems = content.select("li > ul > li"); // get list items that are in a nested ul
    List<String> urls = new ArrayList<String>();

    for (Element listItem: listItems) {

      // Ignores list items from the Table of Contents
      if (!listItem.attr("class").equals("")) {
        continue;
      }

      // Gets the absolute link to the new wiki page
      // Example: https://en.wikipedia.org/wiki/List_of_even-toed_ungulates_by_population
      Element link = listItem.select("a[href]").first();
      String absHref = link.attr("abs:href");
      if (absHref.contains("#")) { // if link is actually a bookmark
        continue;
      }
    
      urls.add(absHref);
    }
    
    return urls;
  }

  /**
   * Add information for every species per url into Datastore
   * @param url: url to parse the tables for
   */
  private static void parseSpeciesTable(String url) throws IOException {
    Document doc = Jsoup.connect(url).get();

    Elements table = doc.getElementsByClass("wikitable");
    for (Element head : table.select("tbody")) {
      for (Element row : head.select("tr")) {
        Elements tds = row.select("td");

        Species species = processSpecies(tds, url);
        if (species == null || !species.hasNecessaryInfo()) {
          continue;
        }

        if (!speciesAlreadyStored(species.getBinomialName())) {
        //   System.out.println("Processing '" + species.getBinomialName() + "'...");
          addApiInfo(species);
        //   System.out.println(species.getTaxonomy().getAnimalKingdom());
          addSpeciesToDatastore(species);
        }
      }
    }
  }

  /**
   * Get Species information from each row of the table
   * @param tds: The row Element to scrape for info
   * @param url: url that contains the table for citation
   * @return Species object with filled fields, or null if incomplete
   */
  private static Species processSpecies(Elements tds, String url) {
    if (tds.size() > 6) {
      String commonName = tds.get(0).text();
      String binomialName = tds.get(1).text();
      String population = scrapePopulation(tds.get(2).text());
      String status = scrapeStatus(tds.get(3).text());
      PopulationTrend trend = scrapeTrend(tds.get(4).select("img").first());
      String notes = tds.get(5).text();
      String imageLink = scrapeImageLink(tds.get(6).select("img").first());

      System.out.printf("%-35s %-30s %-25s %-10s %-15s %n", commonName, binomialName, population, status, trend);
      Species species = new Species(commonName, binomialName, status, trend, population, notes, imageLink, url);
      return species;
    }
    return null;
  }

  /**
   * Checks if the species is already in Datastore
   * @return true if already stored, false otherwise
   */
  private static boolean speciesAlreadyStored(String binomialName) {
    Key key = keyFactory.newKey(binomialName);
    Entity speciesEntity = datastore.get(key);
    return speciesEntity != null;
  }

  /**
   * Retrieves apiMap for the passed species and adds additional info to species
   * @param species: non-null species that isn't already stored in Datastore
   */
  public static void addApiInfo(Species species) {
    // Add API-side fields if available
    try {
      String apiJSON = SpeciesAPIRetrieval.getJSON(API_URL, species.getBinomialName());
      Map apiMap = SpeciesAPIRetrieval.convertJSONToMap(apiJSON);
      SpeciesAPIRetrieval.addAPISpeciesInfo(species, apiMap);
    } catch (Exception e) {
      System.out.println("Exception occurred.");
      e.printStackTrace();
    }
  }

  /**
   * Store species in Datastore
   * @param species: species to store
   */
  public static void addSpeciesToDatastore(Species species) {
    Key key = keyFactory.newKey(species.getBinomialName());
    Entity oldEntity = datastore.get(key);
    
    if (oldEntity == null) {
      Entity speciesEntity = Entity.newBuilder(key)
        .set("common_name", species.getCommonName())
        .set("binomial_name", species.getBinomialName())
        .set("trend", species.getTrend().name())
        .set("status", species.getStatus())
        .set("population", species.getPopulation())
        .set("kingdom", species.getTaxonomy().getAnimalKingdom())
        .set("phylum", species.getTaxonomy().getAnimalPhylum())
        .set("class", species.getTaxonomy().getAnimalClass())
        .set("order", species.getTaxonomy().getAnimalOrder())
        .set("family", species.getTaxonomy().getAnimalFamily())
        .set("genus", species.getTaxonomy().getAnimalGenus())
        .set("image_link", species.getImageLink())
        .set("wikipedia_notes", species.getWikipediaNotes())
        .set("citation_link", species.getCitationLink())
        .build();
      
      datastore.put(speciesEntity);
    //   System.out.println(species.getBinomialName() + " was added to Datastore.");
    // } else {
    //   System.out.println(species.getBinomialName() + " was already in Datastore.");
    }
  }

  // ------------------------------  SCRAPING HELPER FUNCTIONS  ------------------------------ //
  private static String scrapeStatus(String statusString) {
    String status = statusString.replaceAll("Domesticated", "DO");
    status = removeBrackets(status);
    return status;
  }

  private static String scrapePopulation(String populationString) {
    String pop = removeBrackets(populationString);
    pop = pop.replaceAll("[^0-9.–-]", "");
    return pop;
  }

  private static PopulationTrend scrapeTrend(Element trendImg) {
    String trend;
    if (trendImg != null) {
      trend = trendImg.attr("alt");
    } else {
      trend = "Unknown";
    }
    return convertToPopulationTrendEnum(trend);
  }

  private static String scrapeImageLink(Element image) {
    String imageLink = "";
    if (image != null) {
      imageLink = image.absUrl("src");
    }
    return imageLink;
  }

  public static PopulationTrend convertToPopulationTrendEnum(String trendString) {
    switch(trendString) {
      case "Increase":
        return PopulationTrend.INCREASING;
      case "Decrease":
        return PopulationTrend.DECREASING;
      case "Steady":
        return PopulationTrend.STEADY;
      default:
        return PopulationTrend.UNKNOWN;
    }
  }

  private static String removeBrackets(String og) {
    return og.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ");
  }
}
