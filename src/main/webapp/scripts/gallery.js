// Played around with the following packages: masonry.pkgd.js, imagesloaded.pkgd.js
// May need to use different libraries due to commercial use requirement
// Masonry is released under the MIT license: https://desandro.mit-license.org/

// Initialize Masonry
console.log("initializing masonry");
var $grid = $('.grid').masonry({
    itemSelector: '.grid-item',
    percentPosition: true,
    columnWidth: '.grid-sizer',
    horizontalOrder: true
});

fetchAllSpeciesData();

// Test function that fetches sample JSON and appends each species to the gallery
function fetchAllSpeciesData() {
    fetch("/allData").then(response => response.json()).then(speciesData => {
        for (var species in speciesData) {
            // Append images to grid
            var $html = $('<div class="grid-item"> <a href="/species-template.html?species=' + speciesData[species].commonName + '"> <img src="'+ speciesData[species].imageLink +
                          '" /> <div class="overlay">' + speciesData[species].commonName + '</div> </a> </div>'); 
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