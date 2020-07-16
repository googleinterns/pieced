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

// createMap();
var map;

google.load('maps', '3', {
    other_params: 'sensor=false'
});

google.setOnLoadCallback(initialize);
        
// Normalizes coords that tiles repeat horizontally
function getNormalizedCoord(coord, zoom) {
    var y = coord.y;
    var x = coord.x;

    // 0 = 1 tile, 1 = 2 tiles, 2 = 4 tiles, 3 = 8 tiles, etc
    var tileRange = 1 << zoom;

    // don't repeat across y-axis (vertically)
    if (y < 0 || y >= tileRange) {
        return null;
    }

    // repeat across x-axis
    if (x < 0 || x >= tileRange) {
        x = (x % tileRange + tileRange) % tileRange;
    }

    return {
        x: x,
        y: y
    };
}
                
function GbifMapType(tileSize) {
    this.tileSize = tileSize;
}

GbifMapType.prototype.getTile = function(coord, zoom, ownerDocument) {	
    var div = ownerDocument.createElement('div');
    
    var normalizedCoord = getNormalizedCoord(coord, zoom);
        if (!normalizedCoord) {
            return null;
        }  
    
    // http://www.gbif.org/developer/maps
    
    console.log(normalizedCoord.z, normalizedCoord.x, normalizedCoord.y);
    
    // var url = 'https://api.gbif.org/v2/map/occurrence/density/' + normalizedCoord.z + '/' + normalizedCoord.x + '/' + normalizedCoord.y + '@1x.png?style=purpleYellow.point'
    var url = 'http://api.gbif.org/v0.9/map/density/tile?x=' + normalizedCoord.x + '&y=' + normalizedCoord.y + '&z=' + zoom + '&type=TAXON&key=1';
    
    url += '&resolution=4'; // 4 pixels

    // colours
    // url += '&colors=' + encodeURIComponent(',100,#FFFF0B66|100,1000,#FC4E0766|1000,10000,#FC180833|10000,,#BD000466');    
    
    console.log(url);

    div.innerHTML = '<img src="' + url + '"/>';
    div.style.width = this.tileSize.width + 'px';
    div.style.height = this.tileSize.height + 'px';

    return div;
};      

function initialize() {

    var center = new google.maps.LatLng(0,0);
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 3,
        center: center,
        mapTypeId: google.maps.MapTypeId.TERRAIN
    });

    // Insert this overlay map type as the first overlay map type at
    // position 0. Note that all overlay map types appear on top of
    // their parent base map.
    map.overlayMapTypes.insertAt(
        0, new GbifMapType(new google.maps.Size(256, 256)));
}

/* http://stackoverflow.com/questions/6762564/setting-div-width-according-to-the-screen-size-of-user */
$(window).resize(function() { 
    var windowHeight = $(window).height();
    $('#map').css({'height':windowHeight });
});	