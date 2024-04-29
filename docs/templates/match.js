function abrirVentana(partidoSeleccionado){
    const seccionReserva = partidoSeleccionado.closest('.reserva');

    document.getElementById('ventanaFlotante').style.display = 'block';
    document.getElementById('fondoOscuro').style.display = 'block';
    ajustarPosicion(seccionReserva);

    // Guardar referencia al botón seleccionado para su uso posterior
    document.getElementById('ventanaFlotante').setAttribute('dataPartidoSeleccionado', partidoSeleccionado.id);
}

function cerrarVentana() {
    document.getElementById('ventanaFlotante').style.display = 'none';
    document.getElementById('fondoOscuro').style.display = 'none';
}

function ajustarPosicion(seccionReserva) {
    let ventanaFlotante = document.getElementById('ventanaFlotante');

    ventanaFlotante.style.position = 'absolute';
    ventanaFlotante.style.top = seccionReserva.offsetTop + 'px';

    // Ajustar 'left' para colocar la ventana a la derecha de la reserva, incluyendo un margen adicional para separación visual.
    // Asumiendo que seccionReserva.offsetLeft + seccionReserva.offsetWidth da el punto final de la reserva, añade, por ejemplo, 20px más.
    ventanaFlotante.style.left = (seccionReserva.offsetLeft + seccionReserva.offsetWidth + 20) + 'px';

}

function generarReservas(reservas) {
    const seccionesReserva = document.getElementById('seccionesReserva');
    reservas.forEach(reserva => {
        const divReserva = document.createElement('div');
        divReserva.classList.add('reserva');
        divReserva.innerHTML = `
            <div class="horario"><p>${reserva.horario}</p></div>
            <div class="jugadores">${generarBotonesJugadores(reserva.id, 4)}</div>
            <div class="info">
                <p>Nivel: ${reserva.nivel}</p>
                <div class="precio">Precio: ${reserva.precio}</div>
            </div>
        `;
        seccionesReserva.appendChild(divReserva);
    });
}

function generarBotonesJugadores(idReserva, numeroJugadores) {
    let botones = '';
    for (let i = 1; i <= numeroJugadores; i++) {
        botones += `<button class="boton jugador" id="${idReserva}-jugador${i}" onclick="abrirVentana(this)">+</button>`;
    }
    return botones;
}

function generarListaAmigos(amigos)
{
    const listaAmigos = document.getElementById('listaAmigos');
    amigos.forEach((amigo, index) => {
        const divAmigo = document.createElement('div');
        divAmigo.classList.add('amigo');
        divAmigo.innerHTML = `
            <p>${amigo}</p>
            <button class="boton" onclick="agregarAmigo('${amigo}')">+</button>
        `;
        listaAmigos.appendChild(divAmigo);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const reservas = [
        { id: "reserva1", horario: "Mañana | 18:00", nivel: "Intermedio", precio: "99€" },
        { id: "reserva2", horario: "Mañana | 21:00", nivel: "Intermedio", precio: "119€" }
        // Añadir más reservas según sea necesario
    ];

    const amigos = ["Amigo1", "Amigo2", "Amigo3"];

    generarReservas(reservas);
    generarListaAmigos(amigos);
});
