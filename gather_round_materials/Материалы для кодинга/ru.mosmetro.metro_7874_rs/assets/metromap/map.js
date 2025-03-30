
// Layers
const layerStations = document.getElementById('layer_stations');
const layerEmergencies = document.getElementById('layer_emergencies');
const layerConnections = document.getElementById('layer_connections');
const layerTransitions = document.getElementById('layer_transitions');
const layerAdditional = document.getElementById('layer_additional');
const layerNavigation = document.getElementById('layer_navigation');
const layerLocation = document.getElementById('layer_location');
const layerServices = document.getElementById('layer_services');
const layerBanners = document.getElementById('layer_banners');


function toast(message) {
    Android.toast(message);
}

function renderLayer(layer, blur, svg) {
    switch (layer) {

        case 'layer_stations':
            layerStations.innerHTML = svg;
            animateElement(layerStations, true, true);
            break;

        case 'layer_emergencies':
            layerEmergencies.innerHTML = svg;
            animateElement(layerEmergencies, true, false);
            break;

        case 'layer_connections':
            layerConnections.innerHTML = svg;
            animateElement(layerConnections, true, true);
            break;

        case 'layer_transitions':
            layerTransitions.innerHTML = svg;
            animateElement(layerTransitions, true, true);
            break;

        case 'layer_additional':
            layerAdditional.innerHTML = svg;
            animateElement(layerAdditional, true, true);
            break;

        case 'layer_location':
            layerLocation.innerHTML = svg;
            animateElement(layerLocation, true, false);
            break;

        case 'layer_navigation':
            if (svg == 'null') {
                animateElement(layerNavigation, false, false);
                setTimeout(function() {
                    layerNavigation.innerHTML = '';
                    blurLayer(layerNavigation, false);
                }, 200);
            } else {
                blurLayer(layerNavigation, blur);
                layerNavigation.innerHTML = svg;
                animateElement(layerNavigation, true, false);
            }
            break;

        case 'layer_banners':
            layerBanners.innerHTML = svg;
            animateElement(layerBanners, true, false);
            break;

        case 'layer_services':
            if (svg == 'null') {
                animateElement(layerServices, false, true);
                layerServices.innerHTML = '';
            } else {
                layerServices.innerHTML += svg;
                animateElement(layerServices, true, true);
            }
            break;
    }

    console.log(layer + ' rendered');

    Android.loaded();
}


function animateElement(e, show, slow) {
    var css = slow ? 'show-slow' : 'show-fast';
    if (show) {
        e.classList.add(css);
    } else {
        e.classList.remove(css);
    }
}

function blurLayer(e, blur) {
    if (blur) {
        e.classList.add('blurred');
    } else {
        e.classList.remove('blurred');
    }
}

function setNightMode() {
    let doc = document.documentElement;
    doc.style.setProperty("--text-fill", "#f2f2f2");
    doc.style.setProperty("--opacity-background-color", "rgba(22, 24, 27, 0.6)");
    doc.style.setProperty("--blur-background-color", "rgba(22, 24, 27, 0.8)");
}


//function tapStation(e) {
//    e.preventDefault();
//    if (e.target.id.includes('tap-grid')) {
//        const id = e.target.id.replace(/[^0-9.]/g, "");
//
//    }
//}

//function decode(str) {
//    return decodeURIComponent(atob(str).split('').map(function(c) {
//        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
//    }).join(''));
//}
