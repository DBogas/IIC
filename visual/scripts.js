// buttons/page init stuff
function startPage(){
    // show menu
    switchDiv("opts","opt_picker");
}

//div 1 is showing, div 2 is hidden, this method hides 1 and shows 2
function switchDiv(div1, div2){
    document.getElementById(div1).style.display = "none";
    document.getElementById(div2).style.display = "block";
}

function showStopPicker(){
    switchDiv('opt_picker','show_stop');
}

function backFromStops(){
    switchDiv('show_stop','opt_picker');
}

//Google Maps API stuff
function initMap() {
        makeOptions();
        var target = document.getElementById("drop_stops");
        var selectedStop =  window[target.options[target.selectedIndex].value.trim()]; 
        var lat1 = selectedStop.latitude;
        var lon1 = selectedStop.longitude;
        var zoom = 15;
        var myLatLng = {lat: lat1,lng:lon1};
        
        var map = new google.maps.Map(document.getElementById('mapa'), {
          zoom: 15,
          center: myLatLng
        });
        
        var marker = new google.maps.Marker({
          position: myLatLng,
          map: map
        });
}

function makeOptions() {
    var i;
    var target_select = document.getElementById("drop_stops");
    for (i=0; i<allstops.length; i++){
        var opt = document.createElement("option");
        opt.innerHTML  = allstops[i];
        opt.setAttribute("value","p_"+allstops[i]);
        target_select.appendChild(opt);
    }
}



