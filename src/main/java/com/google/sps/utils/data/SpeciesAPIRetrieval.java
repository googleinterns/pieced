package com.google.sps.utils.data;

import com.google.gson.Gson;
import com.google.sps.utils.data.Species;
import com.google.sps.utils.data.TaxonomicPath;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpeciesAPIRetrieval {

    // Retrieves JSON response from given url with the species parameter
    public static String getJSON(String base, String species) throws IOException, InterruptedException {
        String uri = base + encode(species);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Encode a string value using `UTF-8` encoding scheme
    private static String encode(String url) {
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
    * Will be used to convert API JSON to a Map for easier access
    * @return converted map of JSON, or null if incorrect JSON format
    */
    public static Map convertJSONToMap(String jsonString) {
        Gson gson = new Gson();
        Map map = gson.fromJson(jsonString, Map.class);
        try {
            if (map.get("matchType").equals("EXACT")) {
                return map;
            }
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }

    /**
    * Will be used to convert API JSON to a Map for easier access
    * @return converted map of JSON, or null if incorrect JSON format
    */
    public static HashMap convertGeoToMap(String jsonString) {
        Gson gson = new Gson();
        HashMap map = gson.fromJson(jsonString, HashMap.class);
        return map;
    }

    /**
    * Updates Species with fields from converted JSON map from GBIF 
    * @param species: species to add fields to
    * @param apiMap: map of the JSON returned from API call to this species
    */
    public static void addGBIFInfo(Species species, Map apiMap) {
        if (species == null) {
            return;
        }

        if (apiMap == null) {
            System.out.println("No results found in GBIF API for '" + species.getBinomialName() + "'.");
            return;
        }

        // Update DataCollection.speciesMap with values from apiMap
        String kingdom = apiMap.get("kingdom").toString();
        String phylum = apiMap.get("phylum").toString();
        String class_t = apiMap.get("class").toString();
        String order = apiMap.get("order").toString();
        String family = apiMap.get("family").toString();
        String genus = apiMap.get("genus").toString();
        TaxonomicPath taxonomicPath = new TaxonomicPath(kingdom, phylum, class_t, order, family, genus);
        
        species.setTaxonomicPath(taxonomicPath);
        return;
    }

    /**
    * Updates Species with fields from converted JSON map from Knowledge Graph 
    * @param species: species to add fields to
    * @param jsonString: JSON string returned from API call to this species
    */
    public static void addKGInfo(Species species, String jsonString) {
        if (species == null) {
            return;
        }
        if (jsonString == null) {
            System.out.println("No results found in kg API for '" + species.getBinomialName() + "'.");
            species.setNotes("N/A");
            return;
        }

        // Add data from Knowledge Graph as the species' notes
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray itemListElement = (JSONArray) jsonObject.get("itemListElement");
            String notes = itemListElement
                .getJSONObject(0)
                .getJSONObject("result")
                .getJSONObject("detailedDescription")
                .getString("articleBody");
            species.setNotes(notes);
        } catch (JSONException j){
            System.out.println("error: " + j);
            species.setNotes("N/A");
        }
        return;
    }
  
    /**
    * Updates Species with geographical coordinates from converted JSON map
    * @param species: species to add fields to
    * @param apiMap: map of the JSON returned from API call to this species
    */
    public static void addAPIGeoInfo(Species species, Map apiMap) {
        if (species == null) {
            return;
        }

        if (apiMap == null) {
            System.out.println("No results found in GBIF API for '" + species.getBinomialName() + "'.");
            return;
        }

        for (Object i: (ArrayList) apiMap.get("results")) {
            System.out.println(((Map) i).getOrDefault("decimalLatitude", null));
            System.out.println(((Map) i).getOrDefault("decimalLongitude", null));
        }

        return;
    }  
}