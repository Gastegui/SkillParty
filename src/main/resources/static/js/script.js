var password1;
var password2;
var creator;
var text1;
var text2;
var botoia;
var aditionalInformation;
var infoPanels;

window.onload = function(){
    password1 = document.getElementById('firstPassword');
    password1.addEventListener("change", pasahitza1Gorde);
    password2 = document.getElementById('secondPassword');
    password2.addEventListener("change", pasahitza2Gorde);

    creator = document.getElementById('creator');
    creator.addEventListener("click", erakutsi);
    
    aditionalInformation = document.getElementById('gehigarri');
    infoPanels = document.getElementById('infoCards');

    botoia = document.getElementById('testua');
}

function pasahitza1Gorde(event){


    text1 = event.target.value;
}

function pasahitza2Gorde(event){


    text2 = event.target.value;
    konprobatu();
}
function konprobatu(){


    if(text1 == text2)
    {
        botoia.innerHTML = "Ondo sartu dira pasahitzak";
    }
    else
    {
        botoia.innerHTML = "Gaizki sartu dira pasahitzak";
    }
}

function erakutsi(){

    
    if(aditionalInformation.classList.contains("d-none"))
    {
        aditionalInformation.classList.remove("d-none");
        infoPanels.classList.remove("d-none");
    }
    else{
        aditionalInformation.classList.add("d-none");
        infoPanels.classList.add("d-none");
    }
    
}

if(premium.classList.contains("d-none"))
    {
        premium.classList.remove("d-none");
    }
    else{
        premium.classList.add("d-none");
    }