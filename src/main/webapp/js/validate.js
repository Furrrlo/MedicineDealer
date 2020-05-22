'use strict';
document.addEventListener('DOMContentLoaded', () => {

    document.querySelectorAll('form').forEach(form => {

        const evtListeners = [];
        const inputs = [];
        form.querySelectorAll('.field').forEach(field => {

            const input = field.querySelector('input') || field.querySelector('select');
            const inputIcon = field.querySelector('.icon.validity');
            const inputErrMsg = field.querySelector('.help.err');

            if (!input || (!inputIcon && !inputErrMsg))
                return;

            const customValidators = [];
            input.addCustomValidator = validator => {
                if(validator)
                    customValidators.push(validator);
            };
            input.setCustomError = function(errMsg) {
                const isValid = !errMsg || errMsg === '';

                if(inputErrMsg)
                    inputErrMsg.innerText = errMsg;
                input.setCustomValidity('');

                if (isValid) {
                    input.classList.remove('is-danger');
                    input.classList.add('is-success');

                    if(inputIcon)
                        inputIcon.innerHTML = '<i class="fas fa-check"></i>';
                    return true;
                } else {
                    input.classList.remove('is-success');
                    input.classList.add('is-danger');

                    if(inputIcon)
                        inputIcon.innerHTML = '<i class="fas fa-times"></i>';
                    return false;
                }
            };

            const evtListener = () => {
                // Check browser error msg
                if(!input.validity.valid) {
                    input.setCustomError(input.validationMessage);
                    return false;
                }
                // Check custom error msg
                const allValid = customValidators.every(validator => {
                    const result = validator(input);
                    // Valid result
                    if(result === true)
                        return true;
                    // Invalid, but override default behavior
                    if(result === false)
                        return false;
                    // Invalid, use default behavior
                    input.setCustomError(result);
                    return false;
                });

                if(allValid) // All valid, reset error
                    input.setCustomError('');
                return allValid;
            };

            inputs.push(input);

            input.addEventListener('input', evtListener);
            evtListeners.push(evtListener);
        });

        // Disable browser validation
        form.noValidate = true;
        // Add method to add other errors
        const formError = form.querySelector(".form-err");
        form.setCustomError = function(errMsg) {
            if(formError)
                formError.innerText = errMsg;
        };
        // Add our own validation
        form.addEventListener('submit', async event => {
            try {
                // Disable all submit buttons
                document.querySelectorAll("input, button").forEach(btn => {
                    if(btn.type !== 'submit')
                        return;
                    btn.disabled = true;
                });
                // Reset errors
                form.setCustomError('');
                inputs.forEach(input => input.setCustomError(''));
                // Fire listeners
                let stopped = false;
                evtListeners.forEach(evtListener => {
                    if(!evtListener(event)) {
                        stopped = true;
                        event.preventDefault();
                        event.stopImmediatePropagation();
                    }
                });
                // Submit method
                if(!stopped && form.customSubmit) {
                    event.preventDefault();
                    await Promise.resolve(form.customSubmit(event));
                }
            } finally {
                // Reenable all submit buttons
                document.querySelectorAll("input, button").forEach(btn => {
                    if(btn.type !== 'submit')
                        return;
                    btn.disabled = false;
                });
            }
        });
        // The onsubmit is registered with the DOM, so remove it
        // and register it after our listener
        if(form.onsubmit) {
            const submitMethod = form.onsubmit;
            form.onsubmit = null;
            form.addEventListener('submit', evt => {
                if(submitMethod(evt) === false)
                    evt.preventDefault();
            });
        }
    });
});