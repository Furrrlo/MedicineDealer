<%@ page contentType="text/html; charset=UTF-8" %>

<div class="field">
    <label class="label">A che ore devi prenderla?</label>
    <%-- Template --%>
    <div class="is-hidden">
        <div class="control hours-template">
            <div class="field has-addons">
                <p class="control">
                    <a class="hours-remove-button button is-small is-rounded">X</a>
                </p>
                <p class="control">
                    <input class="hour-input input is-small is-rounded" type="time" required/>
                </p>
            </div>
        </div>
    </div>
    <%-- Container--%>
    <div class="field is-grouped is-grouped-multiline hours-container">
        <%-- Buttons go here --%>
    </div>
    <p class="help is-danger err"></p>
    <a class="help hours-add-btn">Aggiungi ora</a>
</div>

<script>
    window.addEventListener('load', function() {

        const templateClass = 'hours-template';
        const templateNode = document.querySelector('.' + templateClass);
        const container = document.querySelector('.hours-container');
        const addButton = document.querySelector('.hours-add-btn');

        const addHourField = (() => {
            let size = 0;
            return () => {
                const newNode = templateNode.cloneNode(true);
                newNode.classList.remove(templateClass);

                container.appendChild(newNode);
                size++;
                Validator.rescan();

                newNode.querySelector('.hours-remove-button').addEventListener('click', () => {
                    if(size <= 1)
                        return;

                    container.removeChild(newNode);
                    size--;
                });
            };
        })();

        addButton.addEventListener('click', addHourField);
        addHourField();
    });
</script>