// Pixelate function: (C) Ken Fyrstenberg, Epistemex, License: CC3.0-attr
var ctx = canvas.getContext('2d');
var animated = false;

// Turn off image smoothing for pixelation effect
ctx.mozImageSmoothingEnabled = false;
ctx.webkitImageSmoothingEnabled = false;
ctx.imageSmoothingEnabled = false;

var image = document.getElementById('species-image');
var slider = document.getElementById('pixel_factor');

// Get number of pixels for native image at full resolution
var originalNumPixels = image.width * image.height;

// Grab initial slider value (equal to species population)
// MUST be parsed as numerical values to avoid bug where the values
// are treated as Strings in some places, but Ints in others
// e.g. (100 - 30 = 70, but 100 + 30 = 10030)
var PIXEL_FACTOR_OLD = parseInt(slider.value);
var PIXEL_FACTOR_CURR = parseInt(slider.value);

// Add trigger to animate whenever the slider value changes
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
    // numPixelsWidth = # pixels wide, numPixelsHeight = # pixels tall

    // var size = v * 0.01;
    // numPixelsWidth = canvas.width * size,
    // numPixelsHeight = canvas.height * size;

    // Grab number of total pixels the image should have
    var totalPixels = animated? v : pixel_factor.value;
    
    // TODO: Add condition for when something is to be considered rectangular
    // Generate ratio > 1 if image is sufficiently non-square
    var size = (canvas.width == canvas.height) ? ("square") : (canvas.width > canvas.height ? "wide" : "tall");
    if (size == "wide") {
        ratio = canvas.width / canvas.height;   // wide image
    } else if (size == "tall") {
        ratio = canvas.height / canvas.width;   // tall image
    }

    var [a, b, c] = getFactors(totalPixels);
    // console.log(a, b, c);

    var numPixelsWidth = Math.sqrt(totalPixels);
    var numPixelsHeight = Math.sqrt(totalPixels);

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

// Find factors
// function getFactors(n) {
//     square_root = Math.ceil(Math.sqrt(n));
//     foundSolution = false;
//     val = square_root;
//     while (!foundSolution) {
//         val2 = Math.floor(n/val);
//         if (val2 * val == n) {
//             foundSolution = true;
//         } else {
//             val -= 1;
//         }
//     }
//     return [val, val2, n];
// }

/** Function to animate transition between two pixelation values;
 * Changes number of pixels until we hit `endpoint` pixels.
 */
function animate_update(endpoint) {
    // target = desired endpoint for pixelation
    var target = pixel_factor.value;
    // dx = speed of pixelation (# pixels/tick)
    var dx = 10;
    // var dx = Math.abs(PIXEL_FACTOR_CURR - target) / 10;
    animated = true;
    var underTarget = false;
    doAnimation();

    function doAnimation() {

        if (PIXEL_FACTOR_CURR < target) {
            PIXEL_FACTOR_CURR += dx;
            // "Binary Search" approach to home in on exact value since dx > 1
            if (!underTarget) {
                dx -= 1;
            }
            underTarget = true;
        } else if (PIXEL_FACTOR_CURR > target) {
            PIXEL_FACTOR_CURR -= dx;
            // "Binary Search" approach to home in on exact value since dx > 1
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
 function animateToFullResolution() {
    console.log("START");
    // TODO: Change speed parameters to be a function of the original resolution.
    // TODO: Ease functions?
    var range = originalNumPixels - PIXEL_FACTOR_CURR;
    var animationCompletionPercentage = 0;
    console.log(PIXEL_FACTOR_OLD, PIXEL_FACTOR_CURR, range);
    // "speed" parameter. Must be different than parameter for other animation fn
    // var dx = (originalNumPixels - PIXEL_FACTOR_CURR) / 100;
    // PIXEL_FACTOR_OLD = PIXEL_FACTOR_CURR;
    // var dx = 2000;
    animated = true;
    doAnimation();

    function doAnimation() {
        // PIXEL_FACTOR_CURR += dx;
        var easeOutput = calculateEaseValue(animationCompletionPercentage);
        PIXEL_FACTOR_CURR = Math.round(originalNumPixels - range * (1 - easeOutput));


        if (PIXEL_FACTOR_CURR >= originalNumPixels) {
            animated = false;
            console.log("HIT MAX");
            console.log(originalNumPixels);
            console.log(PIXEL_FACTOR_OLD, PIXEL_FACTOR_CURR, range);
            return;
        }

        pixelate(PIXEL_FACTOR_CURR);

        if (animated) {
            requestAnimationFrame(doAnimation);
        }

        animationCompletionPercentage += 2;
    }
}

/** After mouse leaves canvas, animate transition to previous pixelation amount
 *
 * Zoom back in to old number of pixels.
 */
function animateToOldResolution() {
    console.log("START");
    // "speed" parameter. Must be different than parameter for other animation fn
    // var dx = (PIXEL_FACTOR_CURR - PIXEL_FACTOR_OLD) / 50; 
    // var dx = 4000;
    var range = PIXEL_FACTOR_CURR - PIXEL_FACTOR_OLD;
    var animationCompletionPercentage = 0;
    console.log(PIXEL_FACTOR_OLD, PIXEL_FACTOR_CURR, range);
    animated = true;
    doAnimation();

    function doAnimation() {
        var easeOutput = calculateEaseValue(animationCompletionPercentage);
        console.log("PIXEL_FACTOR_CURR OLD: " + PIXEL_FACTOR_CURR);
        console.log("EASE: " + easeOutput);
        console.log("1 - EASE: " + (1 - easeOutput));
        console.log("OFFSET: " + range * (1 - easeOutput));
        console.log("PIXEL_FACTOR_OLD: " + PIXEL_FACTOR_OLD);
        console.log("NEW VAL: " + (PIXEL_FACTOR_OLD + range * ( 1 - easeOutput)) );
        console.log("NEW VAL: " + ((PIXEL_FACTOR_OLD) + (range * ( 1 - easeOutput))) );
        console.log("NEW VAL: " + (parseInt(PIXEL_FACTOR_OLD) + parseInt(range * ( 1 - easeOutput))) );
        PIXEL_FACTOR_CURR = Math.round(PIXEL_FACTOR_OLD + range * (1 - easeOutput));
        console.log("PIXEL_FACTOR_CURR NEW: " + PIXEL_FACTOR_CURR);

        pixelate(PIXEL_FACTOR_CURR);

        if (animationCompletionPercentage >= 100) {
            animated = false;
            console.log("HIT OLD VALUE");
            console.log(PIXEL_FACTOR_OLD, PIXEL_FACTOR_CURR, range);
            return;
        }

        if (animated) {
            requestAnimationFrame(doAnimation);
        }

        animationCompletionPercentage += 2;
    }
}

// Map 3animation completion percentage to pixelation factor
function calculateEaseValue(x) {
    // Map [0, 100] to [0, 1]
    x /= 100;
    //InOutQuart ease function, GPL-3.0 license, https://github.com/ai/easings.net
    factor = x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
    return factor;
    // return x/100;
}

// Stand-in code for older browsers that don't support the animation
window.requestAnimationFrame = (function () {
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function (callback) {
        window.setTimeout(callback, 1000 / 60);
    };
})();
