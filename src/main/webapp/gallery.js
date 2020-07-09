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

fetchSpeciesData();


// Test function that fetches sample JSON and appends each species to the gallery
function fetchSpeciesData() {
    fetch("test.json").then(response => response.json()).then(speciesData => {
        for (var species in speciesData) {
            // Append images to grid
            var $html = $('<div class="grid-item"> <img src="'+ speciesData[species].imageLink +
                          '" /> <div class="overlay">' + speciesData[species].commonName + '</div> </div>'); 
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