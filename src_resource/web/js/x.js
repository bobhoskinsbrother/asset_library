function x() {

    function serializeToNameValuePairs(formElements, nameValuePairTransformer) {
        var nameValuePairs = [];
        for (var i = 0; i < formElements.length; i++) {
            var nameValue = nameValuePairTransformer(formElements[i]);
            if (nameValue != null) {
                nameValuePairs.push(nameValue)
            }
        }
        return nameValuePairs
    }

    ajax = {};
    ajax.x = function () {
        try {
            return new ActiveXObject("Msxml2.XMLHTTP")
        } catch (e) {
            try {
                return new ActiveXObject("Microsoft.XMLHTTP")
            } catch (e) {
                return new XMLHttpRequest()
            }
        }
    };
    ajax.serialize = function (form) {
        var collectFormElements = function (name) {
            return form.getElementsByTagName(name)
        };
        var makeNameValuePair = function (element) {
            return (element.name) ? "\"" + encodeURIComponent(element.name) + "\":\"" + encodeURIComponent(element.value) + "\"" : "";
        };
        var inputElements = serializeToNameValuePairs(collectFormElements("input"), function (inputElement) {
            if ((inputElement.type != "radio" && inputElement.type != "checkbox") || inputElement.checked)return makeNameValuePair(inputElement)
        });
        var selectElements = serializeToNameValuePairs(collectFormElements("select"), makeNameValuePair);
        var textAreaElements = serializeToNameValuePairs(collectFormElements("textarea"), makeNameValuePair);
        var formName = form.getAttribute("name");
        return formName+"={" + inputElements.concat(selectElements).concat(textAreaElements).join(",") + "}";
    };
    ajax.send = function (uri, callback, method, payload) {
        var x = ajax.x();
        x.open(method, uri, true);
        x.onreadystatechange = function () {
            if (x.readyState == 4) {
                callback(JSON.parse(x.responseText));
            }
        };
        if (method == "POST") {
            x.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        }
        x.send(payload)
    };
    ajax.get = function (url, callback) {
        ajax.send(url, callback, "GET")
    };
    ajax.gets = function (url) {
        var x = ajax.x();
        x.open("GET", url, false);
        x.send(null);
        return x.responseText
    };
    ajax.post = function (url, callback, payload) {
        ajax.send(url, callback, "POST", payload)
    };
    ajax.update = function (url, element) {
        var el = $(element);
        var callback = function (response) {
            el.innerHTML = response
        };
        ajax.get(url, callback)
    };
    ajax.submit = function (url, form, callback) {
        ajax.post(url, callback, ajax.serialize(form))
    };
    return {
        "post": ajax.post,
        "get": ajax.get,
        "submit": ajax.submit,
        "update": ajax.update
    }
}
