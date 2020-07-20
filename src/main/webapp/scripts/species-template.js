// Get query parameters
var urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('species')) {
    const species = urlParams.get('species');
    var x = capitalizeSpeciesName(species);
    document.title += "Pieced - " + capitalizeSpeciesName(species);
    fetchSpeciesData(species);
}
else {
    console.log("Error: no species was passed.");
}
