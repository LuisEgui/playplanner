//Click en banear usuario
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
});