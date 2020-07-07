// Pixelate function: (C) Ken Fyrstenberg, Epistemex, License: CC3.0-attr
var ctx = canvas.getContext('2d');
var animated = false;

// Turn off image smoothing for pixelation effect
ctx.mozImageSmoothingEnabled = false;
ctx.webkitImageSmoothingEnabled = false;
ctx.imageSmoothingEnabled = false;

var image = document.getElementById('species-image');

var originalNumPixels = image.width * image.height;
// var originalNumPixels = 100;
var PIXEL_FACTOR_OLD = 100;
var PIXEL_FACTOR_CURR = 100;
pixel_factor.addEventListener('change', animate_update, false);

// wait until image has finished loading before attaching pixelate()
image.onload = pixelate;

/** Main Pixelation function
 * 
 * Pixelate image to desired number of pixels.
 * Shrinks and grows image w/o smoothing for automatic pixelation.
 */
function pixelate(v) {
    // v was originally used to represent the percentage to scale dimension down to;
    // w = # pixels wide, h = # pixels tall

    // var size = v * 0.01;
    // w = canvas.width * size,
    // h = canvas.height * size;

    // Grab number of total pixels the image should have
    var numPixels = animated? v : pixel_factor.value;

    // Rough creation of pixels that takes square root of numPixels
    // Assumption that image is "square"; could be updated w. math for rectangular images
    var w = Math.sqrt(numPixels);
    var h = Math.sqrt(numPixels);

    // Draw scaled-down image
    ctx.drawImage(image, 0, 0, w, h);

    // Scale image back up to full canvas size; automatic pixelation because image smoothing is off
    ctx.drawImage(canvas,
                    0, 0, w, h,
                    0, 0, canvas.width, canvas.height);
}

/** Function to animate transition between two pixelation values;
 * Changes number of pixels until we hit `endpoint` pixels.
 */
function animate_update(endpoint) {
    // target = desired endpoint for pixelation
    var target = pixel_factor.value;
    // dx = speed of pixelation (# pixels/tick)
    var dx = 10;
    animated = true;
    var underTarget = false;
    doAnimation();

    function doAnimation() {

        if (PIXEL_FACTOR_CURR < target) {
            PIXEL_FACTOR_CURR += dx;
            // "Binary Search" appraoch to hone in on exact value since dx > 1
            if (!underTarget) {
                dx -= 1;
            }
            underTarget = true;
        } else if (PIXEL_FACTOR_CURR > target) {
            PIXEL_FACTOR_CURR -= dx;
            // "Binary Search" appraoch to hone in on exact value since dx > 1
            if (underTarget) {
                dx -= 1;
            }
            underTarget = false;
        } else {
            animated = false;
            return;
        }

        pixelate(PIXEL_FACTOR_CURR);

        if (animated) {
            requestAnimationFrame(doAnimation);
        }
    }
}

/** When mouse enters canvas, animate transition to original, full-resolution image.
 *
 * Zoom out to original, full-resolution image.
 */
 function animateFullResolution() {
    // TODO: Change speed parameters to be a function of the original resolution.
    // TODO: Ease functions?

    // "speed" parameter. Must be different than parameter for other animation fn
    // var dx = (originalNumPixels - PIXEL_FACTOR_CURR) / 100;
    var dx = 2000;
    animated = true;
    doAnimation();

    function doAnimation() {
        PIXEL_FACTOR_CURR += dx;

        if (PIXEL_FACTOR_CURR >= originalNumPixels) {
            animated = false;
            return;
        }

        pixelate(PIXEL_FACTOR_CURR);
        
        if (animated) {
            requestAnimationFrame(doAnimation);
        }
    }
}

/** After mouse leaves canvas, animate transition to previous pixelation amount
 *
 * Zoom back in to old number of pixels.
 */
function animateOldResolution() {
    // "speed" parameter. Must be different than parameter for other animation fn
    // var dx = (PIXEL_FACTOR_CURR - PIXEL_FACTOR_OLD) / 50; 
    var dx = 4000;

    animated = true;
    doAnimation();

    function doAnimation() {
        PIXEL_FACTOR_CURR -= dx;

        if (PIXEL_FACTOR_CURR <= PIXEL_FACTOR_OLD) {
            animated = false;
            return;
        }

        pixelate(PIXEL_FACTOR_CURR);

        if (animated) {
            requestAnimationFrame(doAnimation);
        }
    }
}

// Stand-in code for older browsers that don't support the animation
window.requestAnimationFrame = (function () {
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function (callback) {
        window.setTimeout(callback, 1000 / 60);
    };
})();

// Test function that fetches sample JSON and modifies page to display information for one species
function fetchSpeciesData(name) {
    // const URL = '/data?species=' + name;
    // fetch(URL).then(response => response.json()).then(speciesData => {
    fetch("test.json").then(response => response.json()).then(speciesData => {
        // console.log(speciesData);
        // console.log(name);

        // Parse name (remove upon integration w. gallery page and backend)
        // Defaults to Impala; Impala looks weird due to image resolution
        name = name === undefined ? "Aepyceros melampus" : name;
        var species = speciesData[name];

        var commonNameContainer =       document.getElementById('common-name-container');
        var scientificNameContainer =   document.getElementById('scientific-name-container');
        var statusContainer =           document.getElementById('status-container');
        var descriptionContainer =      document.getElementById('description-container');
        var citationsContainer =        document.getElementById('citations-container');
        var img =                       document.getElementById('species-image');
        var pixelSlider =               document.getElementById('pixel_factor');

        // Update names for species
        commonNameContainer.innerText =     species.commonName;
        scientificNameContainer.innerText = species.binomialName;

        // Map conservation status code to term and update entry
        var statusCode =    species.status;
        statusCode =        statusCode.substr(0, statusCode.indexOf('['));
        var statusMap =     {
                                "EX" : "Extinct",
                                "EW" : "Extinct in the Wild",
                                "CR" : "Critically Endangered",
                                "EN" : "Endangered",
                                "VU" : "Vulnerable",
                                "NT" : "Near Threatened",
                                "LC" : "Least Concern",
                                "DD" : "Data Deficient",
                                "DO" : "Domesticated",                      // Might be "D" in JSON
                                "NE" : "Not Evaluated"
                            };
        statusContainer.innerText = statusMap[statusCode] === undefined ? "unknown" : statusCode + ": " + statusMap[statusCode];

        // Update description entry
        var notes = species.wikipediaNotes;
        notes = notes.substr(0, notes.indexOf('['));
        descriptionContainer.innerText = notes.length == 0 ? "N/A" : notes;
        citationsContainer.innerText = species.citationLinks;

        // Update image source
        img.src = species.imageLink;

        // Manipulate pixelation value based on species population
        var pop = species.population;
        pop = pop.substr(0, pop.indexOf('[')); 
        pixelSlider.value = pop;
        pixelSlider.max = img.width * img.height;
    });
}
