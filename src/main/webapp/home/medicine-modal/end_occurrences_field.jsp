<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field">
    <div class="field is-horizontal">
        <div class="field-label is-normal">
            <div class="control">
                <label class="radio">
                    <input id="num-ass-radio" type="radio" value="assunzioni" name="end">
                    Dopo
                </label>
            </div>
        </div>
        <div class="field-body">
            <div class="field has-addons">
                <p class="control is-expanded">
                    <input id="num-ass-input" class="input is-small is-rounded" type="number" min="1" disabled required/>
                </p>
                <p class="control">
                    <a class="button is-small is-static is-rounded">assunzioni</a>
                </p>
            </div>
        </div>
    </div>
    <p class="help is-danger err"></p>
</div>

<script>
    window.addEventListener('load', function() {
        const numAssInput = document.getElementById('num-ass-input');
        const numAssRadio = document.getElementById('num-ass-radio');

        numAssRadio.forceChange = () => {
            numAssInput.disabled = !numAssRadio.checked;
        };
        document.querySelectorAll('input[type="radio"][name="end"]').forEach(radio =>
            radio.addEventListener('click', () => {
                numAssRadio.forceChange();

                if(numAssInput.disabled)
                    numAssInput.resetCustomError();
            })
        );
    });
</script>