// Played around with the following packages: masonry.pkgd.js, imagesloaded.pkgd.js
// May need to use different libraries due to commercial use requirement
// Masonry is released under the MIT license: https://desandro.mit-license.org/

// Initialize Masonry
console.log("initializing masonry");
var $grid = $('.grid').masonry({
    itemSelector: '.grid-item',
    percentPosition: true,
    horizontalOrder: true
});

fetchAllSpeciesData();

// Test function that fetches sample JSON and appends each species to the gallery
function fetchAllSpeciesData(status, animal_class) {
    const parameters = {'status': status, 'class': animal_class};
    const url = createQueryString("/allData", parameters);

    fetch(url).then(response => response.json()).then(speciesData => {
        for (var species in speciesData) {
            // Append images to grid
            var $html = $(
                '<div class="grid-filters">' +
                  '<div class="grid-item">' +
                    '<img src="'+ speciesData[species].imageLink +'" />' +
                    '<div class="overlay">' + 
                      '<a href="/species-template.html?species=' + speciesData[species].commonName + '"> ' + speciesData[species].commonName + '</a>' +
                    '</div> ' +
                  '</div>' +
                '</div>'); 

            $grid.append($html)
                // add and lay out newly appended items
                .masonry('appended', $html);
        }

        // Layout Masonry after each image finishes loading
        $grid.imagesLoaded().progress( function() {
            $grid.masonry('layout');
        });
    });
}

/**
 * Create query string from parameters
 */
function createQueryString(url, parameters) {
  const query = Object.entries(parameters)
        .map(pair => pair.map(encodeURIComponent).join('='))
        .join('&');
  return url + "?" + query;
}