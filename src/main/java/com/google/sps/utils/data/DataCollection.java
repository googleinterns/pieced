package com.google.sps.utils.data;

import com.google.sps.utils.data.WebScraper;

import java.io.IOException;

public class DataCollection {
  
  public static void main (String[] args) throws IOException {
    WebScraper.parseListofPages();
    // WebScraper.parseSpeciesTable("https://en.wikipedia.org/wiki/List_of_carnivorans_by_population");
  }
}