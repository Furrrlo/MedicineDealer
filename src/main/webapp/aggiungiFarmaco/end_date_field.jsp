<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field">
    <div class="field is-horizontal">
        <div class="field-label is-normal">
            <div class="control">
                <label class="radio">
                    <input id="end-date-radio" type="radio" value="date" name="end">
                    Data
                </label>
            </div>
        </div>
        <div class="field-body">
            <div class="field">
                <p class="control">
                    <input id="end-date-input" class="input is-small is-rounded" type="date" disabled required/>
                </p>
            </div>
        </div>
    </div>
    <p class="help is-danger err"></p>
</div>

<script>
    window.addEventListener('load', function() {
        const startDateInput = document.getElementById('start-date-input');
        const endDateInput = document.getElementById('end-date-input');
        const endDateRadio = document.getElementById('end-date-radio');

        // Enable the input only when the radio is checked
        document.querySelectorAll('input[type="radio"][name="end"]').forEach(radio =>
            radio.addEventListener('click', () => {
                endDateInput.disabled = !endDateRadio.checked;

                if(endDateInput.disabled)
                    endDateInput.resetCustomError();
            })
        );
        // Check that the end date is not before today
        endDateInput.addCustomValidator(input => {
            let end = new Date(input.value);
            let today = new Date();

            if(end.getTime() <= today.getTime())
                return "Il termine non può essere prima di oggi";
            return true;
        });
        // Check that the end date is not before the start date
        [startDateInput, endDateInput].forEach(input => input.addCustomValidator(() => {
            let start = new Date(startDateInput.value);
            let end = new Date(endDateInput.value);

            if(end.getTime() <= start.getTime()) {
                startDateInput.setCustomError("")
                endDateInput.setCustomError("Il termine non può essere prima dell'inizio");
                return false;
            }

            endDateInput.setCustomError("");
            return true;
        }));
    });
</script>