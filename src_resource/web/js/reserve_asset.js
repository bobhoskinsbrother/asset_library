"use strict";
var ReserveAsset = {
    "Model": {
        "people": function (callback) {
            x().get("/people", function (reply) {
                callback(reply);
            });
        },
        "asset": function (uuid, callback) {
            x().get("/asset/" + uuid, function (reply) {
                callback(reply);
            });
        }

    },
    "Controller": {
        "init": function (reserveAssetForms, assetName, personForm, reserveAssetForm) {
            var uuid = Url.parameter("uuid");
            x().get("/is_available/"+uuid, function(reply){
                if(reply.isAvailable) {
                    ReserveAsset.View.assetUuid().value = uuid;
                    ReserveAsset.Controller.addToAssetName(uuid, $(assetName));
                    ReserveAsset.Controller.attachPersonFormEvent(personForm);
                    ReserveAsset.Controller.attachReserveAssetFormEvent(reserveAssetForm);
                    ReserveAsset.Controller.refreshPeopleSelection();
                } else {
                    $(reserveAssetForms).style.display = "none";
                    var person = reply.person;
                    $("personWhoReservedTheAsset").appendChild($t(person.firstName+" "+person.lastName))
                    $("assetNotAvailable").style.display = "inline";
                }

            });
        },
        "attachFormEvent": function (target, eventFunction) {
            var form = $(target);
            if (form.attachEvent) {
                form.attachEvent("submit", eventFunction);
            } else {
                form.addEventListener("submit", eventFunction);
            }
        },
        "attachPersonFormEvent": function (target) {
            ReserveAsset.Controller.attachFormEvent(target, ReserveAsset.Controller.processPersonForm);
        },
        "attachReserveAssetFormEvent": function (target) {
            ReserveAsset.Controller.attachFormEvent(target, ReserveAsset.Controller.processReserveAssetForm);
        },
        "processForm": function (event, callback) {
            if (event.preventDefault) event.preventDefault();
            callback(event.target);
            return false;
        },
        "processReserveAssetForm": function (event) {
            return ReserveAsset.Controller.processForm(event, function (form) {
                x().submit("/reserve_asset", form, function (model) {
                    ReserveAsset.Controller.processReserveAssetReply(model);
                });
            });
        },
        "processPersonForm": function (event) {
            return ReserveAsset.Controller.processForm(event, function (form) {
                x().submit("/person", form, function (model) {
                    ReserveAsset.Controller.processPersonReply(model);
                });
            });
        },
        "processPersonReply": function (model) {
            var notificationTarget = ReserveAsset.View.notification();
            ReserveAsset.Controller.notifySuccessOrFailure(model, notificationTarget,
                "You've been successfully added to the list.  You can now reserve the asset",
                "You've not been added to the list.  Please try again");
            ReserveAsset.Controller.refreshPeopleSelection();
        },
        "processReserveAssetReply": function (model) {
            var notificationTarget = ReserveAsset.View.notification();
            ReserveAsset.Controller.notifySuccessOrFailure(model, notificationTarget,
                "You've successfully reserved the asset.  You can take it now",
                "You've not reserved the asset.  Please try again");
        },
        "notifySuccessOrFailure": function (model, notificationTarget, successMessage, failureMessage) {
            if (model.status === "success") {
                ReserveAsset.Controller.addSuccessNotification(notificationTarget, successMessage);
            } else {
                ReserveAsset.Controller.addFailureNotification(notificationTarget, failureMessage);
            }
        },
        "addFailureNotification": function (target, message) {
            ReserveAsset.Controller._addNotification(target, "failure_message", message);
        },
        "addSuccessNotification": function (target, message) {
            ReserveAsset.Controller._addNotification(target, "success_message", message);
        },
        "_addNotification": function (target, className, message) {
            ReserveAsset.Controller.removeChildren(target);
            target.appendChild($e("div", {"class": className}, $t(message)));
        },
        "refreshPeopleSelection": function () {
            var select = ReserveAsset.View.personUuid();
            ReserveAsset.Controller.removeChildren(select);
            ReserveAsset.Model.people(function (model) {
                var people = model.people;
                for (var uuid in  people) {
                    if (people.hasOwnProperty(uuid)) {
                        var person = people[uuid];
                        var option = $e("option", { "value": uuid }, $t(person.firstName + " " + person.lastName));
                        select.appendChild(option);
                    }
                }
            });
        },
        "addToAssetName": function (uuid, target) {
            ReserveAsset.Model.asset(uuid, function (asset) {
                target.appendChild($t(asset.name));
            });
        },
        "removeChildren": function (target) {
            while (target.firstChild) {
                target.removeChild(target.firstChild);
            }
        }
    },
    "View": {
        "find": function (target) {
            return $(target);
        },
        "notification": function () {
            return ReserveAsset.View.find("notification");
        },
        "assetUuid": function () {
            return ReserveAsset.View.find("assetUuid");
        },
        "personUuid": function () {
            return ReserveAsset.View.find("personUuid");
        }
    }
};