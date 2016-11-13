//Coordenadas GPS da FCUP: 41.1525188,-8.6406142
function contructMap() {
        var coords = {lat: 41.1525188, lng: -8.6406142};
        var map = new google.maps.Map(document.getElementById('mapa'), {
          zoom: 15,
          center: coords
        });
        
        var marker = new google.maps.Marker({
          position: coords,
          map: map
        });
}