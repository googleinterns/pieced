// Played around with the following packages: masonry.pkgd.js, imagesloaded.pkgd.js
// May need to use different libraries due to commercial use requirement
// Masonry is released under the MIT license: https://desandro.mit-license.org/

// Initialize Masonry
var $grid = $('.grid').masonry({
    itemSelector: '.grid-item',
    percentPosition: true,
    columnWidth: '.grid-sizer',
    horizontalOrder: true,
    fitWidth: true
});

// Layout Masonry after each image finishes loading
$grid.imagesLoaded().progress( function() {
    $grid.masonry('layout');
});

