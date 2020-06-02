<%@ page contentType="text/html;charset=UTF-8" %>

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
<%@ include file="/navbar.jsp"%>

<section class="section columns is-fullheight">
    <div class="column">
        <div class="field">
            <h1 class="title is-2">COME FUNZIONA MEDICINE DEALER</h1>
            <p class="paragraph is-size-4">
                Medicine dealer è un software pensato per aiutare i nostri clienti, in particolare anziani a ricordarsi di assumere le medicine prescritte dal medico.
            </p>
            <p class="paragraph is-size-4">
                Il funzionamento di questo software è basato su un porta medicine, che conterrà le medicine che si dovranno assumere e un sito web per la sua gestione.
            </p>
            <h1 class="title is-2">Il sito web</h1>
            <p class="paragraph is-size-4">
                Il sito comprende la registrazione dell’utente, successivamente si potrà associare a più porta medicine per la loro gestione.
                Sarà presente un calendario con l’elenco delle medicine per ogni giorno, per cambiare il porta medicine da controllare basterà scegliere quello desiderato dalla listbox.
                Si potranno aggiungere nuovi farmaci, rimuoverli completamente o solo per il giorno selezionato.
            </p>
            <h1 class="title is-2">Il porta medicine</h1>
            <p class="paragraph is-size-4">
                Il porta medicine sarà composto da n-slot, in ognuno di questi sarà presente un farmaco associato grazie alle funzioni offerte dal sito.
                Ogni slot avrà una luce che si accenderà quando la medicina al suo interno andrà assunta, mentre un dispositivo acustico emetterà un suono che ricorderà l’assunzione.
            </p>
        </div>
    </div>

    <div class="column is-3 has-background-white-ter">
        <aside class="menu">
            <p class="menu-label">Login</p>

            <form method="GET" action="${pageContext.request.contextPath}/api/utenti">
                <div class="field">
                    <label class="label">Inserisci l'email</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="email"  placeholder="Email" id="email" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <label class="label">Inserisci la password</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="password"  placeholder="Password" id="psw"required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <p class="help has-text-centered is-danger form-err"></p>

                    <div class="control has-text-centered">
                        <input type="submit" class="button is-rounded" value="ACCEDI"/>
                    </div>

                    <p class="help has-text-centered">
                        Se non sei ancora regitrato clicca
                        <a href="${pageContext.request.contextPath}/registrazione">QUI</a>
                    </p>
                </div>
            </form>
        </aside>
    </div>
</section>


<script>
    document.querySelector('form').customSubmit = (event) => {
        const form = event.target;

        const ema = document.getElementById("email").value;
        const psw = document.getElementById("psw").value;

        return fetch(form.action + "?email=" + ema + "&password=" + psw, {
            method: form.method
        }).then(async response => {
            const code = response.status;

            if(code === 200) {
                // logged
                location.href = '${pageContext.request.contextPath}/home';
            } else if(code === 401) {
                // password wrong or email not found
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