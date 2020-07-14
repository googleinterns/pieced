// Get query parameters
var urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('species')) {
    const species = urlParams.get('species');
    document.title += "Pieced - " + species;
    fetchSpeciesData(species);
}
else {
    console.log("Error: no species was passed.");
}

//TODO: capitalize species name, fix general formatting