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
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;

/** Servlet that queries data from all species. */
@WebServlet("/allData")
public class AllDataServlet extends HttpServlet {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String sortBy = request.getParameter("sortBy");

        // Initialize and run a query that will select the specific species from Datastore by filtering by scientific name
        Query<Entity> query = Query.newEntityQueryBuilder()
            .setKind("Species")
            .setOrderBy(OrderBy.asc(sortBy))
            .build();

        QueryResults<Entity> queriedSpecies = datastore.run(query);
        List<Species> data = new ArrayList<>();

        queriedSpecies.forEachRemaining( speciesData -> {
            // Grab information from Datastore entry and construct Species object
            Species species = convertEntityToSpecies(speciesData);
            data.add(species);
        });

        // Convert Species object to JSON and send it back to caller
        String json = convertToJson(data);
        response.getWriter().println(json);
    }

    // Grab information from Datastore entry and construct Species object
    protected Species convertEntityToSpecies(Entity speciesData) {
        String commonName       = speciesData.getString("common_name");
        String binomialName     = speciesData.getString("binomial_name");
        String status           = speciesData.getString("status");
        long population         = speciesData.getLong("population");
        String wikipediaNotes   = speciesData.getString("wikipedia_notes");
        String imageLink        = speciesData.getString("image_link");
        String citationLink     = speciesData.getString("citation_link");
        PopulationTrend trend   = DataCollection.convertToPopulationTrendEnum(speciesData.getString("trend"));
        TaxonomicPath taxonomy  = new TaxonomicPath(
                                    speciesData.getString("kingdom"),
                                    speciesData.getString("phylum"),
                                    speciesData.getString("class"),
                                    speciesData.getString("order"),
                                    speciesData.getString("family"),
                                    speciesData.getString("genus"));

        Species species = new Species.Builder()
                            .withCommonName(commonName)
                            .withBinomialName(binomialName)
                            .withStatus(status)
                            .withPopulationTrend(trend)
                            .withPopulation(population)
                            .withWikipediaNotes(wikipediaNotes)
                            .withImageLink(imageLink)
                            .withCitationLink(citationLink)
                            .withTaxonomicPath(taxonomy)
                            .build();
                                            
        return species;
    }

    // Convert List of data to JSON using Gson library.
    private String convertToJson(List<Species> data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        return json;
    }
}
