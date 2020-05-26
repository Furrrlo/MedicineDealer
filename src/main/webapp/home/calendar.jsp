<%@ page contentType="text/html;charset=UTF-8" %>

<div>
    <div class="calendar" id='calendar'></div>
</div>

<script>
    const Calendar = (() => {

        function Calendar() {}

        Calendar.calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
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
                    click: () => Calendar.reloadEvents()
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

                Calendar.calendar.getEvents().forEach(event => {
                    if(italianTimeFormat(event.start) === dateClicked) events.push(event);
                })

                DayClickModal.open(dateClicked, events);
            }
        });
        Calendar.calendar.render();

        const refreshButton = Calendar.calendar.el.querySelector('.fc-refresh-button');

        Calendar.reloadEvents = () => {
            refreshButton.classList.add('is-loading');
            refreshButton.disabled = true;

            return fetchEvents().then(events => {
                // Remove events
                Calendar.calendar.getEvents().forEach(event => { event.remove(); })
                // Load new ones
                Calendar.calendar.addEventSource(parseEvents(events));

                refreshButton.classList.remove('is-loading');
                refreshButton.disabled = false;
            }).catch(ex => {
                console.error(ex);

                refreshButton.classList.remove('is-loading');
                refreshButton.disabled = false;
            });
        };

        function fetchEvents() {
            const portaMedicineSelect = document.querySelector('#porta-medicine-container');
            let id_porta_medicine = portaMedicineSelect.options[portaMedicineSelect.selectedIndex].value;
            //TODO: How to get granularity
            let granularity = "MONTH"
            let path = "${pageContext.request.contextPath}/api/eventi?" +
                "granularita=" + granularity + "&" +
                "id_porta_medicine=" + id_porta_medicine;

            return fetch(path).then(async response => {

                if(response.status === 401) {
                    LoginModal.open();
                    throw response.status + ": " + (await response.text());
                }

                if(!response.ok)
                    throw response.status + ": " + (await response.text());
                return JXON.stringToJs(await response.text());
            }).then(events => {
                let obj = events.calendario.evento;
                if(!obj || obj.forEach)
                    return obj;
                return [ obj ];
            })
        }

        function parseEvents(events) {
            if(!events) {
                alert("NON CI SONO MEDICINE DA PRENDERE QUESTO MESE");
                return [];
            }

            let eventList = [];
            events.forEach(event => {
                let assunzioni = event.assunzioni.assunzione;
                if(assunzioni && !assunzioni.forEach)
                    assunzioni = [assunzioni];
                if(!assunzioni)
                    return;

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
        }

        function italianTimeFormat(dateUTC) {
            if (!dateUTC)
                return null;

            //TODO: print something like "Lun 25 Mar 2020"
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

        return Calendar;
    })();
</script>