// Played around with the following packages: masonry.pkgd.js, imagesloaded.pkgd.js
// May need to use different libraries due to commercial use requirement
// Masonry is released under the MIT license: https://desandro.mit-license.org/

// Initialize Masonry
var $grid = $('.grid').masonry({
    itemSelector: '.grid-item',
    percentPosition: true,
    columnWidth: '.grid-sizer',
    horizontalOrder: true
});

// Load JSON file
var data = JSON.parse(animals-test);
console.log(data);

var imageArray = [
    "test-images/deer.jpg",
    "test-images/giraffe.jpg",
    "test-images/owl.jpg",
    "test-images/pangolin.jpg",
    "test-images/snake.jpg",
    "test-images/squirrel.jpeg",
    "test-images/tiger.jpeg",
    "test-images/toucan.jpg",
    "test-images/peacock.jpeg",
    "test-images/porcupine.jpg",
    "test-images/red-panda.png",
    "test-images/seal.jpg"
];

for (i = 0; i < imageArray.length; i++) {
    // Append images to grid
    var $html = $('<div class="grid-item"> <img src="'+ imageArray[i] + '" /> </div>'); 
    $grid.append($html)
        // add and lay out newly appended items
        .masonry('appended', $html);
}

// Layout Masonry after each image finishes loading
$grid.imagesLoaded().progress( function() {
    $grid.masonry('layout');
});


