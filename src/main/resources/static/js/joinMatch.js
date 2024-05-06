document.addEventListener("DOMContentLoaded", () => {
    let partidos = document.getElementsByClassName("partido");

    for(let i = 0; i < partidos.length; i++) {

        let partido = partidos.item(i);
        let form = partido.getElementsByClassName("form-unirse").item(0);
        
        //Solo hay un boton y un idPartido por form, luego cogemos el primer elemento.
        let boton = form.getElementsByClassName("unirse-btn").item(0); 
        let idPartido_v = form.getElementsByClassName("idPartido").item(0).value;

        let p_num_participantes = partido.getElementsByClassName("num-participantes").item(0);
        
        boton.onclick = (e) => {
            e.preventDefault();

            go(boton.parentNode.action, 'POST', {
                    idPartido: idPartido_v
            })
            .then(d => {
                boton.style.backgroundColor = "lightgreen";
                boton.style.color = "black";

                boton.textContent = "Te has unido correctamente al partido";
                boton.setAttribute("disabled", "true");
                p_num_participantes.textContent = "Numero actual de participantes: " +  d.result;
            })
            .catch(e => {
                boton.setAttribute("class", "btn-danger");
                boton.textContent = e.text;
                boton.setAttribute("disabled", "true");
            } );
        }
    }
});