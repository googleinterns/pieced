$(document).ready(function() {
    // Load navigation bar
    $("#navigation").load("navigation.html");
    $('.header').height($(window).height());
    $(window).on('scroll', function () {
        if ( $(window).scrollTop() > 10 ) {
            $('.navbar').addClass('bg-nav');
        } else {
            $('.navbar').removeClass('bg-nav');
        }
    });
});