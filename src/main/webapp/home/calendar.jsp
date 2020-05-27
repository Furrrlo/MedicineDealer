<%@ page contentType="text/html;charset=UTF-8" %>

<%-- All these styles are to fix the padding issue --%>
<div class="column" style="display: flex; flex-direction: row; justify-content: stretch;">
    <div class="column" style="padding: 0">
        <div class="calendar" id='calendar'></div>
    </div>
</div>

<script>
    const Calendar = (() => {

        function Calendar() {}

        let firstStart = true;
        Calendar.calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
            height: "parent",
            plugins: [ 'interaction', 'dayGrid', 'timeGrid', 'list', 'bulma' ],
            themeSystem: 'bulma',
            locale: 'it',
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'refresh listYear,dayGridMonth,timeGridWeek,timeGridDay'
            },
            customButtons: {
                refresh: {
                    text: 'Ricarica',
                    click: () => Calendar.reloadEvents()
                }
            },
            buttonText: {
                dayGridMonth: 'Mese',
                timeGridWeek: 'Settimana',
                timeGridDay: 'Giorno',
                listYear: 'Anno',
                today:'Torna alla data di oggi'
            },
            defaultDate: Date.now(),
            selectable: false,
            selectMirror: true,
            editable: false,
            events: (info, successCallback, failureCallback) => {

                if(firstStart) {
                    firstStart = false;
                    successCallback([]);
                    return;
                }

                const refreshButton = Calendar.calendar.el.querySelector('.fc-refresh-button');
                if(refreshButton) {
                    refreshButton.classList.add('is-loading');
                    refreshButton.disabled = true;
                }
                // 1 day less cause the calendar requests 1 more
                let endDate = moment(info.end).subtract(1, 'd').toDate();
                fetchAssumptions({ startDate: info.start, endDate: endDate }).then(assumptions => {
                    successCallback(parseAssumptions(assumptions));

                    if(refreshButton) {
                        refreshButton.classList.remove('is-loading');
                        refreshButton.disabled = false;
                    }
                }).catch(ex => {
                    console.error(ex);
                    failureCallback(ex);

                    if(refreshButton) {
                        refreshButton.classList.remove('is-loading');
                        refreshButton.disabled = false;
                    }
                });
            },
            dateClick: function (info) {

                if(Calendar.calendar.view.type !== 'dayGridMonth')
                    return;

                let events = [];
                let dateClicked = italianTimeFormat(info.date);

                Calendar.calendar.getEvents().forEach(event => {
                    if(italianTimeFormat(event.start) === dateClicked) events.push(event);
                })

                DayClickModal.open(dateClicked, events);
            }
        });
        Calendar.calendar.render();

        Calendar.reloadEvents = () => {
            Calendar.calendar.refetchEvents();
        };

        function fetchAssumptions({ granularity, date, startDate, endDate }) {
            const portaMedicineSelect = document.querySelector('#porta-medicine-container');
            const id_porta_medicine = portaMedicineSelect.options[portaMedicineSelect.selectedIndex].value;

            let path = "${pageContext.request.contextPath}/api/assunzioni?";
            path += "id_porta_medicine=" + id_porta_medicine + "&";
            if(granularity != null)
                path += "granularita=" + granularity + "&";
            if(date != null)
                path += "data=" + getDateOnly(date) + "&";
            if(startDate != null)
                path += "data_inizio=" + getDateOnly(startDate) + "&";
            if(endDate != null)
                path += "data_fine=" + getDateOnly(endDate) + "&";
            path = path.substring(0, path.length - 1);

            return fetch(path).then(async response => {

                if(response.status === 401) {
                    LoginModal.open();
                    throw response.status + ": " + (await response.text());
                }

                if(!response.ok)
                    throw response.status + ": " + (await response.text());
                return JXON.stringToJs(await response.text());
            }).then(assumptions => {
                let obj = assumptions.calendario.assunzione;
                if(!obj)
                    return [];
                if(!obj.forEach)
                    return [ obj ];
                return obj;
            })
        }

        function parseAssumptions(assumptions) {
            if(!assumptions) {
                alert("NON CI SONO MEDICINE DA PRENDERE QUESTO MESE");
                return [];
            }

            let assumptionList = [];
            assumptions.forEach(assumption => {
                let dateToProcess = assumption.data + " " + assumption.ora;
                let myDateTime = moment(dateToProcess, 'YYYY-MM-DD HH:mm').format();

                assumptionList.push({
                    title: assumption.nome_farmaco,
                    start: myDateTime,
                    assumption: assumption
                });
            });

            return assumptionList;
        }

        function getDateOnly(date) {
            const utcDate = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
            return utcDate.toISOString().split("T")[0];
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