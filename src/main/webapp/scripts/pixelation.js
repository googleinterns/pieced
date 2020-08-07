// Pixelate function: original code (C) Ken Fyrstenberg, Epistemex, License: CC3.0-attr

var ctx = canvas.getContext('2d');
var animated = false;

// Turn off image smoothing for pixelation effect
ctx.mozImageSmoothingEnabled = false;
ctx.webkitImageSmoothingEnabled = false;
ctx.imageSmoothingEnabled = false;

var image = document.getElementById('species-image');
var slider = document.getElementById('pixel_factor');
var reset = document.getElementById('reset-population');

// Get number of pixels for native image at full resolution
var originalNumPixels = image.width * image.height;

// Grab initial slider value (equal to species population)
// Must be parsed as numerical values to avoid bug where the values are treated as Strings in some places, but ints in others
var population = parseInt(slider.value);
var PIXEL_FACTOR_OLD = parseInt(slider.value);
var PIXEL_FACTOR_CURR = parseInt(slider.value);

// Add trigger to animate whenever the slider value changes
pixel_factor.addEventListener('change', animate_update, false);
reset.addEventListener('click', resetWrapper, false);

// wait until image has finished loading before attaching pixelate()
image.onload = pixelate;

// reset values after data has been fetched from fetchData.js
function pixelSetup()  {
    // console.log(PIXEL_FACTOR_CURR + " " + PIXEL_FACTOR_OLD + " " + originalNumPixels);
    originalNumPixels = image.width * image.height;
    population = parseInt(slider.value);
    PIXEL_FACTOR_OLD = parseInt(slider.value);
    PIXEL_FACTOR_CURR = parseInt(slider.value);
    // console.log(PIXEL_FACTOR_CURR + " " + PIXEL_FACTOR_OLD + " " + originalNumPixels);
}

/** Main Pixelation function
 * 
 * Pixelate image to desired number of pixels.
 * Accomplished by shrinking and enlarging the image
 * w/o image smoothing to create rough edges that automatically
 * simulate a pixelation effect.
 */
function pixelate(v) {
    // numPixelsWidth = # pixels wide, numPixelsHeight = # pixels tall

    // Grab number of total pixels the image should have
    var totalPixels = animated? v : pixel_factor.value;
    
    var numPixelsWidth = Math.sqrt(totalPixels);
    var numPixelsHeight = Math.sqrt(totalPixels);

    // STRETCH: Add condition for when something is to be considered rectangular
    // Generate ratio > 1 if image is sufficiently non-square
    // var size = (canvas.width == canvas.height) ? ("square") : (canvas.width > canvas.height ? "wide" : "tall");
    // if (size == "wide") {
    //     ratio = canvas.width / canvas.height;   // wide image
    // } else if (size == "tall") {
    //     ratio = canvas.height / canvas.width;   // tall image
    // }

    // if (size == "wide") {        
    //     numPixelsWidth = Math.max(a, b);
    //     numPixelsHeight = Math.min(a, b);
    // } else if (size == "tall") {
    //     numPixelsWidth = Math.min(a, b);
    //     numPixelsHeight = Math.max(a, b);
    // }

    // Draw scaled-down image
    ctx.drawImage(image, 0, 0, numPixelsWidth, numPixelsHeight);

    // Scale image back up to full canvas size; automatic pixelation because image smoothing is off
    ctx.drawImage(canvas,
            0, 0, numPixelsWidth, numPixelsHeight,
            0, 0, canvas.width, canvas.height);
}

/** Function to animate transition between two pixelation values;
 * Changes number of pixels until we hit `target` pixels.
 */
function animate_update() {
    // target = desired endpoint for pixelation is taken from current slider value
    var target = parseInt(slider.value);
    
    // dx = speed of pixelation (# pixels/tick)
    // var dx = 10;
    var dx = Math.ceil(Math.abs(PIXEL_FACTOR_CURR - target) / 50);
    animated = true;
    var underTarget = false;
    doAnimation();

    function doAnimation() {
        if (PIXEL_FACTOR_CURR < target) {
            PIXEL_FACTOR_CURR += dx;
            // "Binary Search" approach to home in on exact value since dx > 1
            if (!underTarget) {
                // dx -= 1;
                dx = Math.ceil(dx/2);
            }
            underTarget = true;
        } else if (PIXEL_FACTOR_CURR > target) {
            PIXEL_FACTOR_CURR -= dx;
            // "Binary Search" approach to home in on exact value since dx > 1
            if (underTarget) {
                // dx -= 1;
                dx = Math.ceil(dx/2);
            }
            underTarget = false;
        } else {
            animated = false;
            PIXEL_FACTOR_OLD = PIXEL_FACTOR_CURR;
            return;
        }

        // Run pixelate function to render `PIXEL_FACTOR_CURR` pixels
        pixelate(PIXEL_FACTOR_CURR);

        // Actually update the canvas we see with the new animation
        if (animated) {
            requestAnimationFrame(doAnimation);
        }
    }
}

