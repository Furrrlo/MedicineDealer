<%@ page contentType="text/html;charset=UTF-8" %>

<div id="event-click-modal" class="modal">
    <div class="modal-background"></div>
    <div class="modal-card">
        <header class="modal-card-head">
            <p class="modal-card-title" id="modal_title"></p>
            <button class="delete" aria-label="close"></button>
        </header>
        <section class="modal-card-body">
            <div class="loader-wrapper">
                <div class="loader is-loading"></div>
            </div>
            <div class="content">
                <h2 class="assumption-title">Abacavir e lamivudin</h2>
                <p>
                    <span class="assumption-date">mercoledì 3 giugno 2020 10:00</span><br>
                    <span class="assumption-cadence">Ogni settimana il lunedì, martedì alle 10:00, 11:00 e 12:00</span>
                </p>
                <p class="real-assumption-date">Assunzione non avvenuta</p>
            </div>
        </section>
        <footer class="modal-card-foot"></footer>
    </div>
</div>

<script>
    const AssumptionClickModal = (() => {
        const modal = document.getElementById("event-click-modal");
        const closeButton = modal.querySelector(".delete");

        const loaderWrapper = modal.querySelector('.loader-wrapper');

        const title = modal.querySelector(".assumption-title");
        const dateSpan = modal.querySelector(".assumption-date");
        const cadenceSpan = modal.querySelector(".assumption-cadence");
        const realDateSpan = modal.querySelector(".real-assumption-date");

        function AssumptionClickModal() {}

        AssumptionClickModal.open = async assumption => {
            loaderWrapper.classList.add('is-active');

            title.textContent = assumption.nome_farmaco;
            modal.classList.add('is-active');

            // Wait 0.5 seconds just so I can show the spinner
            await new Promise(r => setTimeout(r, 500));

            const path = '${pageContext.request.contextPath}/api/eventi/' + assumption.id_evento;
            const event = await fetch(path).then(async response =>{
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
                return event;
            }).catch(ex => {
                console.error(ex);
            });

            dateSpan.textContent = assumption.moment.format('LLLL');
            cadenceSpan.textContent = makeCadenceText(event);

            if(assumption.realMoment)
                realDateSpan.textContent = "Assunzione avvenuta il " + assumption.realMoment.format('LLLL');
            else if(assumption.passed)
                realDateSpan.textContent = "Assunzione non avvenuta";
            else
                realDateSpan.textContent = "";

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
                if(event.cadenza.settimanale.lunedi)
                    text += "lunedì, ";
                if(event.cadenza.settimanale.martedi)
                    text += "martedì, ";
                if(event.cadenza.settimanale.mercoledi)
                    text += "mercoledì, ";
                if(event.cadenza.settimanale.giovedi)
                    text += "giovedì, ";
                if(event.cadenza.settimanale.venerdi)
                    text += "venerdi, ";
                if(event.cadenza.settimanale.sabato)
                    text += "sabato, ";
                if(event.cadenza.settimanale.domenica)
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