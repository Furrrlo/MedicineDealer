<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registrazione</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script defer src="${pageContext.request.contextPath}/node_modules/@fortawesome/fontawesome-free/js/all.min.js"></script>

    <script defer src="${pageContext.request.contextPath}/node_modules/jxon/jxon.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/validate.js"></script>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            // Check whether the user is adult
            document.getElementById("date").addCustomValidator(input => {
                const today = new Date();
                const birthDate = input.value;
                const birthInfo = birthDate.split("-");

                let age = today.getFullYear() - parseInt(birthInfo[0]);
                const m = today.getMonth() - parseInt(birthInfo[1]);
                if (m < 0 || (m === 0 && today.getDate() < parseInt(birthInfo[2])))
                    age--;

                const isAdult = age >= 18;
                if(!isAdult)
                    return "L'utente deve essere un adulto";
                return true;
            });
            // Check password complexity
            document.getElementById("password1").addCustomValidator((() => {
                const passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{6,}$/;

                return input => {
                    const isComplex = passwordRegex.test(input.value);
                    if(!isComplex)
                        return "La password deve contenere almeno 6 caratteri con numeri e lettere";
                    return true;
                }
            })());
            // Check password matching
            [
                document.getElementById("password1"),
                document.getElementById("password2")
            ].forEach(input => input.addCustomValidator((() => {
                const input1 = document.getElementById("password1");
                const input2 = document.getElementById("password2");

                return () => {
                    const match = input1.value === input2.value;
                    if(!match) {
                        input1.setCustomError("");
                        input2.setCustomError("Le password non corrispondono");
                        return false;
                    }
                    input2.setCustomError("");
                    return true;
                }
            })()));
            // Form submission
            document.querySelector('form').customSubmit = (event) => {
                const form = event.target;
                const registration = {
                    "registrazione": {
                        "nome": form.querySelector("input#nome").value,
                        "cognome": form.querySelector("input#cognome").value,
                        "email": form.querySelector("input#email").value,
                        "data_nascita": form.querySelector("input#date").value,
                        "password": form.querySelector("input#password1").value,
                    }
                };

                return fetch(form.action, {
                    method: form.method,
                    headers: {
                        'Content-Type': 'application/xml'
                    },
                    body: JXON.jsToString(registration)
                }).then(async response => {
                    if(response.status === 409) {
                        form.setCustomError("Email già utilizzata");
                        return;
                    }

                    if(!response.ok)
                        throw response.status + ": " + (await response.text());
                    location.href = "${pageContext.request.contextPath}/home";
                }).catch(ex => {
                    form.setCustomError("Errore del server");
                    console.error(ex);
                });
            };
        });
    </script>
</head>

<body>
<%@ include file="/navbar.jsp"%>

<div class="section">
    <form method="POST" action="${pageContext.request.contextPath}/api/utenti">
        <div class="columns is-centered">
            <div class="column is-one-quarter ">
                <div class="field">
                    <h1 class="subtitle">REGISTRATI PER GESTIRE I PORTAMEDICINE</h1>
                </div>

                <div class="field">
                    <label class="label">Inserisci il nome</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="text"  placeholder="Nome" id="nome" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <label class="label">Inserisci il cognome</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="text"  placeholder="Cognome" id="cognome" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <label class="label">Inserisci l'e-mail</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="email"  placeholder="E-mail" id="email" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <label class="label">Seleziona la tua data di nascita</label>
                    <div class="control">
                        <input class="input is-primary is-rounded" type="date" id="date" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <label class="label">Inserisci la password</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="password"  placeholder="Password" id="password1" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <label class="label">Inserisci nuovamente la password</label>
                    <div class="control">
                        <input class="input is-primary is-rounded"
                               type="password"  placeholder="Password" id="password2" required/>
                    </div>
                    <p class="help is-danger err"></p>
                </div>

                <div class="field">
                    <p class="help is-danger form-err"></p>
                    <br>
                    <div class="control">
                        <input type="submit" class="button is-centered" value="REGISTRATI"/>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
</body>
</html>