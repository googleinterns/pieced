package com.google.sps.utils.data;

import com.google.sps.utils.data.WebScraper;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class DataCollection {
  
  public static void main (String[] args) throws IOException {
    List<String> urls = WebScraper.parseListofPages();
    for (String url: urls) {
      System.out.println("NEW PAGE: " + url);
      WebScraper.parseSpeciesTable(url);
      System.out.println("\n");
    }
  }
}