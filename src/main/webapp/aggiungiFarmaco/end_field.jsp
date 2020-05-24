<%@ page contentType="text/html; charset=UTF-8" %>

<div class="field">
    <label class="label">Fino a:</label>

    <div class="field">
        <div class="control">
            <label class="radio">
                <input type="radio" name="end" value="mai" checked>
                Mai
            </label>
        </div>
    </div>

    <%@ include file="end_date_field.jsp" %>
    <%@ include file="end_occurrences_field.jsp" %>
</div>