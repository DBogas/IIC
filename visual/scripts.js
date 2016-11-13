//Coordenadas GPS da FCUP: 41.1525188,-8.6406142
	//longitude: -8.653679 ,
	//latitude: 41.254143
function contructMap() {
        var coords = {lat:  41.254143, lng: -8.653679};
        var map = new google.maps.Map(document.getElementById('mapa'), {
          zoom: 15,
          center: coords
        });
        
        var marker = new google.maps.Marker({
          position: coords,
          map: map
        });
}

function makeOptions() {
    var i;
    var target_select = document.getElementById("drop_stops");
    for (i=0; i<allstops.length; i++){
        var opt = document.createElement("option");
        opt.innerHTML = allstops[i];
        target_select.appendChild(opt);
    }
}



