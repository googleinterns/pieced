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
        commonNameContainer.innerText       = capitalizeSpeciesName(speciesData.commonName);
        scientificNameContainer.innerText   = capitalizeSpeciesName(speciesData.binomialName);

        // Map conservation status code to term and update entry
        var statusCode  = speciesData.status;
        statusCode      = (statusCode == null) ? null : statusCode.substr(0, 2);
        var statusMap   = {
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
                if (notes.indexOf('[') > 0) {
                    descriptionContainer.innerText = notes.substr(0, notes.indexOf('['));
                }
                else {
                    descriptionContainer.innerText = notes;
                }
        }
        citationsContainer.innerText = speciesData.citationLink;

        // Update image source
        img.src = speciesData.imageLink;
        // img.width = Math.round(img.naturalWidth/100)*100;
        // img.height = Math.round(img.naturalHeight/100)*100;
        img.width = img.naturalWidth;
        img.height = img.naturalHeight;

        // Manipulate pixelation value based on species population
        pixelSlider.max = img.width * img.height;
        var pop = speciesData.population;
        pixelSlider.value = (pop === undefined) ? pixelSlider.max : Math.min(pop, pixelSlider.max);

        // Update species taxonomic path
        if (speciesData.taxonomicPath != null) {
            kingdomContainer.innerText      = speciesData.taxonomicPath.kingdom_t;
            phylumContainer.innerText       = speciesData.taxonomicPath.phylum_t;
            classContainer.innerText        = speciesData.taxonomicPath.class_t;
            orderContainer.innerText        = speciesData.taxonomicPath.order_t;
            familyContainer.innerText       = speciesData.taxonomicPath.family_t;
            genusContainer.innerText        = speciesData.taxonomicPath.genus_t;
        }
    });
}

// Capitalize each word in species' names
function capitalizeSpeciesName(name) {
    wordsArray = name.toLowerCase().split(' ');
    capitalizedArray = wordsArray.map(w => w.substring(0,1).toUpperCase() + w.substring(1));
    capitalizedName = capitalizedArray.join(' ');
    return capitalizedName;
}
