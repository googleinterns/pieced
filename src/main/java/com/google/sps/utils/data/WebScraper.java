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

public class WebScraper {
  private static final String LIST_URL = "https://en.wikipedia.org/wiki/Lists_of_organisms_by_population";
  private static final String LIST_CONTENT_CLASS = "mw-parser-output";
  private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
  private static KeyFactory keyFactory = datastore.newKeyFactory().setKind("Species-2");

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
  public static List<String> parseListofPages() throws IOException {
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

  public static Map<String, Animal> parseSpeciesTable(String url) throws IOException {
    Document doc = Jsoup.connect(url).get();
    Map<String, Animal> animals = new HashMap<String, Animal>();

    Elements table = doc.getElementsByClass("wikitable");
    for (Element head : table.select("tbody")) {
      for (Element row : head.select("tr")) {
        Elements tds = row.select("td");
        if (tds.size() > 6) {
          String commonName = tds.get(0).text();
          String binomialName = tds.get(1).text();
          String populationString = cleanPopulation(tds.get(2).text());
          String status = cleanStatus(tds.get(3).text());
          String trend;
          Element trendImg = tds.get(4).select("img").first();
          if (trendImg != null) {
            trend = trendImg.attr("alt");
          } else {
            trend = "Unknown";
          }
          String notes = tds.get(5).text();
          String imgUrl = "";
          Element image = tds.get(6).select("img").first();
          if (image != null) {
            imgUrl = image.absUrl("src");
          }
          System.out.printf("%-35s %-30s %-25s %-10s %-15s %n", commonName, binomialName, populationString, status, trend);
//           Animal animal = new Animal(commonName, binomialName, populationString, status, trend, notes, imgUrl);
//           animal.addCitationLink(url);
//           animals.put(binomialName, animal);
          
        //   System.out.println(commonName + "     " + binomialName + "     " + populationString + "     " + status + "     " + trend);
            // Proceed if we have the binomialName for this species and it isn't stored in Datastore
            if ((binomialName == null) || (binomialName.length() <= 0)) {
                System.out.println("'" + commonName + "' did not have a binomialName, skipping.");
                continue;
            }
            if (!speciesAlreadyStored(binomialName)) {
                System.out.println("Processing '" + binomialName + "'...");
                addWikipediaSpeciesInfoToMap(commonName, binomialName, status, trend, populationString);
                retrieveAndAddAPISpeciesInfoToMap(binomialName);
                addSpeciesToDatastore(DataCollection.speciesMap.get(binomialName));
                System.out.println("\n");
            }
        }
      }
    }
    return animals;
  }

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

  private static String removeBrackets(String og) {
    return og.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ");
  }

    /* Retrieves apiMap for the passed species and adds additional info to DataCollection.speciesMap
     * binomialName: non-null species name that isn't already stored in Datastore
     */
    public static void retrieveAndAddAPISpeciesInfoToMap(String binomialName) {
        // Add API-side fields if available
        try {
            String apiJSON = SpeciesAPIRetrieval.getJSON(DataCollection.API_URL, binomialName);
            Map apiMap = SpeciesAPIRetrieval.convertJSONToMap(apiJSON);
            SpeciesAPIRetrieval.addAPISpeciesInfoToMap(binomialName, apiMap);
        }
        catch (Exception e) {
            System.out.println("Exception occurred.");
            e.printStackTrace();
        }
    }

    /* Adds the passed species to DataCollection's speciesMap with information from Wikipedia
     * binomialName: non-null species name that isn't already stored in Datastore
     */
    public static void addWikipediaSpeciesInfoToMap(String commonName, String binomialName, String status,
                                                    String trendString, String populationString) { // move to speciesapiretrieval as static
        PopulationTrend trend = DataCollection.getPopulationTrend(trendString);
        Animal animal = new Animal(commonName, binomialName, status, trend, populationString, null, null);
        DataCollection.speciesMap.put(binomialName, animal);
    }

    // Returns true if species is already stored in Datastore, false otherwise
    private static boolean speciesAlreadyStored(String binomialName) {
        Key key = keyFactory.newKey(binomialName);
        Entity speciesEntity = datastore.get(key);
        if (speciesEntity != null) {
            System.out.println(binomialName + " was already in Datastore.");
            return true;
        }

        return false;
    }

    public static void addSpeciesToDatastore(Animal animal) { // move to datacollection
        if (animal == null) {
            System.out.println("This animal is null in speciesMap!!");
            return;
        }

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
                .build();
            datastore.put(animalEntity);
            System.out.println(animal.getBinomialName() + " was added to Datastore.");
        }
        else {
            System.out.println(animal.getBinomialName() + " was already in Datastore.");
        }
    }

}