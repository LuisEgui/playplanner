function abrirVentana(){
    document.getElementById('ventanaFlotante').style.display = 'block';
    document.getElementById('fondoOscuro').style.display = 'block';
    ajustarPosicion();
}

function cerrarVentana() {
    document.getElementById('ventanaFlotante').style.display = 'none';
    document.getElementById('fondoOscuro').style.display = 'none';
}

function ajustarPosicion() {
    var ventanaFlotante = document.getElementById('ventanaFlotante');
    var reservaAnt = document.getElementById('reserva1');
    var reserva = document.getElementById('reserva2');

    reserva.style.marginTop = reserva.offsetHeight + 'px';
    ventanaFlotante.style.marginTop = reservaAnt.offsetHeight + 'px';
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
});
