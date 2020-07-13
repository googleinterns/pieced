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

/** Servlet that queries data from all species. */
@WebServlet("/allData")
public class AllDataServlet extends HttpServlet {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    // Lightweight dummy class containing only the necessary parameters for gallery.html
    public class RequiredData {
        /*
        String commonName   : common name for species
        String imageLink    : url for image
        */
        public String commonName;
        public String imageLink;

        public RequiredData(String commonName, String imageLink) {
            this.commonName = commonName;
            this.imageLink = imageLink;
        }
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Initialize and run a query that will select the specific species from Datastore by filtering by scientific name
        Query<Entity> query = Query.newEntityQueryBuilder().setKind("Species").build();
        QueryResults<Entity> queriedSpecies = datastore.run(query);

        List<RequiredData> data = new ArrayList<>();

        queriedSpecies.forEachRemaining( speciesData -> {
            // Grab information from Datastore entry and construct Species object
            String commonName       = speciesData.getString("common_name");
            String imageLink        = speciesData.getString("image_link");

            RequiredData species = new RequiredData(commonName, imageLink);
            data.add(species);
        });
        // Convert Species object to JSON and send it back to caller
        String json = convertToJson(data);
        response.getWriter().println(json);
    }

    // Convert List of data to JSON using Gson library.
    private String convertToJson(List<RequiredData> data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        return json;
    }
}
