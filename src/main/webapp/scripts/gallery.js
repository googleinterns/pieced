// Played around with the following packages: masonry.pkgd.js, imagesloaded.pkgd.js
// May need to use different libraries due to commercial use requirement
// Masonry is released under the MIT license: https://desandro.mit-license.org/

// global variables
var filters = new Map()
var $active_filters_ul = $('.active-filters');
var $grid = $('#grid')

// Call these functions when page loads
$(document).ready(function() {
    // Initialize Masonry
    console.log("initializing masonry");
    $grid.masonry({
        itemSelector: '.grid-item',
        percentPosition: true,
        // horizontalOrder: true,
        columnWidth: '.grid-sizer'
    });

    // get all data
    fetchAllSpeciesData("common_name");

    // hides filters on click
    deleteFilter()
});

/** 
 *Fetches sample JSON and appends each species to the gallery
 * @param sortBy: the parameter to sort by
 */
function fetchAllSpeciesData(sortBy) {
    const parameters = {'sortBy': sortBy};
    const url = createQueryString("/allData", parameters);
    console.log(url)
    clearGallery();
    
    fetch(url).then(response => response.json()).then(speciesData => {
        for (var species in speciesData) {
            // Append images to grid
            var $html = $(
                '<div class="grid-filters ' + speciesData[species].status + ' ' + speciesData[species].trend + ' ' + speciesData[species].taxonomicPath.order_t + ' ' + speciesData[species].taxonomicPath.class_t + '">' +
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
        recompileAllFilters();
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

function clearGallery() {
    $grid.empty();
    $sizer = $('<div class="grid-sizer"></div>');
    $grid.append($sizer);
}

// ------------------------------------ FILTER FUNCTIONS ------------------------------------ //
function showClass(class_name) {
    $('.' + class_name).show();
}

function showAllClasses() {
    $('.grid-filters').show();
}

function hideClass(class_name) {
    $('.' + class_name).hide();
}

function hideAllClasses() {
    console.log("hideall")
    $('.grid-filters').hide();
}

function addFilter(class_name, category) {
    if (filters.has(class_name)) {
        return;
    }

    if (filters.size >= 5) {
        alert("Only 5 filters are allowed. Please remove a filter.");
        return;
    }

    filters.set(class_name, category)

    $filter = $(
        '<li class="active-filter list-inline-item">' +
            '<button class="btn my-2 my-sm-0" type="submit">' +
                '<i class="fa fa-close"></i>' +
                class_name +
            '</button>' +
        '</li>'
    );
    $active_filters_ul.append($filter);

    if (filters.size == 1) {
        hideAllClasses();
    }

    showClass(class_name);
    $grid.masonry('layout');
}

function deleteFilter() {
    $('.active-filters').on('click', 'button', function(){
        $(this).closest('li').remove();

        filters.delete($(this).text())
        hideClass($(this).text())
        
        if (filters.size == 0) {
            showAllClasses();
        }

        $grid.masonry('layout');
    });
}

function clearFilters() {
    $('.active-filters').empty();
    showAllClasses();
    filters.clear();
    $grid.masonry('layout');
}

function recompileAllFilters() {
    console.log("recompile");
    if (filters.size == 0) {
        console.log("no filters");
        return;
    }
    hideAllClasses();
    
    for (let filter of filters.keys()) {
        console.log(filter);
        showClass(filter)
    }
}

// ------------------------------------ SEARCH FUNCTIONS ------------------------------------ //
function searchName() {
    var input = document.getElementById("species-search");
    var filter = input.value.toUpperCase();
    console.log(filter)
    var grid_item = grid.getElementsByClassName("grid-filters");
    for (var i = 0; i < grid_item.length; i++) {
        var name = grid_item[i].getElementsByTagName("a")[0].innerText;
        if (name.toUpperCase().indexOf(filter) <= -1) {
            grid_item[i].style.display = "none";
        } else {
            grid_item[i].style.display = "block";
        }
    }
    $grid.masonry('layout');
}