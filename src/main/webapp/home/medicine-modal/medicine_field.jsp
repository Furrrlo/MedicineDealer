<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field">
    <label class="label">Inserisci la medicina e lo slot dove sarà posizionata</label>
    <div class="field-body">
        <%@ include file="aic-field.jsp" %>

        <div class="field">
            <div class="control is-expanded">
                <input id="slot-input" class="input is-rounded"
                       type="number" placeholder="In che slot è posizionato" required/>
            </div>
            <p class="help is-danger err"></p>
        </div>
    </div>
</div>