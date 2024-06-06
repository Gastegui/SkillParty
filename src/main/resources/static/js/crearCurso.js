document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('mainForm');
    var select = document.getElementById('lang');
    var cat = document.getElementById('category');
  
    form.addEventListener('submit', function (event) {
      var langValue = select.value;
      var catValue = cat.value;
  
      if ((langValue === "") || catValue === "") {
        alert('Por favor, selecciona una opción.');
        event.preventDefault();
        event.stopPropagation();
      }
  
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
  
      form.classList.add('was-validated');
    }, false);
  });