/*
 * When mouse enters canvas containing image, this activates
 * an animation that gradually "un-pixelates" the image until it
 * reaches the original, full-resolution image, creating a
 * "Zoom out" effect.
 */
 function animateToFullResolution() {
    // range between start of animation (PIXEL_FACTOR_CURR) and end of animation (originalNumPixels)
    var range = originalNumPixels - PIXEL_FACTOR_CURR;
    var animationCompletionPercentage = 0;
    // console.log(PIXEL_FACTOR_OLD, PIXEL_FACTOR_CURR, range);

    animated = true;
    doAnimation();

    function doAnimation() {
        // Calculate ease function value
        var easeOutput = calculateEaseValue(animationCompletionPercentage);

        /** Use ease value to calculate proportional increase in # of pixels
         *
         * If easeOutput = 0, we just started the animation: `originalNumPixels - range * (1 - 0) = originalNumPixels - (originalNumPixels - PIXEL_FACTOR_CURR) = PIXEL_FACTOR_CURR`
         *
         * If easeOutput = 1, we just finished the animation:
         * `originalNumPixels - range * (1 - 1) = originalNumPixels` */
        PIXEL_FACTOR_CURR = Math.round(originalNumPixels - range * (1 - easeOutput));

        // If we've hit 100% completion of the animation (i.e. reached full resolution), stop
        if (animationCompletionPercentage >= 100) {
            animated = false;
            // console.log("Reached original resolution: " + PIXEL_FACTOR_OLD + " " + PIXEL_FACTOR_CURR + " " + originalNumPixels + " " + range);
            return;
        }

        // Run pixelate function to render `PIXEL_FACTOR_CURR` pixels
        pixelate(PIXEL_FACTOR_CURR);

        // Actually update the canvas we see with the new animation
        if (animated) {
            requestAnimationFrame(doAnimation);
        }

        // Increment animation completion percentage by 2% (larger value = animates faster)
        animationCompletionPercentage += 2;
    }
}

/*
 * When mouse leaves canvas containing image, this activates
 * an animation that gradually "re-pixelates" the image until it
 * reaches the previous pixelation level, leaving the image looking
 * as it did when the mouse first entered the canvas.
 */
function animateToOldResolution() {
    // range between start of animation (PIXEL_FACTOR_CURR) and end of animation (PIXEL_FACTOR_OLD)
    var range = PIXEL_FACTOR_CURR - PIXEL_FACTOR_OLD;
    var animationCompletionPercentage = 0;
    // console.log(PIXEL_FACTOR_OLD, PIXEL_FACTOR_CURR, range);
    animated = true;
    doAnimation();

    function doAnimation() {
        // Calculate ease function value
        var easeOutput = calculateEaseValue(animationCompletionPercentage);

        /** Use ease value to calculate proportional increase in # of pixels
         *
         * If easeOutput = 0, we just started the animation: `PIXEL_FACTOR_OLD + range * (1 - 0) = PIXEL_FACTOR_OLD + (PIXEL_FACTOR_CURR - PIXEL_FACTOR_OLD) = PIXEL_FACTOR_CURR`
         *
         * If easeOutput = 1, we just finished the animation: `PIXEL_FACTOR_OLD + range * (1 - 1) = PIXEL_FACTOR_OLD` */
        PIXEL_FACTOR_CURR = Math.round(PIXEL_FACTOR_OLD + range * (1 - easeOutput));
        
        // Run pixelate function to render `PIXEL_FACTOR_CURR` pixels
        pixelate(PIXEL_FACTOR_CURR);

        // If we've hit 100% completion of the animation (i.e. reached old resolution), stop
        if (animationCompletionPercentage >= 100) {
            animated = false;
            // console.log("Reached old resolution: " + PIXEL_FACTOR_OLD + " " + PIXEL_FACTOR_CURR + " " + originalNumPixels + " " + range);
            return;
        }

        // Actually update the canvas we see with the new animation
        if (animated) {
            requestAnimationFrame(doAnimation);
        }

        // Increment animation completion percentage by 2% (larger value = animates faster)
        animationCompletionPercentage += 2;
    }
}

// Map animation completion percentage to pixelation factor using InOutQuart ease function
function calculateEaseValue(x) {
    // Map [0, 100] to [0, 1]
    x /= 100;
    //InOutQuart ease function, GPL-3.0 license, https://github.com/ai/easings.net
    factor = x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
    return factor;
}

// Wrapper function to reset the population to the original value
function resetWrapper() {
    slider.value = Math.min(population, slider.max);
    animate_update();
}


// Stand-in code for older browsers that don't support the animation
window.requestAnimationFrame = (function () {
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function (callback) {
        window.setTimeout(callback, 1000 / 60);
    };
})();
