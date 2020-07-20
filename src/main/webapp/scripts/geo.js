// Loads Google Maps API.
function createMap() {
    const map = new google.maps.Map(document.getElementById('map-section'), {
        center: {lat: 37.579869, lng: -122.121974},
        zoom: 10,
    });

  const testMarker = new google.maps.Marker({
      position: {lat: 25.034429, lng: 121.546511},
      map: map,
      title: 'Berkeley, CA'
    });
}

createMap();
