package com.google.sps.utils.data;

import com.google.sps.utils.data.WebScraper;
import com.google.sps.utils.data.SpeciesAPIRetrieval;
import java.io.IOException;

public class DataCollection {
    private static final String apiURL = "https://api.gbif.org/v1/species/5231190";
  
    public static void main (String[] args) throws IOException {
        WebScraper.parseSpeciesTable("https://en.wikipedia.org/wiki/List_of_carnivorans_by_population");

        try {
            SpeciesAPIRetrieval.get(apiURL);
        }
        catch (Exception e) {
            System.out.println("Exception occurred.");
            e.printStackTrace();
        }
    }
}