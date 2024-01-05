const config = {
    clientId: "foodanalytics",
    clientSecret: "food123",
    callbackUrl: "http://localhost:4000",
    authorizeUrl: "http://localhost:3002/oauth/authorize",
    tokenUrl: "http://localhost:3002/oauth/token",
    cozinhasUrl: "http://localhost:3001/v1/cozinhas",
};

let accessToken = "";

function generateCodeVerifier() {
    let codeVerifier = generateRandomString(128);
    localStorage.setItem("codeVerifier", codeVerifier);
    return codeVerifier;
}

function generateRandomString(length) {
    let text = "";
    let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (let i = 0; i < length; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return text;
}

function generateCodeChallenge(codeVerifier) {
    return base64URL(CryptoJS.SHA256(codeVerifier));
}

function getCodeVerifier() {
    return localStorage.getItem("codeVerifier");
}

function base64URL(string) {
    return string.toString(CryptoJS.enc.Base64).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
}


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

function gerarAccessTokenPKCE(code) {
    alert("Gerando access token com code " + code);

    let codeVerifier = getCodeVerifier();
    const clientAuth = btoa(`${config.clientId}:${config.clientSecret}`);

    let params = new URLSearchParams();
    params.append("grant_type", "authorization_code");
    params.append("code", code);
    params.append("redirect_uri", config.callbackUrl);
    params.append("code_verifier", codeVerifier);

    fetch(config.tokenUrl, {
        method: "POST",
        headers: {
            Authorization: `Basic ${clientAuth}`,
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params.toString()
    })
        .then(response => response.json())
        .then(data => {
            accessToken = data.access_token;
            alert("Access token gerado: " + accessToken);
        })
        .catch(error => {
            alert("Erro ao gerar access key");
        });
}
function loginPKCE() {
    debugger
    let codeVerifier = generateCodeVerifier();
    let codeChallenge = generateCodeChallenge(codeVerifier);

    window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&redirect_uri=${config.callbackUrl}&code_challenge_method=s256&code_challenge=${codeChallenge}`;
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
            // alert("State inválido");
            gerarAccessTokenPKCE(code);
        }
    }

    document.getElementById("btn-consultar").addEventListener("click", consultar);
    document.getElementById("btn-login").addEventListener("click", login);
    document.getElementById("btn-login-pkce").addEventListener("click", loginPKCE);

});
