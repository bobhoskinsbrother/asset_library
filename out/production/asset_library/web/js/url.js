"use strict";

var Url = {
    "parameter": function (key) {
        var query = window.location.search.substring(1);
        var nameValuePair = query.split("&");
        for (var i = 0; i < nameValuePair.length; i++) {
            var pair = nameValuePair[i].split("=");
            if (pair[0] == key) {
                return pair[1];
            }
        }
        return false ;
    }

};