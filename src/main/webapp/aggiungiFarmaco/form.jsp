<%@ page contentType="text/html;charset=UTF-8" %>

<form method="post" action="${pageContext.request.contextPath}/api/eventi" >
    <div class="field">
        <h1 class="SUBTITLE">AGGIUNGI UNA NUOVA MEDICINA AL CALENDARIO</h1>

        <%@ include file="medicine_field.jsp" %>
        <%@ include file="date_field.jsp" %>
        <%@ include file="hours_field.jsp" %>
        <%@ include file="repeat_field.jsp" %>

        <div class="field is-grouped is-grouped-centered">
            <p class="control">
                <input type="submit" class="button is-rounded is-fullwidth" value="AGGIUNGI FARMACO"/>
            </p>
            <p class="control">
                <button class="cancel-btn button is-rounded is-fullwidth">ANNULLA</button>
            </p>
        </div>
    </div>
</form>

<script>
    window.addEventListener('load', function() {

        const urlParams = new URLSearchParams(window.location.search);
        const idPortaMedicine = urlParams.get("id_porta_medicine");

        document.querySelector('form').customSubmit = (event) => {
            const form = event.target;
            const bodyObj = {
                "id_porta_medicine": idPortaMedicine,
                // TODO: slot-input
                "aic_farmaco": document.getElementById("aic-input").value,
                "data": document.getElementById("start-date-input").value,
                "orari": getHours()
            };
            const cadenza = getCadenza();
            if(cadenza)
                bodyObj.cadenza = cadenza;

            return fetch(form.action, {
                method: form.method,
                headers: {
                    'Content-Type': 'application/xml'
                },
                body: JXON.jsToString({ "nuovo_evento": bodyObj })
            }).then(async response => {
                if(!response.ok)
                    throw response.status + ": " + (await response.text());
                location.href = "${pageContext.request.contextPath}/home";
            }).catch(ex => {
                form.setCustomError("Errore del server");
                console.error(ex);
            });
        };

        function getCadenza() {

            if(!document.getElementById('repeat-checkbox').checked)
                return null;

            let cadenza = {
                "intervallo": document.getElementById('interval-input').value
            };

            let period = document.getElementById('period-select').value;
            if(period === "Giornaliera") {
                cadenza.giornaliera = '';
            } else if(period === "Settimanale") {
                cadenza.settimanale = {
                    "lunedi": document.getElementById("monday").checked,
                    "martedi": document.getElementById("tuesday").checked,
                    "mercoledi": document.getElementById("wednesday").checked,
                    "giovedi": document.getElementById("thursday").checked,
                    "venerdi": document.getElementById("friday").checked,
                    "sabato": document.getElementById("saturday").checked,
                    "domenica": document.getElementById("sunday").checked
                };
            }

            const selectedEnd = document.querySelector('input[type="radio"][name="end"]:checked').value;
            if(selectedEnd === "date") {
                cadenza.fine = {
                    "data": document.getElementById('end-date-input').value
                };
            } else if(selectedEnd === "assunzioni") {
                cadenza.fine = {
                    "occorenze": document.getElementById('num-ass-input').value
                };
            }

            return cadenza;
        }

        function getHours() {
            const hours = [];
            document.querySelectorAll('.hours-container input.hour-input').forEach(input => {
                hours.push({ "ora": input.value });
            });
            return hours;
        }

        document.querySelector('.cancel-btn').addEventListener('click', () => {
            location.href = "${pageContext.request.contextPath}/home";
        });

    });
</script>