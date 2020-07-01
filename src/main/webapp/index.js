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

// wait until image is actually available
image.onload = pixelate;

/** Main Pixelation function
 * 
 * Pixelate image to desired number of pixels.
 * Shrinks and grows image w/o smoothing for automatic pixelation.
 * v = 100 is original image quality.
 */
function pixelate(v) {

        // Use input value; v = percentage to scale dimensions down to
    // var size = v * 0.01;
        // w = # pixels wide
        // h = # pixels tall
    // w = canvas.width * size,
    // h = canvas.height * size;

    var lambda = animated? v : pixel_factor.value;

    var w = Math.sqrt(lambda);
    var h = Math.sqrt(lambda);

    // Draw scaled-down image.
    ctx.drawImage(image, 0, 0, w, h);

    // Scale image back up to full canvas size; automatic pixelation because image smoothing is off.
    ctx.drawImage(canvas,
                    0, 0, w, h,
                    0, 0, canvas.width, canvas.height);
}

/** Function to animate transition between two pixelation values
 *
 * Changes number of pixels until we hit `endpoint` pixels.
 */
function animate_update(endpoint) {
    var target = pixel_factor.value;
    var dx = 10;
    animated = true;
    var underTarget = false;
    doAnimation();

    function doAnimation() {

        if (PIXEL_FACTOR_CURR < target) {
            PIXEL_FACTOR_CURR += dx;
            if (!underTarget) {
                dx -= 1;
            }
            underTarget = true;
        } else if (PIXEL_FACTOR_CURR > target) {
            PIXEL_FACTOR_CURR -= dx;
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

    // var dx = (originalNumPixels - PIXEL_FACTOR_CURR) / 100; // "speed" parameter. Cannot be the same as the paramter for the other animation function,
                                                            // or else they will cancel out and loop forever.
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
    // var dx = (PIXEL_FACTOR_CURR - PIXEL_FACTOR_OLD) / 50; // "speed" parameter. Cannot be the same as the paramter for the other animation function,
                                                          // or else they will cancel out and loop forever.
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

// poly-fill for requestAnmationFrame with fallback for older
// browsers which do not support rAF.
window.requestAnimationFrame = (function () {
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function (callback) {
        window.setTimeout(callback, 1000 / 60);
    };
})();













// Test function that fetches sample JSON and modifies page to display information for one species.
function fetchSpeciesData(name) {
    // const URL = '/data?species=' + name;
    // fetch(URL).then(response => response.json()).then(speciesData => {
    fetch("test.json").then(response => response.json()).then(speciesData => {
        // console.log(speciesData);
        species = speciesData["Pelea capreolus"];

        var commonNameContainer = document.getElementById('common-name-container');
        var scientificNameContainer = document.getElementById('scientific-name-container');
        var statusContainer = document.getElementById('status-container');
        var descriptionContainer = document.getElementById('description-container');
        var citationsContainer = document.getElementById('citations-container');
        var img = document.getElementById('species-image');
        var numPixels = document.getElementById('pixel_factor');

        commonNameContainer.innerText = species.commonName;
        scientificNameContainer.innerText = species.binomialName;

        var statusCode = species.status;
        statusCode = statusCode.substr(0, statusCode.indexOf('['));
        var statusMap = {
                            "EX" : "Extinct",
                            "EW" : "Extinct in the Wild",
                            "CR" : "Critically Endangered",
                            "EN" : "Endangered",
                            "VU" : "Vulnerable",
                            "NT" : "Near Threatened",
                            "LC" : "Least Concern",
                            "DD" : "Data Deficient",
                            "NE" : "Not Evaluated"
                        };
        statusContainer.innerText = statusMap[statusCode] === undefined ? "unknown" : statusCode + ": " + statusMap[statusCode];

        descriptionContainer.innerText = "N/A";
        citationsContainer.innerText = species.citationLinks;

        img.src = species.imageLink;

        var pop = species.population;
        pop = pop.substr(0, pop.indexOf('[')); 
        numPixels.value = pop;
    });
}
