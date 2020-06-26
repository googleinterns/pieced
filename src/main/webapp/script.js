// Pixelate function: (C) Ken Fyrstenberg, Epistemex, License: CC3.0-attr
var ctx = canvas.getContext('2d');
var animated = false;

// turn off image smoothing for pixelated effect
ctx.mozImageSmoothingEnabled = false;
ctx.webkitImageSmoothingEnabled = false;
ctx.imageSmoothingEnabled = false;

var img = document.getElementById('species-image');

// var originalNumPixels = img.width * img.height;
var originalNumPixels = 100;

// wait until image is actually available
img.onload = pixelate;

// Pixelation function
// Shrinks image and then scales it back up w/o smoothing for automatic pixelation
// v = 100 is original image quality
function pixelate(v) {

        // Use input value; v = percentage to scale dimensions down to
    // var size = v * 0.01;
        // cache scaled width and height
        // w = # pixels wide
        // h = # pixels tall
    // w = canvas.width * size,
    // h = canvas.height * size;

    var lambda = animated? v : pixel_factor.value;

    var w = Math.sqrt(lambda);
    var h = Math.sqrt(lambda);

    console.log(lambda, w, h);

    // draw original image to the scaled size
    ctx.drawImage(img, 0, 0, w, h);

    // then draw that scaled image thumb back to fill canvas
    // As smoothing is off the result will be pixelated
    ctx.drawImage(canvas, 0, 0, w, h, 0, 0, canvas.width, canvas.height);
}

var PIXEL_FACTOR_CURR = originalNumPixels;

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

// Restore pixelation.
function out() {
    var v = originalNumPixels;
    var dx = 10; // "speed" parameter
    console.log("max pixelation: v starts at " + v);

    animated = true;
    anim();

    function anim() {
        v -= dx;

        if (v < PIXEL_FACTOR_CURR) {
            animated = false;
            return;
        }

        pixelate(v);

        if (animated) {
            requestAnimationFrame(anim);
        }
    }
}

// Full resolution
function over() {
    var v = PIXEL_FACTOR_CURR;
    var dx = 10; // "speed" parameter
    console.log("go to original: v starts at " + v);

    animated = true;
    anim();

    function anim() {
        v += dx;

        if (v > originalNumPixels) {
            animated = false;
            return;
        }

        pixelate(v);
        
        if (animated) {
            requestAnimationFrame(anim);
        }
    }
}

pixel_factor.addEventListener('change', animate_update, false);

// poly-fill for requestAnmationFrame with fallback for older
// browsers which do not support rAF.
// window.requestAnimationFrame = (function () {
//     return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function (callback) {
//         window.setTimeout(callback, 1000 / 60);
//     };
// })();