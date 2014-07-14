"use strict";
function $(element) {
    if (typeof element == "string") {
        element = document.getElementById(element);
    }
    return element
}
function $e(type, attributes) {
    var element = document.createElement(type);
    if(attributes) {
        for(var key in attributes ) {
            element.setAttribute(key, attributes[key]);
        }
    }
    if((arguments.length) > 2) {
        for (var i = 2; i < arguments.length; i++) {
            var child = arguments[i];
            element.appendChild(child);
        }
    }
    return  element;
}
function $t(text) {
    return document.createTextNode(text);
}