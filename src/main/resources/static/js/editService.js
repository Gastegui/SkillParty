var botonPublicar;
window.onload = function() {

    botonPublicar = document.getElementById('botonPublicar');

    botonPublicar.addEventListener("click", privado);
}

function privado() {
    if(botonPublicar.innerHTML == 'Publicar servicio') {
        botonPublicar.innerHTML = 'Ocultar servicio';
    } else {
        botonPublicar.innerHTML = 'Publicar servicio';
    }
}

