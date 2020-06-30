package com.google.sps.utils.data;

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
          String populationString = tds.get(2).text(); // @TODO: Clean up this string. Currently has [#] for reference and commas
          populationString = populationString.replaceAll(",","").replaceAll("\\s+","");
          String status = tds.get(3).text().replaceAll("Domesticated", "D");
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
          System.out.printf("%-25s %-30s %-25s %-10s %-15s %s %n", commonName, binomialName, populationString, status, trend, imgUrl);
          Animal animal = new Animal(commonName, binomialName, populationString, status, trend, notes, imgUrl);
          animal.addCitationLink(url);
          animals.put(binomialName, animal);
        }
      }
    }
    return animals;
  }
}