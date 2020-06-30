package com.google.sps.utils.data;

import com.google.sps.utils.data.DataCollection;
import com.google.sps.utils.data.PopulationTrend;
import com.google.sps.utils.data.SpeciesAPIRetrieval;
import com.google.sps.utils.data.Animal;

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

  public static void parseSpeciesTable(String url) throws IOException {
    Document doc = Jsoup.connect(url).get();

    Elements table = doc.getElementsByClass("wikitable");
    for (Element head : table.select("tbody")) {
      for (Element row : head.select("tr")) {
        Elements tds = row.select("td");
        if (tds.size() > 6) {
          String commonName = tds.get(0).text();
          String binomialName = tds.get(1).text();
          String populationString = tds.get(2).text(); // @TODO: Clean up this string. Currently has [#] for reference and commas
          String status = tds.get(3).text();
          String trend;
          Element trendImg = tds.select("img").first();
          if (trendImg != null) {
            trend = trendImg.attr("alt");
          } else {
            trend = "Unknown";
          }
        //   System.out.println(commonName + "     " + binomialName + "     " + populationString + "     " + status + "     " + trend);
            updateSpeciesMap(commonName, binomialName, status, trend);
        }
      }
    }
  }

    public static void updateSpeciesMap(String commonName, String binomialName, String status, String trendString) {
        PopulationTrend trend = getPopulationTrend(trendString);
        Animal animal = new Animal(commonName, binomialName, status, trend, 0, null, null);
        DataCollection.speciesMap.put(binomialName, animal);

        // Add API-side fields if available
        try {
            String apiJSON = SpeciesAPIRetrieval.getJSON(DataCollection.API_URL, binomialName);
            // convert json to map
            Map jsonMap = SpeciesAPIRetrieval.convertJSONToMap(apiJSON);

            SpeciesAPIRetrieval.updateMap(binomialName, jsonMap);
            System.out.println("Binomial Name: " + DataCollection.speciesMap.get(binomialName).getBinomialName());
            System.out.println("Trend: " + DataCollection.speciesMap.get(binomialName).getTrend());
            System.out.println("Genus: " + DataCollection.speciesMap.get(binomialName).getTaxonomy().getAnimalGenus());
        }
        catch (Exception e) {
            System.out.println("Exception occurred.");
            e.printStackTrace();
        }
    }

    private static PopulationTrend getPopulationTrend(String trendString) {
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
}