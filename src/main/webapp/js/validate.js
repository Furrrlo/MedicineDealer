let Validator = ((exports) => {
    'use strict';

    exports.rescan = () => {
        document.querySelectorAll('form').forEach(form => {

            const moddedInputs = new Set();
            form.querySelectorAll('.field').forEach(field => {

                const inputCandidates = field.querySelectorAll('input, select');
                // const inputIcon = field.querySelector('.icon.validity');
                const inputErrMsg = field.querySelector(':scope > .help.err');

                inputCandidates.forEach(input => {

                    moddedInputs.add(input);
                    if(input.hasBeenModded) {
                        if(inputErrMsg)
                            input.errorMessageNodes.add(inputErrMsg);
                        return;
                    }

                    input.customValidators = new Set();
                    input.addCustomValidator = validator => {
                        if(validator)
                            input.customValidators.add(validator);
                    };
                    input.addCustomValidator(input => {
                        if(!input.validity.valid)
                            return input.validationMessage;
                        return true;
                    });

                    input.errorMessageNodes = new Set();
                    if(inputErrMsg)
                        input.errorMessageNodes.add(inputErrMsg);

                    input.setCustomError = function(errMsg) {
                        const isValid = !errMsg || errMsg === '';

                        input.errorMessageNodes.forEach(node => node.innerText = errMsg);
                        input.setCustomValidity('');

                        if (isValid) {
                            input.classList.remove('is-danger');
                            input.classList.add('is-success');

                            // if(inputIcon)
                            //     inputIcon.innerHTML = '<i class="fas fa-check"></i>';
                            return true;
                        } else {
                            input.classList.remove('is-success');
                            input.classList.add('is-danger');

                            // if(inputIcon)
                            //     inputIcon.innerHTML = '<i class="fas fa-times"></i>';
                            return false;
                        }
                    };
                    input.resetCustomError = function() {
                        input.errorMessageNodes.forEach(node => node.innerText = '');
                        input.setCustomValidity('');

                        input.classList.remove('is-danger');
                        input.classList.remove('is-success');
                    };

                    input.customValidation = () => {
                        const allValid = [...input.customValidators].every(validator => {
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

                    input.addEventListener('input', () => {
                        const isVisible = input.offsetParent || input.offsetWidth || input.offsetHeight;
                        if(isVisible && !input.disabled)
                            input.customValidation();
                    });

                    input.hasBeenModded = true;
                });
            });

            form.resetCustomErrors = () => {
                form.setCustomError('');
                moddedInputs.forEach(input => input.resetCustomError());
            };
            form.customValidation = async event => {
                try {
                    // Disable all submit buttons
                    document.querySelectorAll("input, button").forEach(btn => {
                        if(btn.type !== 'submit')
                            return;
                        btn.disabled = true;
                    });
                    // Reset errors
                    form.resetCustomErrors();
                    // Fire listeners
                    let stopped = false;
                    moddedInputs.forEach(input => {
                        const isVisible = input.offsetParent || input.offsetWidth || input.offsetHeight;
                        if(!isVisible || input.disabled)
                            return;
                        // Validate
                        if(!input.customValidation(event)) {
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
            };

            if(!form.hasBeenModded) {
                form.hasBeenModded = true;
                // Disable browser validation
                form.noValidate = true;
                // Add method to add other errors
                const formError = form.querySelector(".form-err");
                form.setCustomError = function(errMsg) {
                    if(formError)
                        formError.innerText = errMsg;
                };
                // Add our own validation
                form.addEventListener('submit', event => form.customValidation(event));
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
            }
        });
    };

    return exports;
})({});

document.addEventListener('DOMContentLoaded', () => {
    Validator.rescan();
});