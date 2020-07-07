// Played around with the following packages: masonry.pkgd.js, imagesloaded.pkgd.js
// May need to use different libraries due to commercial use requirement

// Initialize Masonry
var $grid = $('.grid').masonry({
  itemSelector: '.grid-item',
  percentPosition: true,
  columnWidth: '.grid-sizer'
});
// Layout Masonry after each image finishes loading
$grid.imagesLoaded().progress( function() {
  $grid.masonry();
});  
