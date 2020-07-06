package com.google.sps.utils.data;

import com.google.sps.utils.data.WebScraper;
import com.google.sps.utils.data.SpeciesAPIRetrieval;
import com.google.sps.utils.data.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileWriter;
import com.google.gson.Gson;
import java.util.Map;
import java.util.HashMap;

public class DataCollection {
    public static final String API_URL = "https://api.gbif.org/v1/species/match?name=";

    /* Mapping of species' binomial name to its corresponding Animal object
     */
    public static Map<String, Animal> speciesMap = new HashMap<String, Animal>(); 
  
    public static void main (String[] args) throws IOException {
      List<String> urls = WebScraper.parseListofPages();
      for (String url: urls) {
        System.out.println("NEW PAGE: " + url);
        Map<String, Animal> animals = WebScraper.parseSpeciesTable(url);
        speciesMap.putAll(animals);
      //   System.out.println(animals.toString());
        System.out.println("\n");
      //   break;
      }
      // System.out.println(speciesMap.toString());
      writeJson(speciesMap);
    }

    public static void writeJson(Map<String, Animal> speciesMap) throws IOException{
      Gson gson = new Gson();
      gson.toJson(speciesMap, new FileWriter("src/main/java/com/google/sps/utils/data/animals-test.json"));
    }

    public static PopulationTrend getPopulationTrend(String trendString) {
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