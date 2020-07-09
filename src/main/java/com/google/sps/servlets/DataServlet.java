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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.classes.Comment;
import com.google.gson.Gson;
import java.util.*;

/** Servlet that grabs data on individual species. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  PreparedQuery queriedSpeciesData;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String speciesName = "Accipiter bicolor";
    Query query = new Query("Species").setFilter(PropertyFilter.eq("binomial_name", speciesName));
    queriedSpeciesData = datastore.prepare(query);

    String commonName =     (String) queriedSpeciesData.getProperty("common_name");
    String binomialName =   (String) queriedSpeciesData.getProperty("binomial_name");
    String status =         (String) queriedSpeciesData.getProperty("status");
    String population =     (String) queriedSpeciesData.getProperty("population");
    String wikipediaNotes = (String) queriedSpeciesData.getProperty("wikipedia_notes");
    String imageLink =      (String) queriedSpeciesData.getProperty("image_link");
    String citationLink =   (String) queriedSpeciesData.getProperty("citation_link");
    PopulationTrend trend = (PopulationTrend) queriedSpeciesData.getProperty("trend");

    Species queriedSpecies = new Species(commonName,
                                         binomialName,
                                         status,
                                         trend,
                                         population,
                                         wikipediaNotes,
                                         imageLink,
                                         citationLink);

    String json = convertToJson(queriedSpecies);
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
  }

  // Convert List to JSON using Gson library.
  private String convertToJson(List<Comment> msgs) {
    Gson gson = new Gson();
    String json = gson.toJson(msgs);
    return json;
  }
 
 // Extracts comment text from request and returns it.
  private String getComment(HttpServletRequest request) {
    String speciesName = request.getParameter("species");

    // Prevent accidental/blank submissions.
    if (speciesName.equals("")) {
        return null;
    }
    return speciesName;
  }
}
