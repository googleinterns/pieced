// Fetches JSON for a given species
function fetchSpeciesData(name) {
    const URL = '/speciesData?species=' + name;
    fetch(URL).then(response => handleErrors(response)).then(speciesData => populatePageWithSpeciesData(speciesData));
}

// Helper function to check for errors in fetch() calls
function handleErrors(response) {
    if (!response.ok) {
        document.getElementById('common-name-container').innerText = response.status + " " + response.statusText;
        throw Error(response.status + " " + response.statusText);
    }
    return response.json();
}

// Helper function that takes the JSON and modifies the species page to display the contained information
function populatePageWithSpeciesData(speciesData) {
    // var canvas                  = document.getElementById('canvas');
    var commonNameContainer     = document.getElementById('common-name-container');
    var scientificNameContainer = document.getElementById('scientific-name-container');
    var populationContainer     = document.getElementById('population-container');
    var statusContainer         = document.getElementById('status-container');
    var descriptionContainer    = document.getElementById('description-container');
    var img                     = document.getElementById('species-image');
    var imageCreditContainer    = document.getElementById('image-credit');
    var pixelSlider             = document.getElementById('pixel_factor');
    var taxonomyContainer       = document.getElementById('taxonomy-container');

    commonNameContainer.innerText = speciesData.commonName;
    scientificNameContainer.innerText = speciesData.binomialName;
    populationContainer.innerText = speciesData.population + " left";
    statusContainer.innerText = getSpeciesStatus(speciesData.status);
    descriptionContainer.innerText = speciesData.wikipediaNotes;
    img.src = speciesData.imageLink;

    var citation = `<a href=${speciesData.citationLink}>&copy Wikipedia</a>`;
    imageCreditContainer.innerHTML = citation;

    // Update species taxonomic path
    if (speciesData.taxonomicPath != null) {
        var taxonomyString = `<b>${speciesData.taxonomicPath.kingdom_t} > 
                                 ${speciesData.taxonomicPath.phylum_t} > 
                                 ${speciesData.taxonomicPath.class_t} > 
                                 ${speciesData.taxonomicPath.order_t} > 
                                 ${speciesData.taxonomicPath.family_t} > 
                                 ${speciesData.taxonomicPath.genus_t}</b>`;
        taxonomyContainer.innerHTML = taxonomyString;
    }

    // Manipulate pixelation value based on species population
    pixelSlider.max = img.width * img.height;
    var pop = speciesData.population;
    pixelSlider.value = (pop === undefined) ? pixelSlider.max : Math.min(pop, pixelSlider.max);

    var ctx = canvas.getContext('2d');
    // Turn off image smoothing again, since modifying the canvas attributes reenables smoothing
    ctx.mozImageSmoothingEnabled = false;
    ctx.webkitImageSmoothingEnabled = false;
    ctx.imageSmoothingEnabled = false;

    pixelSetup();
}

// Map conservation status code to term and update entry
function getSpeciesStatus(statusCode) {
    statusCode = (statusCode == null) ? null : statusCode.substr(0, 2);
    var statusMap = {
            "EX" : "Extinct",
            "EW" : "Extinct in the Wild",
            "CR" : "Critically Endangered",
            "EN" : "Endangered",
            "VU" : "Vulnerable",
            "NT" : "Near Threatened",
            "LC" : "Least Concern",
            "DD" : "Data Deficient",
            "DO" : "Domesticated",
            "NE" : "Not Evaluated"
        };
    return (statusMap[statusCode] === undefined) ? "Unknown" : statusMap[statusCode];
}

// Capitalize each word in species' names
function capitalizeSpeciesName(name) {
    wordsArray = name.toLowerCase().split(' ');
    capitalizedArray = wordsArray.map(w => w.substring(0,1).toUpperCase() + w.substring(1));
    capitalizedName = capitalizedArray.join(' ');
    return capitalizedName;
}