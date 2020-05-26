<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>home</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script defer src="${pageContext.request.contextPath}/node_modules/@fortawesome/fontawesome-free/js/all.min.js"></script>

    <script defer src="${pageContext.request.contextPath}/node_modules/jxon/jxon.min.js"></script>
    <script defer src="${pageContext.request.contextPath}/node_modules/moment/min/moment.min.js"></script>

    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/core/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/core/main.min.js'></script>
    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/daygrid/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/daygrid/main.min.js'></script>
    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/timegrid/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/timegrid/main.min.js'></script>
    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/list/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/list/main.min.js'></script>
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/interaction/main.min.js'></script>

    <script src='${pageContext.request.contextPath}/js/BulmaCalendarTheme.js'></script>
    <link href='${pageContext.request.contextPath}/css/bulma-calendar-theme.css' rel='stylesheet' />
</head>

<body>
<%@ include file="/navbar.jsp"%>

<%@ include file="/login_modal.jsp"%>
<%@ include file="day_click_modal.jsp"%>

<section class="section columns is-fullheight">
    <%@ include file="calendar.jsp"%>

    <div class="column is-hidden-mobile is-3 has-background-white-ter">
        <aside class="menu">

            <p class="menu-label">Porta Medicine di:</p>
            <%@ include file="devices.jsp"%>

            <div class="field">
                <div class="control">
                    <button id="add-medicine-btn" class="button is-primary is-rounded is-fullwidth">AGGIUNGI UN FARMACO</button>
                </div>
            </div>

            <div class="field">
                <div class="control">
                    <button id="remove-medicine-btn" class="button is-primary is-rounded is-fullwidth">RIMUOVI MEDICINE</button>
                </div>
            </div>

            <!--    <div class="section">-->
            <!--        <div class="column is-one-half has-background-white-ter">-->

            <!--            <div class="field has-text-centered">-->
            <!--                <h1 class="subtitle">VUOI RIMUOVERE UN FARMACO DALL'ELENCO?</h1>-->
            <!--                <div class="control has-text-centered">-->
            <!--                    <input type="submit" class="button is-rounded" value="RIMUOVI UN FARMACO" onclick="rimuoviFarmaco()"/>-->
            <!--                </div>-->
            <!--            </div>-->

            <!--            <div class="field has-text-centered">-->
            <!--                <h1 class="subtitle">SE CLICCHI SU UN GIORNO DEL CALENDARIO DOVREBBE USCIRE L'ELENCO DELLE MEDICINE RELATIVE A QUEL GIORNO</h1>-->
            <!--                <div class="control has-text-centered">-->
            <!--                    <input type="submit" class="button is-rounded" value="TIPO COSI" onclick="farmaciDelGiorno()"/>-->
            <!--                </div>-->
            <!--            </div>-->
            <!--            <br>-->
            <!--        </div>-->
            <!--    </div>-->
        </aside>
    </div>
</section>

<script>
    window.addEventListener('load', function () {
        'use strict';

        // Add listeners
        const portaMedicineSelect = document.getElementById('porta-medicine-container');
        portaMedicineSelect.addEventListener('change', () => {
            let date = new Date();
            Calendar.calendar.gotoDate(date);
            Calendar.reloadEvents();
        });

        const addMedicineBtn = document.getElementById('add-medicine-btn');
        addMedicineBtn.addEventListener('click', () => {
            const id = portaMedicineSelect.options[portaMedicineSelect.selectedIndex].value;
            location.href = "${pageContext.request.contextPath}/aggiungiFarmaco?id_porta=" + id;
        });

        const removeMedicineBtn = document.getElementById('remove-medicine-btn');
        removeMedicineBtn.addEventListener('click', () => {
            const id = portaMedicineSelect.options[portaMedicineSelect.selectedIndex].value;
            location.href = "${pageContext.request.contextPath}/rimuoviFarmaco?id_porta=" + id;
        });
        // First load
        Devices.reloadDevices().then(() => Calendar.reloadEvents());
    });

    function rimuoviFarmaco() { location.href = "${pageContext.request.contextPath}/rimuoviFarmaco"; }
</script>

</body>
</html>