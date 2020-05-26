<%@ page contentType="text/html;charset=UTF-8" %>

<div>
    <div class="calendar" id='calendar'></div>
</div>

<script>
    const Calendar = (() => {

        function Calendar() {}

        Calendar.calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
            height: "parent",
            plugins: [ 'interaction', 'dayGrid', 'timeGrid', 'bulma' ],
            themeSystem: 'bulma',
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'refresh'
            },
            customButtons: {
                refresh: {
                    text: 'Ricarica',
                    click: () => Calendar.reloadEvents("MONTH", null)
                },
                prev: {
                    click: function () {
                        Calendar.calendar.prev();
                        let date = Calendar.calendar.getDate();
                        Calendar.reloadEvents("MONTH", date);
                    }
                },
                next: {
                    click: function () {
                        Calendar.calendar.next();
                        let date = Calendar.calendar.getDate();
                        Calendar.reloadEvents("MONTH", date);
                    }
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

        Calendar.reloadEvents = (granularity, date) => {
            const refreshButton = Calendar.calendar.el.querySelector('.fc-refresh-button');
            
            refreshButton.classList.add('is-loading');
            refreshButton.disabled = true;

            return fetchEvents(granularity, date).then(events => {
                // Remove events
                Calendar.calendar.getEvents().forEach(event => { event.remove(); })
                // Load new ones
                Calendar.calendar.addEventSource(parseEvents(events));
                // Reset height
                Calendar.calendar.setOption('height', "parent");

                refreshButton.classList.remove('is-loading');
                refreshButton.disabled = false;
            }).catch(ex => {
                console.error(ex);

                refreshButton.classList.remove('is-loading');
                refreshButton.disabled = false;
            });
        };

        function fetchEvents(granularity, date) {
            const portaMedicineSelect = document.querySelector('#porta-medicine-container');
            let id_porta_medicine = portaMedicineSelect.options[portaMedicineSelect.selectedIndex].value;

            let path;
            if(date != null) {
                //TODO: slow to load
                date.setHours(4);
                let ISODate = date.toISOString();
                let myDate = ISODate.split("T");
                path = "${pageContext.request.contextPath}/api/eventi?" +
                    "granularita=" + granularity + "&" +
                    "id_porta_medicine=" + id_porta_medicine + "&" +
                    "data=" + myDate[0];
            } else {
                path = "${pageContext.request.contextPath}/api/eventi?" +
                    "granularita=" + granularity + "&" +
                    "id_porta_medicine=" + id_porta_medicine;
            }

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


    function prevButton() {

    }
    
    function nextButton() {

    }
</script>