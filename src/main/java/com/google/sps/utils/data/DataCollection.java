package com.google.sps.utils.data;

import com.google.sps.utils.data.WebScraper;
import com.google.sps.utils.data.SpeciesAPIRetrieval;
import com.google.sps.utils.data.Animal;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class DataCollection {
    public static final String API_URL = "https://api.gbif.org/v1/species/match?name=";
    public static Map<String, Animal> speciesMap = new HashMap<String, Animal>(); 
  
    public static void main (String[] args) throws IOException {
        List<String> urls = WebScraper.parseListofPages();
        for (String url: urls) {
            System.out.println("NEW PAGE: " + url);
            WebScraper.parseSpeciesTable(url);
            System.out.println("\n");
        }

        // try {
        //     String canonicalName = "Passer domesticus";
        //     String apiJSON = SpeciesAPIRetrieval.getJSON(API_URL, canonicalName);
        //     // convert json to map
        //     Map jsonMap = SpeciesAPIRetrieval.convertJSONToMap(apiJSON);
        //     System.out.println("Canonical Name: " + jsonMap.get("canonicalName"));
        //     SpeciesAPIRetrieval.updateMap(canonicalName, jsonMap);
        // }
        // catch (Exception e) {
        //     System.out.println("Exception occurred.");
        //     e.printStackTrace();
        // }
    }

}