document.addEventListener("DOMContentLoaded", function() {
    
    var createAccountButton = document.getElementById("create-account-button");
    var createAccountButtonHamburger = document.getElementById("create-account-button-hamburgesa");
    var loginButton = document.getElementById("login-button");
    var loginButtonHamburger = document.getElementById("login-button-hamburgesa");
    var serviceButton = document.getElementById("service-button");
    var serviceButtonHamburger = document.getElementById("service-button-hamburgesa");
    var courseButton = document.getElementById("course-button");
    var courseButtonHamburger = document.getElementById("course-button-hamburgesa");
    var currentPath = window.location.pathname;

    console.log('Current Path:', currentPath);

    if (currentPath === '/login') {
        if (createAccountButton) {
            console.log('Found createAccountButton');
            createAccountButton.classList.remove('d-none');
        } else {
            console.log('createAccountButton not found');
        }

        if (createAccountButtonHamburger) {
            console.log('Found createAccountButtonHamburger');
            createAccountButtonHamburger.classList.remove('d-none');
        } else {
            console.log('createAccountButtonHamburger not found');
        }

        if (loginButton) {
            console.log('Found loginButton');
            loginButton.classList.add('d-none');
        } else {
            console.log('loginButton not found');
        }

        if (loginButtonHamburger) {
            console.log('Found loginButtonHamburger');
            loginButtonHamburger.classList.add('d-none');
        } else {
            console.log('loginButtonHamburger not found');
        }

        if (serviceButton) {
            console.log('Found serviceButton');
            serviceButton.classList.add('d-none');
        } else {
            console.log('serviceButton not found');
        }

        if (serviceButtonHamburger) {
            console.log('Found serviceButtonHamburger');
            serviceButtonHamburger.classList.add('d-none');
        } else {
            console.log('serviceButtonHamburger not found');
        }

        if (courseButton) {
            console.log('Found courseButton');
            courseButton.classList.add('d-none');
        } else {
            console.log('courseButton not found');
        }

        if (courseButtonHamburger) {
            console.log('Found courseButtonHamburger');
            courseButtonHamburger.classList.add('d-none');
        } else {
            console.log('courseButtonHamburger not found');
        }
    }
});
