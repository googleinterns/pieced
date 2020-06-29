package com.google.sps.utils.data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SpeciesAPIRetrieval {
    private static final String apiURL = "https://api.gbif.org/v1/species/5231190";

    public static void main(String[] args) {
        try {
            get(apiURL);
        }
        catch (Exception e) {
            System.out.println("Exception occurred.");
            e.printStackTrace();
        }
    }

    public static void get(String uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
    
}