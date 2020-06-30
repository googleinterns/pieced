package com.google.sps.utils.data;


import com.google.gson.Gson;
import com.google.sps.utils.data.Animal;
import com.google.sps.utils.data.Taxonomy;
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

    // Returns converted map of JSON, or null if incorrect JSON format
    public static Map convertJSONToMap(String jsonString) {
        Gson gson = new Gson();
        Map map = gson.fromJson(jsonString, Map.class);

        try {
            if (map.get("matchType").equals("EXACT")) {
                return map;
            }
        }
        catch (NullPointerException e) {
            return null;
        }
        return null;
        // Assert.assertEquals(Double.class, map.get("employee.salary").getClass());
    }

    // Updates the DataCollection speciesMap with fields from converted JSON map
    public static void updateMap(String canonicalName, Map jsonMap) {
        if (jsonMap == null) {
            return;
        }

        // Update DataCollection.speciesMap with values from jsonMap, search map by canonicalName
        String kingdom = jsonMap.get("kingdom").toString();
        String phylum = jsonMap.get("phylum").toString();
        String class_t = jsonMap.get("class").toString();
        String order = jsonMap.get("order").toString();
        String family = jsonMap.get("family").toString();
        String genus = jsonMap.get("genus").toString();
        Taxonomy taxonomy = new Taxonomy(kingdom, phylum, class_t, order, family, genus);
        Taxonomy.printTaxonomy(taxonomy);
        if (DataCollection.speciesMap.get(canonicalName) != null) {
            DataCollection.speciesMap.get(canonicalName).setTaxonomy(taxonomy);
        }
        return;
    }
    
}