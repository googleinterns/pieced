package com.google.sps.utils.data;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainScraper {
  public static void main (String[] args) throws IOException {
    String url = getPageURL();
    Document doc = Jsoup.connect(url).get();

    Elements table = doc.getElementsByClass("wikitable");
    for (Element head : table.select("tbody")) {
      for (Element row : head.select("tr")) {
        Elements tds = row.select("td");
        if (tds.size() > 6) {
          String commonName = tds.get(0).text();
          String binomialName = tds.get(1).text();
          String population = tds.get(2).select("b").text();
          System.out.println(commonName + ":" + population);
        }
      }
    }
  }

  private static String getPageURL() {
    return "https://en.wikipedia.org/wiki/List_of_carnivorans_by_population";
  }
}