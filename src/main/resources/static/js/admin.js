document.addEventListener("DOMContentLoaded", () => {
    let ban = document.getElementById("banuser-btn");

    let infoBanDiv = document.getElementById("infoBan");

    ban.onclick = (e) => {
        e.preventDefault();
        let username_val = document.getElementById("username").value;

        go(ban.parentNode.action, 'POST', {
                username: username_val
        })
        .then(d => infoBanDiv.insertAdjacentHTML("beforeend", `<div>Usuario ${escapar(username_val)} baneado correctamente</div>`))
        .catch(e => infoBanDiv.insertAdjacentHTML("beforeend", `<div>Usuario ${escapar(username_val)} no baneado correctamente</div>`))
    }

    let formsMarcarLeido = document.getElementsByClassName("form-marcar-leido");
    for(let i = 0; i < formsMarcarLeido.length; i++) {
        let form = formsMarcarLeido.item(i);
        let btnMarcarLeido = form.getElementsByClassName("marcar-leido-btn").item(0);

        btnMarcarLeido.onclick = (e) => {
            e.preventDefault();
            let idMsj_val = form.getElementsByClassName("idMsj").item(0).value;

            go(btnMarcarLeido.parentNode.action, 'POST', {
                    idMsj : idMsj_val
            })
            .then(d => {
                btnMarcarLeido.textContent = 'Procesado';
                btnMarcarLeido.setAttribute("disabled", "true");
            });
        }
    }
});