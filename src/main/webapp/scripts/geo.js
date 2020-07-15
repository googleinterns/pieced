// Loads Google Maps API.
function createMap() {
    const map = new google.maps.Map(document.getElementById('map-section'), {
        center: {lat: 37.579869, lng: -122.121974},
        zoom: 10,
    });

  const homeMarker = new google.maps.Marker({
      position: {lat: 37.217832, lng: -121.858269},
      map: map,
      title: 'Home'
    });
  
  const freshmanMarker = new google.maps.Marker({
      position: {lat: 37.866276, lng: -122.255204},
      map: map,
      title: 'Freshman Year'
    });
  
  const sophomoreMarker = new google.maps.Marker({
      position: {lat: 37.867764, lng: -122.261153},
      map: map,
      title: 'Sophomore Year'
    });

  const juniorMarker = new google.maps.Marker({
      position: {lat: 37.867454, lng: -122.257261},
      map: map,
      title: 'Junior Year'
    });

  const schoolMarker = new google.maps.Marker({
      position: {lat: 37.871967, lng: -122.258583},
      map: map,
      title: 'School'
    });

  const otherHomeMarker = new google.maps.Marker({
      position: {lat: 25.034429, lng: 121.546511},
      map: map,
      title: 'Home away from Home'
    });
}

createMap();