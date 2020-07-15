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

  public static void collectData() throws IOException {
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
          addApiInfo(species);
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
      String commonName = tds.get(0).text().trim();
      String binomialName = tds.get(1).text().trim();
      String populationString = scrapePopulation(tds.get(2).text()).trim();
      long population = convertPopulationLong(populationString);
      String status = scrapeStatus(tds.get(3).text()).trim();
      PopulationTrend trend = scrapeTrend(tds.get(4).select("img").first());
      String notes = removeBrackets(tds.get(5).text()).trim();
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
      System.err.println("Exception occurred retrieving API data: " + e);
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
        .set("status", species.getStatus())
        .set("population", species.getPopulation())
        .set("image_link", species.getImageLink())
        .set("wikipedia_notes", species.getWikipediaNotes())
        .set("citation_link", species.getCitationLink())
        .build();
    
      if (species.getTaxonomicPath() != null) {
        speciesEntity = Entity.newBuilder(speciesEntity).set("kingdom", species.getTaxonomicPath().getAnimalKingdom()).build();
        speciesEntity = Entity.newBuilder(speciesEntity).set("phylum", species.getTaxonomicPath().getAnimalPhylum()).build();
        speciesEntity = Entity.newBuilder(speciesEntity).set("class", species.getTaxonomicPath().getAnimalClass()).build();
        speciesEntity = Entity.newBuilder(speciesEntity).set("order", species.getTaxonomicPath().getAnimalOrder()).build();
        speciesEntity = Entity.newBuilder(speciesEntity).set("family", species.getTaxonomicPath().getAnimalFamily()).build();
        speciesEntity = Entity.newBuilder(speciesEntity).set("genus", species.getTaxonomicPath().getAnimalGenus()).build();
      }
    
      if (species.getTrend() != null) {
        speciesEntity = Entity.newBuilder(speciesEntity).set("trend", species.getTrend().name()).build();
      }
      
      datastore.put(speciesEntity);
    }
  }

  // ------------------------------  SCRAPING HELPER FUNCTIONS  ------------------------------ //

  /**
   * Takes in the status information from wikipedia and cleans it.
   * 
   * The first two letters are its status according to IUCN and then a parenthesis holds its 
   * wikipedia reference number. An edge case is "Domesticated" being written out, which is
   * rewritten as "DO" to match the others.
   * @param statusString string formatted like EN[#] that needs to be cleaned
   */
  private static String scrapeStatus(String statusString) {
    String status = statusString.replaceAll("Domesticated", "DO");
    status = removeBrackets(status);
    if (status.length() > 2) {
        status = status.substring(0, 2);
    }
    return status;
  }

  /**
   * Takes in the population information from wikipedia and cleans it.
   * 
   * Input comes in two main formats (excluding any commas or white space characters):
   *    1. a[b] where a is the population count and b is the reference number
   *    2. a-b[c] where a is the lower bound population, b is the upper bound, and c is the reference number
   * @param populationString string that needs to be cleaned
   */
  private static String scrapePopulation(String populationString) {
    String pop = removeBrackets(populationString);
    pop = pop.replaceAll("[^0-9.–-]", ""); // remove any characters other than numbers and '-'
    return pop;
  }

  /**
   * Takes in the trendImg element from wikipedia and extracts the trend
   * 
   * If the species has trend information, the format is:
   *    <img alt="TREND" src="image" title="TREND">
   * @param trendImg html for the trend box in the table
   */
  private static PopulationTrend scrapeTrend(Element trendImg) {
    String trend;
    if (trendImg != null) {
      trend = trendImg.attr("alt");
    } else {
      return PopulationTrend.UNKNOWN;
    }
    return convertToPopulationTrendEnum(trend);
  }

  /**
   * Takes in the image element from wikipedia and extracts the url
   * 
   * If the species has an image, the format is:
   *    <img src="IMAGE_URL" srcset="IMAGE_URL_SIZE:1.5x, IMAGE_URL_SIZE:2x">
   * @param trendImg html for the species image in the table
   */
  private static String scrapeImageLink(Element image) {
    String imageLink = "";
    if (image != null) {
      imageLink = image.absUrl("src");
    }
    return imageLink;
  }

  public static PopulationTrend convertToPopulationTrendEnum(String trendString) {
    try {
      return PopulationTrend.valueOf(trendString.trim().toUpperCase());
    } catch(IllegalArgumentException ex) {
      return PopulationTrend.UNKNOWN;
    }
  }

  private static String removeBrackets(String og) {
    return og.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ");
  }

  private static long convertPopulationLong(String populationString) {
    String popValues[]= populationString.split("–|-");
    try {
        if (popValues.length == 1) {
            return Long.parseLong(populationString);
        }
        else {
            long popAverage = (Long.parseLong(popValues[0]) + Long.parseLong(popValues[1])) / 2;
            return popAverage;
        }
    }
    catch (NumberFormatException n) {
        System.out.println("Error: Wikipedia population '" + populationString + "' had incorrect format.");
        return -1;
    }
  }
}
