function abrirVentana(partidoSeleccionado){
    const seccionReserva = partidoSeleccionado.closest('.reserva');

    document.getElementById('ventanaFlotante').style.display = 'block';
    document.getElementById('fondoOscuro').style.display = 'block';
    ajustarPosicion(seccionReserva);
}

function cerrarVentana() {
    document.getElementById('ventanaFlotante').style.display = 'none';
    document.getElementById('fondoOscuro').style.display = 'none';
}

function ajustarPosicion(seccionReserva) {
    var ventanaFlotante = document.getElementById('ventanaFlotante');
    var partidosDebajo = document.querySelectorAll('.reserva');

    var distanciaDesdeArriba = seccionReserva.offsetTop + seccionReserva.offsetHeight;

    // Desplazar los partidos debajo
    var desplazamiento = seccionReserva.offsetHeight;

    ventanaFlotante.style.top = distanciaDesdeArriba + 'px';

    partidosDebajo.forEach(function(partido) {
        if(partido = seccionReserva.nextElementSibling)
            partido.style.marginTop = desplazamiento + 'px';
    });
}

function agregarReservas() {
    var reserva = document.getElementById('reserva');
    var nuevaReserva = reserva.cloneNode(true); // Clonar la reserva
    document.getElementById('seccionesReserva').appendChild(nuevaReserva); // Agregar la nueva reserva al contenedor
}

document.addEventListener('DOMContentLoaded', function() {
    var reservas = 3;
    for (var i = 0; i < reservas - 1; i++) {
        agregarReservas();
    }

    var partidos = document.querySelectorAll('.partido');
    partidos.forEach(function(partido) {
        partido.addEventListener('click', function() {
            abrirVentana(partido); // Llama a la función para abrir la ventana flotante con el partido seleccionado
        });
    });
});
