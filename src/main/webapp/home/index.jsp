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

    <div class="modal" id="day_click_modal">
        <div class="modal-background"></div>
        <div class="modal-card">
            <header class="modal-card-head">
                <p class="modal-card-title" id="modal_title"></p>
                <button class="delete" aria-label="close" onclick="discardModal()"></button>
            </header>
            <section class="modal-card-body">

                <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth" id="medicine_table">
                    <th>Nome Medicina</th>
                    <th>Ora Acquisione</th>
                    <th>Assunta</th>
                    <th>Slot</th>
                    <th>Clicca per rimuovere solo per oggi</th>
                </table>
            </section>
        </div>
    </div>

</section>

<script>

    function farmaciDelGiorno(date,events) {
        let modal = document.getElementById("day_click_modal");
        let table = document.getElementById("medicine_table");

        document.getElementById("modal_title").innerHTML = date;

        let i = 1;
        events.forEach(event => {
            let row = table.insertRow(i);

            let cell1 = row.insertCell(0);
            cell1.innerHTML = event.title;

            let cell2 = row.insertCell(1);
            cell2.innerHTML = event.start.getHours() + ":" + event.start.getMinutes();
            // let cell3

            let cell3 = row.insertCell(2);
            //TODO Assunta yes or no
            let cell4 = row.insertCell(3);
            //TODO Slot in which the medicine is

            let cell5 = row.insertCell(4);
            let btn = document.createElement("BUTTON");
            btn.innerHTML = "ELIMINA";
            btn.onclick = function (){

            };
            cell5.appendChild(btn);
        })

        modal.classList.add('is-active');
    }
    window.onclick = function (event){
        if(event.target.className == 'modal-background')
            discardModal();
    }

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
                let events = [];
                let dateClicked = italianTimeFormat(info.date);

                calendar.getEvents().forEach(event => {
                    if(italianTimeFormat(event.start) == dateClicked) events.push(event);
                })

                farmaciDelGiorno(dateClicked,events);
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
            let path = "${pageContext.request.contextPath}/api/eventi?" +
                "granularita=" + granularity + "&" +
                "id_porta_medicine=" + id_porta_medicine;

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

    function italianTimeFormat (dateUTC) {

        //TODO: print something like "Lun 25 Mar 2020"
        if (dateUTC) {
            let jsDateFormat = new Date(dateUTC)
            let fullStringTime = {
                day: Number(jsDateFormat.getDate() < 10) ? '0' + jsDateFormat.getDate() : jsDateFormat.getDate(),
                month: Number((jsDateFormat.getMonth() + 1)) < 10 ? '0' + (jsDateFormat.getMonth() + 1) : (jsDateFormat.getMonth() + 1),
                year: jsDateFormat.getFullYear(),
                hours: Number(jsDateFormat.getHours()) < 10 ? '0' + jsDateFormat.getHours() : jsDateFormat.getHours(),
                minutes: Number(jsDateFormat.getMinutes()) < 10 ? '0' + jsDateFormat.getMinutes() : jsDateFormat.getMinutes()
            }
            return fullStringTime.day + '/' + fullStringTime.month + '/' + fullStringTime.year + ' ';
        }
        return null
    }

    function discardModal(){
        let modal = document.getElementById("day_click_modal");
        let table = document.getElementById("medicine_table");

        for(let n = table.rows.length - 1;n > 0;n--) table.deleteRow(n);

        modal.classList.remove('is-active');
    }

    function aggiungiFarmaco(){
        let id = document.getElementById("porta-medicine-container").selectedIndex.id;
        location.href = "${pageContext.request.contextPath}/aggiungiFarmaco?id_porta=" + id;
    }

    function rimuoviFarmaco() { location.href = "${pageContext.request.contextPath}/rimuoviFarmaco"; }


</script>

</body>

</html>