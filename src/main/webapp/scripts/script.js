$(document).ready(function() {
  // Load navigation bar
  $(function() {
    $("#navigation").load("navigation.html");
  });

  /** Add navbar background color when scrolled */
  $(window).scroll(function() {
    if ($(window).scrollTop() > 56) {
      $(".navbar").addClass("bg-nav");
    } else {
      $(".navbar").removeClass("bg-nav");
    }
  });

  /** If Mobile, add background color when toggler is clicked */
  $(".navbar-toggler").click(function() {
    if (!$(".navbar-collapse").hasClass("show")) {
      $(".navbar").addClass("bg-nav");
    } else {
      if ($(window).scrollTop() < 56) {
        $(".navbar").removeClass("bg-nav");
      } else {
      }
    }
  });
});