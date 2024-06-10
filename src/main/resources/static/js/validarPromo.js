var texto;
var promocion;
var descuento;
var sinDescuento;


window.onload = function(){
    texto = document.getElementById('textoPromocion');
    texto.addEventListener("change", comprobar);


    promocion = document.getElementsByClassName('conPromocion');

    sinDescuento = document.getElementById('sinPromocion');
    

}


function comprobar(event){
   
    if(event.target.value == "SKILLPARTY")
    {
        promocion[0].classList.remove("d-none");
        promocion[1].classList.remove("d-none");
        sinDescuento.classList.add("d-none");
        
    }
    else
    {
        promocion[0].classList.add("d-none");
        promocion[1].classList.add("d-none");
        sinDescuento.classList.remove("d-none");
    }
}




