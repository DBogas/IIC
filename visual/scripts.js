
function contructMap() {
        /*
        var e = document.getElementById("ddlViewBy");
        var strUser = e.options[e.selectedIndex].value;*/
    
        var target = document.getElementById("drop_stops");
     
        var selectedStop =  window[target.options[target.selectedIndex].value.trim()]; 
        var lat = selectedStop.latitude;
        var lon = selectedStop.longitude;
        
        
        var zoom = 15;
        var url = "http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+"&zoom="+zoom+"&size=600x300&maptype=roadmap&sensor=false";
        var mapa = document.getElementById("map_img");
        mapa.src = url;
        /*var map = new google.maps.Map(document.getElementById('mapa'), {
          zoom: 15,
          center: coords
        });*/
        
       /* var marker = new google.maps.Marker({
          position: coords,
          map: map
        });*/
}

function makeOptions() {
    var i;
    var target_select = document.getElementById("drop_stops");
    for (i=0; i<allstops.length; i++){
        var opt = document.createElement("option");
        opt.innerHTML  = allstops[i];
        opt.setAttribute("value","p_"+allstops[i]);
        target_select.appendChild(opt);
        //console.log(allstops[i]);
    }
}

function startPage(){
    makeOptions();
}



