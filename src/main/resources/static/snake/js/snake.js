var tabla = [];
var serpiente = [];
var tamaño = 0;
var direccion = 'D';
var ultimaDireccion = "D";
var timeout;
var crecer = false;
var terminado = false;

document.addEventListener("keydown", function(event) {
    // Obtener el código de la tecla presionada
    var keyCode = event.keyCode || event.which;
    // Convertir el código de la tecla a su correspondiente carácter
    var key = String.fromCharCode(keyCode);
    
    // Verificar qué tecla se presionó
    switch (keyCode) {
        case 87:
        case 38:
            if(ultimaDireccion != "S")
                direccion = "W";
            break;
        case 65:
        case 37:
            if(ultimaDireccion != "D")
                direccion = "A";
            break;
        case 83:
        case 40:
            if(ultimaDireccion != "W")
                direccion = "S";
            break;
        case 68:
        case 39:
            if(ultimaDireccion != "A")
                direccion = "D";
            break;
        default:
            break;
    }
    console.log(keyCode);
});

function bucle() {
    avanzar();
    clearTimeout(timeout);
    if(terminado == false)
        timeout = setTimeout(bucle, 250-tamaño*3);
}

function randInt(min, max) {
    max -= 1;
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function colocarManzana() {

    randx = randInt(0, elementos);
    randy = randInt(0, elementos);
    if(tabla[randx][randy].classList.contains("verde"))
    {
        colocarManzana();
    }
    return tabla[randx][randy].classList.add("rojo");
}

function derrota() {
    clearTimeout(timeout);
    terminado = true;
    alert("Juego terminado!")
}

function avanzar() {
    siguienteC = siguienteCabeza();
    aux1x = 0;
    aux1y = 0;
    aux2x = 0;
    aux2y = 0;

    if(tabla[siguienteC[0]][siguienteC[1]].classList.contains("verde"))
    {
        derrota();
        return;
    }

    if(tabla[siguienteC[0]][siguienteC[1]].classList.contains("rojo"))
        crecer = true;

    aux2x = serpiente[0][0];
    aux2y = serpiente[0][1];
    for(var i = 0; i < tamaño; i++) {
        
        if(i == 0) {
            serpiente[0][0] = siguienteC[0];
            serpiente[0][1] = siguienteC[1];
            tabla[siguienteC[0]][siguienteC[1]].classList.add("verde");
            if(crecer == true)
            tabla[siguienteC[0]][siguienteC[1]].classList.remove("rojo");
        }
        else {
            serpiente[i][0] = aux1x;
            serpiente[i][1] = aux1y;
        }
        aux1x = aux2x;
        aux1y = aux2y;
        if(i+1 != tamaño) {
            aux2x = serpiente[i+1][0];
            aux2y = serpiente[i+1][1];
        }
    }
    if(crecer == true) {
        serpiente[tamaño] = [];
        serpiente[tamaño][0] = aux1x;
        serpiente[tamaño][1] = aux1y;
        tamaño += 1;
        crecer = false;
        colocarManzana();
    }
    else {
        tabla[aux2x][aux2y].classList.remove("verde");
    }
}

function siguienteCabeza() {
    ret = [];
    switch(direccion) {
        case "W":
            if(serpiente[0][0] == 0)
            {
                ret[0] = elementos-1;
                ret[1] = serpiente[0][1];
                break;
            }
            else
            {
                ret[0] = serpiente[0][0]-1;
                ret[1] = serpiente[0][1];
                break;
            }
        case "S":
            if(serpiente[0][0] == elementos-1)
            {
                ret[0] = 0;
                ret[1] = serpiente[0][1];
                break;
            }
            else
            {
                ret[0] = serpiente[0][0]+1;
                ret[1] = serpiente[0][1];
                break;
            }
        case "A":
            if(serpiente[0][1] == 0)
            {
                ret[0] = serpiente[0][0];
                ret[1] = elementos-1;
                break;
            }
            else
            {
                ret[0] = serpiente[0][0];
                ret[1] = serpiente[0][1]-1;
                break;
            }
        case "D":
            if(serpiente[0][1] == elementos-1)
            {
                ret[0] = serpiente[0][0];
                ret[1] = 0;
                break;
            }
            else
            {
                ret[0] = serpiente[0][0];
                ret[1] = serpiente[0][1]+1;
                break;
            }
    }
    ultimaDireccion = direccion;
    return ret;
}

function iniciar() {

    tamaño = 2;
    serpiente[0] = [];
    serpiente[1] = [];
    serpiente[1][0] = 0;
    serpiente[1][1] = 0;
    serpiente[0][0] = 0;
    serpiente[0][1] = 1;
    tabla[0][0].classList.add("verde");
    tabla[0][1].classList.add("verde");
    colocarManzana();
    bucle();
}

document.addEventListener("DOMContentLoaded", function() {
    var a = 0;
    for(let i = 0; i < elementos; i++) {
        tabla[i] = [];
        for(let j = 0; j < elementos; j++) {
            id = (i+1)+"-"+(j+1);
            tabla[i][j] = document.getElementById(id);
        }
    }
    iniciar();
});
