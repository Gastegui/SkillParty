var password1;
var password2;
var creator;
var text1;
var text2;
var botoia;
var mezua;
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

    mezua = document.getElementById('testua');
    botoia = document.getElementById('login');
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
        mezua.innerHTML = "Ondo sartu dira pasahitzak";
        login.classList.remove("disabled");

    }
    else
    {
        mezua.innerHTML = "Gaizki sartu dira pasahitzak";
        login.classList.add("disabled");
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

