<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Benvenuto in MEDICINE DEALER</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script defer src="${pageContext.request.contextPath}/node_modules/@fortawesome/fontawesome-free/js/all.min.js"></script>

    <script defer src="${pageContext.request.contextPath}/node_modules/jxon/jxon.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/validate.js"></script>
</head>

<body>
<section class="hero is-primary">
    <div class="hero-body">
        <div class="container">
            <h1 class="title is-1" >MEDICINE DEALER</h1>
            <h2 class="subtitle">GESTISCI L'ACQUISIZIONE DI MEDICINALI CON SEMPLICITA'</h2>
        </div>
    </div>
</section>

<div class="section">
    <form method="GET" action="${pageContext.request.contextPath}/api/utenti">
        <div class="columns is-centered">
            <div class="column is-one-quarter ">

                <div class="field">
                    <h1 class="subtitle">ACCEDI PER GESTIRE I PORTAMEDICINE</h1>
                </div>

                <div class="field">
                    <label class="label">Inserisci l'e-mail</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="email" placeholder="E-mail" id="email" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <label class="label">Inserisci la password</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="password"  placeholder="Password" id="password" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <p class="help has-text-centered is-danger form-err"></p>
                    <br>
                    <div class="control">
                        <button class="button is-rounded is-fullwidth">ACCEDI</button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
    document.querySelector("form").customSubmit = (event) => {

        const form = event.target;

        const ema = document.getElementById("email").value;
        const psw = document.getElementById("password").value;

        return fetch(form.action + "?email=" + ema + "&password=" + psw, {
            method: form.method
        }).then(async response => {
            const code = response.status;

            if (code === 200) {
                // logged
                location.href = 'home.html';
            } else if(code === 401) {
                // email not found or password wrong
                form.setCustomError("Credenziali non valide");
            } else if(!response.ok) {
                throw response.status + ": " + (await response.text());
            }
        }).catch(ex => {
            form.setCustomError("Errore del server");
            console.error(ex);
        });
    };
</script>

</body>
</html>