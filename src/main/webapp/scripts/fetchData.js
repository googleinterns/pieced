// Test function that fetches sample JSON and modifies page to display information for one species
function fetchSpeciesData(name) {
    const URL = '/speciesData?species=' + name;
    fetch(URL).then(response => response.json()).then(speciesData => {
        var commonNameContainer     = document.getElementById('common-name-container');
        var scientificNameContainer = document.getElementById('scientific-name-container');
        var statusContainer         = document.getElementById('status-container');
        var descriptionContainer    = document.getElementById('description-container');
        var citationsContainer      = document.getElementById('citations-container');
        var img                     = document.getElementById('species-image');
        var pixelSlider             = document.getElementById('pixel_factor');

        // Update names for species
        commonNameContainer.innerText       = speciesData.commonName;
        scientificNameContainer.innerText   = speciesData.binomialName;

        // Map conservation status code to term and update entry
        var statusCode  = speciesData.status;
        statusCode      = statusCode == null ? null : statusCode.substr(0, statusCode.indexOf('['));
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
        statusContainer.innerText = statusMap[statusCode] === undefined ? "unknown" : statusCode + ": " + statusMap[statusCode];

        // Update description entry
        var notes = speciesData.wikipediaNotes;
        descriptionContainer.innerText = notes == null ? "N/A" : notes.substr(0, notes.indexOf('['));
        citationsContainer.innerText = speciesData.citationLink;

        // Update image source
        img.src = speciesData.imageLink;

        // Manipulate pixelation value based on species population
        pixelSlider.max = img.width * img.height;

        var pop = speciesData.population;
        pop = pop == null ? pixelSlider.max : pop.substr(0, pop.indexOf('['));
        pixelSlider.value = pop;

    });
}
