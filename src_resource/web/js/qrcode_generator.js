"use strict";
var QRCodeGenerator = {
    "Controller": {
        "init": function (textToEncodeElement, targetQrCodeElement, printTextElement) {
            QRCodeGenerator.Controller.preventAllFormsDoingStuff();
            QRCodeGenerator.Controller.attachOnChangeTo($(textToEncodeElement), $(targetQrCodeElement), $(printTextElement));
        },
        "preventAllFormsDoingStuff": function () {
            var forms = document.forms;
            for (var i = 0; i < forms.length; i++) {
                var form = forms[i];
                QRCodeGenerator.Controller.attachFormEvent(form, function () {
                    return QRCodeGenerator.Controller.processForm({"target":undefined}, function(){});
                });
            }
        },
        "attachOnChangeTo": function ($textField, $qrCodeTarget, $printTextTarget) {
            $textField.onchange = QRCodeGenerator.Controller.textHasChanged($textField, $qrCodeTarget, $printTextTarget)
            $textField.onkeyup = QRCodeGenerator.Controller.textHasChanged($textField, $qrCodeTarget, $printTextTarget)
        },
        "textHasChanged": function($textField, $qrCodeTarget, $printTextTarget) {
            return function() {
                var textFieldValue = $textField.value;

                MVC.Controller.removeChildren($qrCodeTarget);
                MVC.Controller.removeChildren($printTextTarget);

                $qrCodeTarget.appendChild(showQRCode(textFieldValue));
                $printTextTarget.appendChild($t(textFieldValue));

            }
        },
        "processForm": function (event, callback) {
            if (event.preventDefault) event.preventDefault();
            callback(event.target);
            return false;
        },
        "attachFormEvent": function (target, eventFunction) {
            var form = $(target);
            if (form.attachEvent) {
                form.attachEvent("submit", eventFunction);
            } else {
                form.addEventListener("submit", eventFunction);
            }
        }
    }
};