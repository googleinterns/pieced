# PIECED
![GitHub issues](https://img.shields.io/github/issues-raw/googleinterns/step129-2020?style=flat-square) ![GitHub](https://img.shields.io/github/license/googleinterns/step129-2020?style=flat-square)

**[LIVE DEMO](https://step129-2020.appspot.com)**

PIECED aims to visualize the declining populations of different species and highlight their vulnerability status. We hope to spread awareness to the public about these environmental issues and showcase just how close some species are to extinction.

Inspired by the 2008 WWF Japan Campaign [Population By Pixel](https://www.boredpanda.com/endagered-animals-pixels-extinction/?utm_source=google&utm_medium=organic&utm_campaign=organic), our project highlights population disparities by pixelating species images based on the number of individuals left. We manipulated CSS filters to alter image blurring and smoothing, giving us a controllable pixelation effect where the number of pixels in the image is approximately equal to the estimated population of a given species. We also added animations to transition between the original, full-resolution image and its pixelated counterpart.

<p float="left" align="center">
  <img src="/images/macaw-fullres.png" width="200" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="/images/macaw-animated.gif" width="200" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="/images/macaw-pixelated.png" width="200" />
</p>

## Meet the Team
PIECED was developed by Fiza ([@fgoyal](https://github.com/fgoyal)),
Ariana ([@arianazhu](https://github.com/arianazhu)), and
Maxwell ([@maxhchen](https://github.com/maxhchen)) as a capstone project for Google's
2020 STEP internship program.

## Data Collection
We collected data on over 1200 species. Most of the data was extracted by web-scraping [Wikipedia](https://en.wikipedia.org/wiki/Lists_of_organisms_by_population), but we supplemented it by retrieving data from the [GBIF API](https://www.gbif.org/developer/species) and [Google Knowledge Graph Search API](https://developers.google.com/knowledge-graph). 

Information was collected as follows: 
| Wikipedia  | GBIF | Knowledge Graph |
| ------------- | ------------- | ------------- |
| Scientific name (key)  | Kingdom  | Description |
| Common name  | Phylum  | - |
| Population  | Class  | - |
| Population trend  | Order  | - |
| IUCN status  | Family  | - |
| Image  | Genus  | - |

## Technologies Used

This project uses the following tools/libraries:

- [AppEngine on Google Cloud](https://cloud.google.com/appengine) for deploying
- [Java Servlets](https://docs.oracle.com/javaee/5/tutorial/doc/bnafe.html) for back-end
- [jsoup](https://jsoup.org/) for web-scraping Wikipedia
- [GBIF API](https://www.gbif.org/developer/species) for data collection
- [Google Knowledge Graph Search API](https://developers.google.com/knowledge-graph) for data collection
- [Google Cloud Datastore](https://cloud.google.com/datastore) for storing data
- [Bootstrap 4](https://getbootstrap.com/docs/4.5/getting-started/introduction/) for setting up the front-end
- [Masonry.js](https://masonry.desandro.com/) for the gallery view

## Usage and Deployment

To run the project, you need to install Maven and the Google Cloud
Platform SDK.

You can run a local test server using
```
mvn package appengine:run
```

You can run just the Java data collection files using
```
mvn exec:java
```

## License
[Apache License 2.0](LICENSE.md)

Copyright 2020 Fiza Goyal, Ariana Zhu, Maxwell Chen
