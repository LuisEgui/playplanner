document.addEventListener("DOMContentLoaded", () => {
    if (config.socketUrl) {
        let match_chatToken = 'XhbWy-uPBI1N81IT'/*document.getElementById("match_chatToken")*/;
        console.log("Intenando suscribirse...");
        ws.subscribe(config.socketUrl, `/topic/${match_chatToken}`);

        /*let p = document.querySelector("#nav-unread");
        if (p) {
            go(`${config.rootUrl}/match/${match_id}/unread`, "GET").then(d => p.textContent = d.unread);
        }*/
    } else {
        console.log("Not opening websocket: missing config", config)
    }
});