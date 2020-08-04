// Masonry is released under the MIT license: https://desandro.mit-license.org/

// global variables
var filters = new Map()
var $active_filters_ul = $('.active-filters');
var $grid = $('#grid')
var DIV = "div";

// Call these functions when page loads
$(document).ready(function() {
    // Initialize Masonry
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

    // lets users search for a species
    searchName();
});

// Helper function to check for errors in fetch() calls
function handleErrors(response) {
    console.log(response);
    if (!response.ok) {
        throw Error(response.statusText);
    }
    return response.json();
}

/** 
 * Fetches sample JSON and appends each species to the gallery
 * @param sortBy: the parameter to sort by
 */
function fetchAllSpeciesData(sortBy) {
    const parameters = {'sortBy': sortBy};
    const url = createQueryString("/allData", parameters);
    clearGallery();
    
    fetch(url).then(response => handleErrors(response)).then(speciesData => {
    // fetch(url).then(response => response.json()).then(speciesData => {
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

        applyAllFilters();
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

/**
 * Reset all the animals that have been loaded into the grid
 */
function clearGallery() {
    $grid.empty();
    $sizer = $('<div class="grid-sizer"></div>');
    $grid.append($sizer);
}

// ------------------------------------------- FILTER FUNCTIONS ------------------------------------------- //

/**
 * Applies all the filters stored in the map. It's divided into 3 categories: status, trend, and taxon
 * It ORs filters in the same category and ANDs filters in different categories
 * For example)
 *      If Mammals, Birds, and Endangered are the selected filters,
 *      it will show Endangered Mammals and Endangered Birds
 */
function applyAllFilters() {
    hideAllClasses();
    
    // initialize selectors with "div" so they show all divs by default
    // if no filters, "div" means all classes show anyways
    var status_selector = DIV;
    var trend_selector = DIV;
    var taxon_selector = DIV;

    for (let [key, value] of filters) {
        if (value === "status") {
            status_selector = createSelectorString(status_selector, key);
        } else if (value === "trend") {
            trend_selector = createSelectorString(trend_selector, key);
        } else if (value === "taxon") {
            taxon_selector = createSelectorString(taxon_selector, key);
        } 
    }

    $(status_selector).filter(trend_selector).filter(taxon_selector).show();
    $grid.masonry('layout');
}

/**
 * Hides all the elements in the grid
 */
function hideAllClasses() {
    $('.grid-filters').hide();
}

/**
 * Appends class_name to selector to create a filter string.
 * Example final format: "div.CR, div.EN, div.VU"
 * @param selector: string containing the filters that have already been set, initialized with DIV
 * @param class_name: the new filter to append
 */
function createSelectorString(selector, class_name) {
    if (selector.length > DIV.length) { // if selector already has a filter
        return selector.concat(", " + DIV + "." + class_name);
    } else {
        return selector.concat("." + class_name);
    }
}

/**
 * Adds up to 5 filters to the filters map and applies them
 * @param class_name: the actual filter class, eg) "CR"
 * @param category: the category of the filter to determine OR or AND, eg) "status"
 */
function addFilter(class_name, category) {
    if (filters.has(class_name)) {
        return;
    }

    if (filters.size >= 5) {
        alert("Only 5 filters are allowed. Please remove a filter.");
        return;
    }

    filters.set(class_name, category)
    addFilterToListUI(class_name);
    applyAllFilters();
}

/**
 * Deletes the filter from the UI List and the filters map
 * Runs automatically when the list item is clicked
 */
function deleteFilter() {
    $('.active-filters').on('click', 'button', function(){
        $(this).closest('li').remove();

        filters.delete($(this).text())
        applyAllFilters();
    });
}

/**
 * Clears all filters in the filters map
 */
function clearFilters() {
    $('.active-filters').empty();
    filters.clear();
    applyAllFilters();
}

/**
 * Appends a UI list item to the active_filters_ul
 * @param class_name: the filter that has just been added
 */
function addFilterToListUI(class_name) {
    $filter = $(
        '<li class="active-filter list-inline-item">' +
            '<button class="btn my-2 my-sm-0" type="submit">' +
                '<i class="fa fa-close"></i>' +
                class_name +
            '</button>' +
        '</li>'
    );
    $active_filters_ul.append($filter);
}

// -------------------------------------------- SEARCH FUNCTIONS -------------------------------------------- //
/**
 * Filters the gallery as the user types letter by letter
 */
function searchName() {
    $("#species-search").on('input', function() { 
        var input = document.getElementById("species-search");
        var filter = input.value.toUpperCase();
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
    });
}