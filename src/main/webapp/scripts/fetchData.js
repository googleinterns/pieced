// Test function that fetches sample JSON and modifies page to display information for one species
function fetchSpeciesData(name) {
    const URL = '/speciesData?species=' + name;
    fetch(URL).then(response => response.json()).then(speciesData => {
        // var canvas                  = document.getElementById('canvas');
        var commonNameContainer     = document.getElementById('common-name-container');
        var scientificNameContainer = document.getElementById('scientific-name-container');
        var statusContainer         = document.getElementById('status-container');
        var descriptionContainer    = document.getElementById('description-container');
        var citationsContainer      = document.getElementById('citations-container');
        var img                     = document.getElementById('species-image');
        var pixelSlider             = document.getElementById('pixel_factor');
        var kingdomContainer        = document.getElementById('kingdom-container');
        var phylumContainer         = document.getElementById('phylum-container');
        var classContainer          = document.getElementById('class-container');
        var orderContainer          = document.getElementById('order-container');
        var familyContainer         = document.getElementById('family-container');
        var genusContainer          = document.getElementById('genus-container');

        // Update names for species
        commonNameContainer.innerText       = speciesData.commonName;
        scientificNameContainer.innerText   = speciesData.binomialName;

        // Map conservation status code to term and update entry
        var statusCode = speciesData.status;
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
        statusContainer.innerText = (statusMap[statusCode] === undefined) ? "unknown" : statusCode + ": " + statusMap[statusCode];

        // Update description entry
        var notes = speciesData.wikipediaNotes;
        switch(notes) {
            case null:
                descriptionContainer.innerText = "N/A";
                break;
            case "":
                descriptionContainer.innerText = "N/A";
                break;
            default:
                descriptionContainer.innerText = notes;
        }
        citationsContainer.innerText = speciesData.citationLink;

        // Update image source
        img.src = speciesData.imageLink;
        // canvas.width = img.width = Math.round(img.naturalWidth/16)*16;
        // canvas.height = img.height = Math.round(img.naturalHeight/16)*16;

        // canvas.width = img.width = img.naturalWidth;
        // canvas.height = img.height = img.naturalHeight;

        // Manipulate pixelation value based on species population
        pixelSlider.max = img.width * img.height;
        var pop = speciesData.population;
        // console.log("img.width * img.height = " + pixelSlider.max + " population = " + pop);
        // console.log("natural width * height: " + img.naturalWidth * img.naturalHeight);
        pixelSlider.value = (pop === undefined) ? pixelSlider.max : Math.min(pop, pixelSlider.max);

        var ctx = canvas.getContext('2d');
        // Turn off image smoothing again, since modifying the canvas attributes reenables smoothing
        ctx.mozImageSmoothingEnabled = false;
        ctx.webkitImageSmoothingEnabled = false;
        ctx.imageSmoothingEnabled = false;

        // Update species taxonomic path
        if (speciesData.taxonomicPath != null) {
            kingdomContainer.innerText      = speciesData.taxonomicPath.kingdom_t;
            phylumContainer.innerText       = speciesData.taxonomicPath.phylum_t;
            classContainer.innerText        = speciesData.taxonomicPath.class_t;
            orderContainer.innerText        = speciesData.taxonomicPath.order_t;
            familyContainer.innerText       = speciesData.taxonomicPath.family_t;
            genusContainer.innerText        = speciesData.taxonomicPath.genus_t;
        }

        pixelSetup();
    });
}

// Capitalize each word in species' names
function capitalizeSpeciesName(name) {
    wordsArray = name.toLowerCase().split(' ');
    capitalizedArray = wordsArray.map(w => w.substring(0,1).toUpperCase() + w.substring(1));
    capitalizedName = capitalizedArray.join(' ');
    return capitalizedName;
}
