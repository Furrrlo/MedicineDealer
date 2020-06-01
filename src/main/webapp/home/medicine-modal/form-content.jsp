<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field">

    <input id="form-mode-input" type="hidden">
    <input id="event-id-input" type="hidden">
    <input id="device-id-input" type="hidden">

    <%@ include file="medicine_field.jsp" %>
    <%@ include file="date_field.jsp" %>
    <%@ include file="hours_field.jsp" %>
    <%@ include file="repeat_field.jsp" %>

    <div class="field">
        <p class="help has-text-centered is-danger form-err"></p>
    </div>
</div>

<script>
    window.addEventListener('load', function() {

        document.querySelector('form').customSubmit = (event) => {
            const form = event.target;
            const isEditing = document.getElementById('form-mode-input').value === 'edit';

            const bodyObj = {};
            if(!isEditing)
                bodyObj.id_porta_medicine = document.getElementById('device-id-input').value;
            // TODO: slot-input
            bodyObj.aic_farmaco = document.getElementById("aic-input").aic_value;
            bodyObj.data = document.getElementById("start-date-input").value;
            addCadenza(bodyObj, isEditing);
            bodyObj.orari = { ora: getHours() };

            console.log(bodyObj);
            console.log(JXON.jsToString(isEditing ?
                { "modifica_evento": bodyObj } :
                { "nuovo_evento": bodyObj }));

            const path = form.action + (isEditing ?
                '/' + document.getElementById('event-id-input').value :
                '');
            return fetch(path, {
                method: isEditing ? 'put' : 'post',
                headers: {
                    'Content-Type': 'application/xml'
                },
                body: JXON.jsToString(isEditing ?
                    { "modifica_evento": bodyObj } :
                    { "nuovo_evento": bodyObj })
            }).then(async response => {
                if(!response.ok)
                    throw response.status + ": " + (await response.text());

                MedicineModal.close();
                Calendar.reloadEvents();

            }).catch(ex => {
                form.setCustomError("Errore del server");
                console.error(ex);
            });
        };

        function addCadenza(bodyObj, isEditing) {

            if(!document.getElementById('repeat-checkbox').checked) {
                if(isEditing)
                    bodyObj.elimina_cadenza = '';
                return null;
            }

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
            } else if(isEditing) {
                cadenza.elimina_fine = '';
            }

            bodyObj.cadenza = cadenza;
        }

        function getHours() {
            const hours = [];
            document.querySelectorAll('.hours-container input.hour-input').forEach(input => {
                hours.push(input.value + ':00'); /// Add :00 so XML accepts it as valid
            });
            return hours;
        }
    });
</script>