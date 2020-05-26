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
    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/list/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/list/main.min.js'></script>
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/interaction/main.min.js'></script>
</head>

<body>
<%@ include file="/navbar.jsp"%>

<%@ include file="day_click_modal.jsp"%>

<section class="section columns is-fullheight">
    <div class="column">
        <%@ include file="calendar.jsp"%>
    </div>

    <div class="column is-3 has-background-white-ter">
        <aside class="menu">

            <p class="menu-label">Porta Medicine di:</p>
            <%@ include file="devices.jsp"%>

            <!--    <div class="section">-->
            <!--        <div class="column is-one-half has-background-white-ter">-->
            <!--            <div class="field has-text-centered">-->
            <!--                <h1 class="subtitle">VUOI AGGIUNGERE UN FARMACO ALL'ELENCO?</h1>-->
            <!--                    <div class="control has-text-centered">-->
            <!--                        <input type="submit" class="button is-rounded" value="AGGIUNGI UN FARMACO" onclick="aggiungiFarmaco()"/>-->
            <!--                    </div>-->
            <!--                </div>-->
            <!--            <br>-->

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
        const portaMedicineSelect = document.querySelector('#porta-medicine-container');
        portaMedicineSelect.addEventListener("change", () => Calendar.reloadEvents("MONTH",null));
        //TODO: gotoDate current month when device selected change
        // First load
        Devices.reloadDevices().then(() => Calendar.reloadEvents("MONTH", null));
    });

    function aggiungiFarmaco(){
        let id = document.getElementById("porta-medicine-container").selectedIndex.id;
        location.href = "${pageContext.request.contextPath}/aggiungiFarmaco?id_porta=" + id;
    }

    function rimuoviFarmaco() { location.href = "${pageContext.request.contextPath}/rimuoviFarmaco"; }
</script>

</body>
</html>