"use strict";
var ReturnAsset = {
    "Model": {
        "cacheModelsLocally": function (deferred) {
            ReturnAsset.Model.remoteAssets(deferred);
        },
        "remoteAssets": function (deferred) {
            x().get("/assets", function (reply) {
                ReturnAsset.Model.assets = reply.assets;
                ReturnAsset.Model.remoteReturnAssets(deferred);
            });
        },
        "remoteReturnAssets": function (deferred) {
            x().get("/reserve_assets", function (reply) {
                ReturnAsset.Model.reserveAssets = reply;
                ReturnAsset.Model.remotePeople(deferred);
            });
        },
        "remotePeople": function (deferred) {
            x().get("/people", function (reply) {
                ReturnAsset.Model.people = reply.people;
                ReturnAsset.Model.joinLocalReturnAssets(deferred);
            });
        },
        "findPerson": function (uuid) {
            var people = ReturnAsset.Model.people;
            for (var key in people) {
                if (key === uuid) {
                    return people[key];
                }
            }
            return undefined;
        },
        "findAsset": function (uuid) {
            var assets = ReturnAsset.Model.assets;
            for (var key in assets) {
                if (key === uuid) {
                    return assets[key];
                }
            }
            return undefined;

        },

        "joinLocalReturnAssets": function (deferred) {
            var reserveAssets = ReturnAsset.Model.reserveAssets;
            for (var key in reserveAssets) {
                var reserveAsset = reserveAssets[key];
                reserveAsset.person = ReturnAsset.Model.findPerson(reserveAsset.personUuid);
                reserveAsset.asset = ReturnAsset.Model.findAsset(reserveAsset.assetUuid);
            }
            deferred(ReturnAsset.Model.reserveAssets);
        }

    },
    "Controller": {
        "init": function (returnAssetForm) {
            ReturnAsset.Controller.refreshSelectTarget();
            ReturnAsset.Controller.attachReturnAssetFormEvent(returnAssetForm)
        },
        "refreshSelectTarget": function() {
            ReturnAsset.Model.cacheModelsLocally(function (reservedAssets) {
                ReturnAsset.View.makeSelectReturnAsset(reservedAssets, ReturnAsset.View.selectTarget());
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
        "processForm": function (event, callback) {
            if (event.preventDefault) event.preventDefault();
            callback(event.target);
            return false;
        },

        "attachReturnAssetFormEvent": function (target) {
            ReturnAsset.Controller.attachFormEvent(target, ReturnAsset.Controller.processReturnAssetForm);
        },
        "processReturnAssetForm": function (event) {
            return ReturnAsset.Controller.processForm(event, function (form) {
                x().submit("/return_asset", form, function (model) {
                    ReturnAsset.Controller.processReturnAssetReply(model);
                });
            });
        },
        "processReturnAssetReply": function (model) {
            var notificationTarget = ReturnAsset.View.notification();
            ReturnAsset.Controller.notifySuccessOrFailure(model, notificationTarget,
                "You've successfully returned the asset.",
                "You've not returned the asset.  Please try again");
            ReturnAsset.Controller.refreshSelectTarget();
        },
        "notifySuccessOrFailure": function (model, notificationTarget, successMessage, failureMessage) {
            if (model.status === "success") {
                ReturnAsset.Controller.addSuccessNotification(notificationTarget, successMessage);
            } else {
                ReturnAsset.Controller.addFailureNotification(notificationTarget, failureMessage);
            }
        },
        "addFailureNotification": function (target, message) {
            ReturnAsset.Controller._addNotification(target, "alert alert-danger", message);
        },
        "addSuccessNotification": function (target, message) {
            ReturnAsset.Controller._addNotification(target, "alert alert-success", message);
        },
        "_addNotification": function (target, className, message) {
            ReturnAsset.Controller.removeChildren(target);
            target.appendChild($e("div", {"class": className}, $t(message)));
        },
        "removeChildren": function (target) {
            while (target.firstChild) {
                target.removeChild(target.firstChild);
            }
        }
    },
    "View": {

        "selectTarget": function(){
            return $("selectReturnAsset");
        },

        "notification": function () {
            return ReturnAsset.View.find("notification");
        },

        "makeSelectReturnAsset": function (reservedAssets, selectTarget) {
            ReturnAsset.Controller.removeChildren(selectTarget);

            var $select = ReturnAsset.View.makeSelect("uuid");

            for (var key in reservedAssets) {
                if (reservedAssets.hasOwnProperty(key)) {
                    var reservedAsset = reservedAssets[key];
                    var $option = ReturnAsset.View.makeOption(
                            reservedAsset.asset.name + " (checked out to " + reservedAsset.person.firstName + " " + reservedAsset.person.lastName + ")",
                        reservedAsset.uuid);
                    $select.appendChild($option);
                }
            }
            $(selectTarget).appendChild($select);
        },
        "makeSelect": function (id) {
            var $select = $e("select", {"id": id, "name": id, "required": "required", "class":"form-control"});
            var $pleaseSelectOption = ReturnAsset.View.makeOption("Please Select", "");
            $select.appendChild($pleaseSelectOption);
            return $select;
        },
        "makeOption": function (name, value) {
            return $e("option", {"value": value}, $t(name));

        },

        "find": function (target) {
            return $(target);
        }
    }
};