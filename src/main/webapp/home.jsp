<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>home</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script defer src="${pageContext.request.contextPath}/node_modules/@fortawesome/fontawesome-free/js/all.min.js"></script>

    <script defer src="${pageContext.request.contextPath}/node_modules/jxon/jxon.min.js"></script>
    <script defer src="${pageContext.request.contextPath}/js/moment/moment.js"></script>

    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/core/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/core/main.min.js'></script>
    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/daygrid/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/daygrid/main.min.js'></script>
    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/list/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/list/main.min.js'></script>
    <link href='${pageContext.request.contextPath}/node_modules/@fullcalendar/interaction/main.min.css' rel='stylesheet' />
    <script src='${pageContext.request.contextPath}/node_modules/@fullcalendar/interaction/main.min.js'></script>


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

<section class="section columns is-fullheight">
    <div class="column">
        <div>
            <div class="calendar" id='calendar'></div>
        </div>
    </div>

    <div class="column is-3 has-background-white-ter">
        <aside class="menu">

            <p class="menu-label">Porta Medicine di:</p>

            <div class="field">
                <div class="control">
                    <div class="select is-rounded is-fullwidth">
                        <!-- Template -->
                        <select class="is-hidden">
                            <option class="porta-medicine-template"></option>
                        </select>
                        <!-- Container -->
                        <select id="porta-medicine-container"></select>
                    </div>
                </div>
            </div>

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

        const calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
            height: "parent",
            plugins: [ 'interaction', 'dayGrid', 'timeGrid' ],
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'refresh'
            },
            customButtons: {
                refresh: {
                    text: 'Ricarica',
                    click: reloadEvents
                }
            },
            defaultDate: Date.now(),
            selectable: true,
            selectMirror: true,
            editable: true,
            event: [],
            dateClick: function (info) {
                console.log(info.dateStr);

                farmaciDelGiorno(info.date,getEventsByDate(info.date,calendar));
            }

        });
        calendar.render();

        const portaMedicineSelect = document.querySelector('#porta-medicine-container');
        const refreshButton = calendar.el.querySelector('.fc-refresh-button');

        const reloadDevices = (() => {
            const container = portaMedicineSelect;
            const templateClass = 'porta-medicine-template';
            const template = document.querySelector('.' + templateClass);

            return () => {
                return fetch('${pageContext.request.contextPath}/api/porta_medicine').then(async response => {
                    if(!response.ok)
                        throw response.status + ": " + (await response.text());
                    return JXON.stringToJs(await response.text());
                }).then(devices => {
                    // XML is kinda shit to use
                    let obj = devices.porta_medicine.porta_medicina;
                    if(obj.forEach)
                        return obj;
                    return [ obj ];
                }).then(devices => {
                    // Cleanup container
                    while (container.firstChild)
                        container.removeChild(container.firstChild);
                    // Load devices
                    if(!devices.forEach)
                        return;

                    devices.forEach(device => {
                        const deviceNode = template.cloneNode(true);
                        deviceNode.classList.remove(templateClass);

                        deviceNode.value = device.id;
                        deviceNode.textContent = device.nome;

                        container.appendChild(deviceNode);
                    });
                }).catch(ex => {
                    console.error(ex);
                });
            };
        })();

        function reloadEvents() {
            refreshButton.classList.add('is-loading');
            refreshButton.disabled = true;

            // Remove events
            calendar.getEvents().forEach(event => { event.remove(); })
            // Load new ones
            let id_porta_medicine = portaMedicineSelect.options[portaMedicineSelect.selectedIndex].value;
            //TODO: How to get granularity
            let granularity = "MONTH"
            let path = "api/eventi?granularita=" + granularity + "&id_porta_medicine=" + id_porta_medicine;

            return fetch(path).then(async response => {
                if(!response.ok)
                    throw response.status + ": " + (await response.text());
                return JXON.stringToJs(await response.text());
            }).then(events => {
                let obj = events.calendario.evento;
                if(!obj || obj.forEach)
                    return obj;
                return [ obj ];
            }).then(events => {
                if(!events) {
                    alert("NON CI SONO MEDICINE DA PRENDERE QUESTO MESE");
                    return [];
                }

                let eventList = [];
                events.forEach(event => {
                    let assunzioni = event.assunzioni.assunzione;
                    if(assunzioni && !assunzioni.forEach)
                        assunzioni = [assunzioni];

                    if(assunzioni)
                        assunzioni.forEach(assunzione => {
                            let dateToProcess = assunzione.data + " " + assunzione.ora;
                            let myDateTime = moment(dateToProcess, 'YYYY-MM-DD HH:mm').format();

                            eventList.push({
                                title: event.nome_farmaco,
                                start: myDateTime
                            });
                        });
                });

                return eventList;
            }).then(events => {
                calendar.addEventSource(events);

                refreshButton.classList.remove('is-loading');
                refreshButton.disabled = false;
            }).catch(ex => {
                console.error(ex);

                refreshButton.classList.remove('is-loading');
                refreshButton.disabled = false;
            });
        }

        // Add listeners
        portaMedicineSelect.addEventListener("change", reloadEvents);
        // First load
        reloadDevices().then(reloadEvents);
    });

    function getEventsByDate(date,calendar){
        let events = [];

        events = calendar.getEvents();
        events.forEach(event => {
            if(event.start != date) event.remove();
        })

        return events;
    }

    function aggiungiFarmaco(){

        let id = document.getElementById("porta-medicine-container").selectedIndex.id;

        location.href = "aggiungiFarmaco.html?id_porta=" + id;
    }

    function rimuoviFarmaco() { location.href = "rimuoviFarmaco.html"; }

    function farmaciDelGiorno(date,events) {

        let eventStr = encodeURIComponent(JSON.stringify(events));

        //location.href = "farmaciDelGiorno.html?data=" + date +"&events=" + eventStr;
    }

</script>

</body>

</html>