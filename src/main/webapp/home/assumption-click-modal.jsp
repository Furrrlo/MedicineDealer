<%@ page contentType="text/html;charset=UTF-8" %>

<div id="event-click-modal" class="modal">
    <div class="modal-background"></div>
    <div class="modal-card">
        <header class="modal-card-head">
            <p class="modal-card-title"></p>
            <button class="delete" aria-label="close"></button>
        </header>
        <section class="modal-card-body">
            <div class="loader-wrapper">
                <div class="loader is-loading"></div>
            </div>
            <div class="content">
                <p>
                    <span class="assumption-date">mercoledì 3 giugno 2020 10:00</span><br>
                    <span class="assumption-cadence">Ogni settimana il lunedì, martedì alle 10:00, 11:00 e 12:00</span>
                </p>
                <p class="real-assumption-date">Assunzione non avvenuta</p>
            </div>
        </section>
        <footer class="modal-card-foot">
            <button class="edit-btn button is-rounded">MODIFICA</button>
            <button class="delete-btn button is-danger is-rounded">ELIMINA</button>
        </footer>
    </div>
</div>

<script>
    const AssumptionClickModal = (() => {
        const modal = document.getElementById("event-click-modal");
        const closeButton = modal.querySelector(".delete");

        const loaderWrapper = modal.querySelector('.loader-wrapper');

        const title = modal.querySelector(".modal-card-title");
        const dateSpan = modal.querySelector(".assumption-date");
        const cadenceSpan = modal.querySelector(".assumption-cadence");
        const realDateSpan = modal.querySelector(".real-assumption-date");

        const editButton = modal.querySelector('.edit-btn');
        const deleteButton = modal.querySelector('.delete-btn');

        let currentEvent;

        function AssumptionClickModal() {}

        AssumptionClickModal.open = async assumption => {
            loaderWrapper.classList.add('is-active');
            editButton.disabled = true;
            deleteButton.disabled = true;
            currentEvent = null;

            title.textContent = assumption.nome_farmaco;
            modal.classList.add('is-active');

            // Wait 0.5 seconds just so I can show the spinner
            await new Promise(r => setTimeout(r, 500));

            const path = '${pageContext.request.contextPath}/api/eventi/' + assumption.id_evento;
            currentEvent = await fetch(path).then(async response =>{
                if(!response.ok)
                    throw response.status + ":" + (await response.text());
                return JXON.stringToJs(await response.text());
            }).then(event0 => {
                const event = event0.evento;
                if(!event.orari.ora)
                    event.orari = [];
                else if(!event.orari.ora.forEach)
                    event.orari = [ event.orari.ora ];
                else
                    event.orari = event.orari.ora;

                event.id = assumption.id_evento;
                return event;
            });

            dateSpan.textContent = assumption.moment.format('LLLL');
            cadenceSpan.textContent = makeCadenceText(currentEvent);

            if(assumption.realMoment)
                realDateSpan.textContent = "Assunzione avvenuta il " + assumption.realMoment.format('LLLL');
            else if(assumption.passed)
                realDateSpan.textContent = "Assunzione non avvenuta";
            else
                realDateSpan.textContent = "";

            editButton.disabled = currentEvent.finito === 'true';
            deleteButton.disabled = currentEvent.finito === 'true';

            loaderWrapper.classList.remove('is-active');
        };

        AssumptionClickModal.close = () => {
            modal.classList.remove('is-active');
        };

        // Listeners

        closeButton.addEventListener('click', () => {
            AssumptionClickModal.close();
        });

        window.addEventListener('click', (event) => {
            if(event.target.classList.contains('modal-background'))
                AssumptionClickModal.close();
        });

        editButton.addEventListener('click', () => {
            if(!currentEvent)
                return;
            AssumptionClickModal.close();
            MedicineModal.open(currentEvent);
        });

        deleteButton.addEventListener('click', async () => {
            if(!currentEvent)
                return;
            try {
                deleteButton.classList.add('is-loading');

                const path = '${pageContext.request.contextPath}/api/eventi/' + currentEvent.id;
                await fetch(path, { method: 'delete' });

                AssumptionClickModal.close();
                Calendar.reloadEvents();
            } finally {
                deleteButton.classList.remove('is-loading');
            }
        });

        function makeCadenceText(event) {
            if(!event.cadenza)
                return "";

            let text = "Ogni ";
            if(event.cadenza.intervallo === "1") {
                if(event.cadenza.giornaliera !== undefined)
                    text += "giorno ";
                else if(event.cadenza.settimanale)
                    text += "settimana ";
            } else {
                text += event.cadenza.intervallo + " ";

                if(event.cadenza.giornaliera !== undefined)
                    text += "giorni ";
                else if(event.cadenza.settimanale)
                    text += "settimane ";
            }

            if(event.cadenza.settimanale) {
                text += "di ";
                if(event.cadenza.settimanale.lunedi === 'true')
                    text += "lunedì, ";
                if(event.cadenza.settimanale.martedi === 'true')
                    text += "martedì, ";
                if(event.cadenza.settimanale.mercoledi === 'true')
                    text += "mercoledì, ";
                if(event.cadenza.settimanale.giovedi === 'true')
                    text += "giovedì, ";
                if(event.cadenza.settimanale.venerdi === 'true')
                    text += "venerdi, ";
                if(event.cadenza.settimanale.sabato === 'true')
                    text += "sabato, ";
                if(event.cadenza.settimanale.domenica === 'true')
                    text += "domenica, ";
                text = text.substring(0, text.length - 2) + " ";
            }

            text += "alle ";
            event.orari.forEach(ora => text += ora.substring(0, ora.length - 3) + ", ");
            text = text.substring(0, text.length - 2);

            return text;
        }

        return AssumptionClickModal;
    })();
</script>