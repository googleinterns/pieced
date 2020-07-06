package com.google.sps.utils.data;

import com.google.sps.utils.data.Animal;
import com.google.sps.utils.data.DataCollection;
import com.google.sps.utils.data.PopulationTrend;
import com.google.sps.utils.data.SpeciesAPIRetrieval;
import com.google.sps.utils.data.Animal;
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
    Map<String, Animal> animals = new HashMap<String, Animal>();

    Elements table = doc.getElementsByClass("wikitable");
    for (Element head : table.select("tbody")) {
      for (Element row : head.select("tr")) {
        Elements tds = row.select("td");

        Animal animal = processAnimal(tds, url);
        if (animal == null || !animal.hasNecessaryInfo()) {
          continue;
        }

        if (!speciesAlreadyStored(animal.getBinomialName())) {
          System.out.println("Processing '" + animal.getBinomialName() + "'...");
          addApiInfo(animal);
          System.out.println(animal.getTaxonomy().getAnimalKingdom());
          addAnimalToDatastore(animal);
        }
      }
    }
    return animals;
  }

  /**
   * Get Animal information from each row of the table
   * @param tds: The row Element to scrape for info
   * @param url: url that contains the table for citation
   * @return Animal object with filled fields, or null if incomplete
   */
  private static Animal processAnimal(Elements tds, String url) {
    if (tds.size() > 6) {
      String commonName = tds.get(0).text();
      String binomialName = tds.get(1).text();
      String population = cleanPopulation(tds.get(2).text());
      String status = cleanStatus(tds.get(3).text());
      PopulationTrend trend = getTrend(tds.get(4).select("img").first());
      String notes = tds.get(5).text();
      String imageLink = "";
      Element image = tds.get(6).select("img").first();
      if (image != null) {
        imageLink = image.absUrl("src");
      }
      System.out.printf("%-35s %-30s %-25s %-10s %-15s %n", commonName, binomialName, population, status, trend);
      Animal animal = new Animal(commonName, binomialName, status, trend, population, notes, imageLink, url);
      return animal;
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
    if (speciesEntity != null) {
      System.out.println(binomialName + " was already in Datastore.");
      return true;
    }
    return false;
  }

  /**
   * Retrieves apiMap for the passed species and adds additional info to animal
   * @param animal: non-null animal that isn't already stored in Datastore
   */
  public static void addApiInfo(Animal animal) {
    // Add API-side fields if available
    try {
      String apiJSON = SpeciesAPIRetrieval.getJSON(DataCollection.API_URL, animal.getBinomialName());
      Map apiMap = SpeciesAPIRetrieval.convertJSONToMap(apiJSON);
      SpeciesAPIRetrieval.addAPISpeciesInfo(animal, apiMap);
    } catch (Exception e) {
      System.out.println("Exception occurred.");
      e.printStackTrace();
    }
  }

  /**
   * Store animal in Datastore
   * @param animal: animal to store
   */
  public static void addAnimalToDatastore(Animal animal) {
    Key key = keyFactory.newKey(animal.getBinomialName());
    Entity oldEntity = datastore.get(key);
    
    if (oldEntity == null) {
      Entity animalEntity = Entity.newBuilder(key)
        .set("common_name", animal.getCommonName())
        .set("binomial_name", animal.getBinomialName())
        .set("trend", animal.getTrend().name())
        .set("status", animal.getStatus())
        .set("population", animal.getPopulation())
        .set("kingdom", animal.getTaxonomy().getAnimalKingdom())
        .set("phylum", animal.getTaxonomy().getAnimalPhylum())
        .set("class", animal.getTaxonomy().getAnimalClass())
        .set("order", animal.getTaxonomy().getAnimalOrder())
        .set("family", animal.getTaxonomy().getAnimalFamily())
        .set("genus", animal.getTaxonomy().getAnimalGenus())
        .set("image_link", animal.getImageLink())
        .set("wikipedia_notes", animal.getWikipediaNotes())
        .set("citation_link", animal.getCitationLink())
        .build();
      
      datastore.put(animalEntity);
      System.out.println(animal.getBinomialName() + " was added to Datastore.");
    } else {
      System.out.println(animal.getBinomialName() + " was already in Datastore.");
    }
  }

  // ------------------------------  SCRAPING HELPER FUNCTIONS  ------------------------------ //
  private static String cleanStatus(String statusString) {
    String status = statusString.replaceAll("Domesticated", "D");
    status = removeBrackets(status);
    return status;
  }

  private static String cleanPopulation(String populationString) {
    String pop = removeBrackets(populationString);
    pop = pop.replaceAll("[^0-9.–-]", "");
    return pop;
  }

  private static PopulationTrend getTrend(Element trendImg) {
    String trend;
    if (trendImg != null) {
      trend = trendImg.attr("alt");
    } else {
      trend = "Unknown";
    }
    return convertPopulationTrend(trend);
  }

  public static PopulationTrend convertPopulationTrend(String trendString) {
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
