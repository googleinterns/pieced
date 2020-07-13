// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.utils.data.Species;
import com.google.sps.utils.data.PopulationTrend;
import com.google.sps.utils.data.DataCollection;
import com.google.sps.utils.data.TaxonomicPath;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.*;

/** Servlet that grabs data on individual species. */
@WebServlet("/speciesData")
public class SpeciesDataServlet extends HttpServlet {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Parse identifying species name from query string
        String speciesName = getSpeciesName(request);
        
        // Return invalid data if species name is null or empty
        if (speciesName == null) {
            String json = generateInvalidResponse(response, "No species name requested.");
            response.getWriter().println(json);
            return;
        }

        // Initialize and run a query that will select the specific species from Datastore by filtering by scientific name
        Query<Entity> query = Query.newEntityQueryBuilder().setKind("Species").setFilter(PropertyFilter.eq("common_name", speciesName)).build();
        QueryResults<Entity> queriedSpecies = datastore.run(query);

        if (!queriedSpecies.hasNext()) {
            String json = generateInvalidResponse(response, "No results in datastore.");
            response.getWriter().println(json);
            return;
        }

        // There will be at most one entry returned by any query due to how we add and modify species to Datastore,
        // so we only need to call queriedSpecies.next() a single time
        Entity speciesData = queriedSpecies.next();

        // Grab information from Datastore entry and construct Species object
        String commonName       = speciesData.getString("common_name");
        String binomialName     = speciesData.getString("binomial_name");
        String status           = speciesData.getString("status");
        String population       = speciesData.getString("population");
        String wikipediaNotes   = speciesData.getString("wikipedia_notes");
        String imageLink        = speciesData.getString("image_link");
        String citationLink     = speciesData.getString("citation_link");
        PopulationTrend trend   = DataCollection.convertToPopulationTrendEnum(speciesData.getString("trend"));
        TaxonomicPath taxonomy  = new TaxonomicPath(speciesData.getString("kingdom"),
                                                    speciesData.getString("phylum"),
                                                    speciesData.getString("class"),
                                                    speciesData.getString("order"),
                                                    speciesData.getString("family"),
                                                    speciesData.getString("genus"));

        Species species = new Species(commonName,
                                            binomialName,
                                            status,
                                            trend,
                                            population,
                                            wikipediaNotes,
                                            imageLink,
                                            citationLink);
        species.setTaxonomicPath(taxonomy);

        // Convert Species object to JSON and send it back to caller
        String json = convertToJson(species);
        response.getWriter().println(json);
    }

    // Convert species data to JSON using Gson library.
    private String convertToJson(Species data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        return json;
    }
    
    // Extracts species name from request and returns it.
    private String getSpeciesName(HttpServletRequest request) {
        String speciesName = request.getParameter("species");

        // Prevent accidental/blank submissions.
        if (speciesName.equals("")) {
            return null;
        }
        return speciesName;
    }

    // Builds an invalid JSON response when the request is invalid or misses in the datastore.
    private String generateInvalidResponse(HttpServletResponse response, String errorMessage) {
        System.err.println("Invalid fetch request: " + errorMessage);
        Species species = new Species(null,
                                null,
                                null,
                                PopulationTrend.UNKNOWN,
                                null,
                                null,
                                null,
                                null);

        species.setTaxonomicPath(null);
        String json = convertToJson(species);
        return json;
    }
}
