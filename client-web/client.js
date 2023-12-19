const config = {
    clientId: "foodanalytics",
    clientSecret: "food123",
    callbackUrl: "http://localhost:4000",
    authorizeUrl: "http://localhost:3002/oauth/authorize",
    tokenUrl: "http://localhost:3002/oauth/token",
    cozinhasUrl: "http://localhost:3001/v1/cozinhas",
};

let accessToken = "";

async function consultar() {
    try {
        const response = await fetch(config.cozinhasUrl, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${accessToken}`,
                "Access-Control-Allow-Origin": "*",
            },
        });

        if (!response.ok) {
            throw new Error("Erro ao consultar recurso");
        }

        const data = await response.json();
        const json = JSON.stringify(data);
        document.getElementById("resultado").innerText = json;
    } catch (error) {
        alert(error.message);
    }
}

async function gerarAccessToken(code) {
    try {
        const clientAuth = btoa(`${config.clientId}:${config.clientSecret}`);
        const params = new URLSearchParams();
        params.append("grant_type", "authorization_code");
        params.append("code", code);
        params.append("redirect_uri", config.callbackUrl);

        const response = await fetch(config.tokenUrl, {
            method: "POST",
            headers: {
                Authorization: `Basic ${clientAuth}`,
                "Content-Type": "application/x-www-form-urlencoded",
                "Access-Control-Allow-Origin": "*",
            },
            body: params.toString(),
        });

        if (!response.ok) {
            throw new Error("Erro ao gerar access token");
        }

        const responseData = await response.json();
        accessToken = responseData.access_token;

        alert("Access token gerado: " + accessToken);
    } catch (error) {
        alert(error.message);
    }
}

function login() {
    const state = btoa(Math.random());
    localStorage.setItem("clientState", state);

    window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&state=${state}&redirect_uri=${config.callbackUrl}`;
}

document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");
    const state = params.get("state");
    const currentState = localStorage.getItem("clientState");

    if (code) {
        // window.history.replaceState(null, null, "/");

        if (currentState == state) {
            gerarAccessToken(code);
        } else {
            alert("State inválido");
        }
    }

    document.getElementById("btn-consultar").addEventListener("click", consultar);
    document.getElementById("btn-login").addEventListener("click", login);
});
