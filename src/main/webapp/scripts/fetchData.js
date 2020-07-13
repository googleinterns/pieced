// Test function that fetches sample JSON and modifies page to display information for one species
function fetchSpeciesData(name) {
    const URL = '/data?species=' + name;
    fetch(URL).then(response => response.json()).then(speciesData => {

        // Parse name (remove upon integration w. gallery page and backend)
        // Defaults to Impala; Impala looks weird due to image resolution

        name = name === undefined ? "Aepyceros melampus" : name;
        var species = speciesData[name];

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
        statusCode      = statusCode.substr(0, statusCode.indexOf('['));
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
        notes = notes.substr(0, notes.indexOf('['));
        descriptionContainer.innerText = notes.length == 0 ? "N/A" : notes;
        citationsContainer.innerText = speciesData.citationLinks;

        // Update image source
        img.src = speciesData.imageLink;

        // Manipulate pixelation value based on species population
        var pop = speciesData.population;
        pop = pop.substr(0, pop.indexOf('[')); 
        pixelSlider.value = pop;
        pixelSlider.max = img.width * img.height;
    });
}
