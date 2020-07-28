function corsProxy(url) {
    return `https://cors-anywhere.herokuapp.com/${url}`;
}

function getColorPalette(url) {
    console.log(url)
    var img = document.createElement('img');
    img.crossOrigin = "Anonymous";
    img.src = corsProxy(url);
    var paletteReady = false;
    
    if (!paletteReady) {
        getPalette(img);
    }
}
// var url = document.getElementById("species-img"); 
// console.log(url)
// var img = document.querySelector('img');
// // img.crossOrigin = "Anonymous";
// // img.src = corsProxy(img.src);

// var list = document.querySelector('ul')
// var paletteReady = false;

// if (!paletteReady) {
//     getPalette();
// }

function getPalette(img) {
    paletteReady = true;
    var vibrant = new Vibrant(img);
    swatches = vibrant.swatches(),
    listFragment = new DocumentFragment();
        
        // for ( var swatch in swatches ) {
        //     if (swatches.hasOwnProperty(swatch) && swatches[swatch]) { 
        //         console.log(swatch, swatches[swatch].getHex());
        //         var li = document.createElement('li'),
        //             p = document.createElement('p'),
        //             small = document.createElement('small');
                
        //         p.textContent = swatches[swatch].getHex();
        //         p.style.color = swatches[swatch].getTitleTextColor();
        //         small.textContent = swatch;
        //         small.style.color = swatches[swatch].getBodyTextColor();
        //         li.style.backgroundColor = swatches[swatch].getHex();
        //         li.appendChild(p);
        //         li.appendChild(small);
        //         listFragment.appendChild(li);
        //     }
        // }
        
        // list.appendChild(listFragment);
        
        // if (swatches['DarkVibrant']) {
        //     section.style.backgroundColor = swatches['DarkVibrant'].getHex();
        // }
    }