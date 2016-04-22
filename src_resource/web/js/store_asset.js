"use strict";
var StoreAsset = {
    "Model": {
        "asset": function (uuid, callback) {
            x().get("/asset/" + uuid, function (reply) {
                callback(reply);
            });
        }
    },
    "Controller": {
        "init": function (assetForm) {

            StoreAsset.Controller.addEditFeaturesUuidIfExists();

            StoreAsset.Controller.attachStoreAssetFormEvent(assetForm);
        },

        "addEditFeaturesUuidIfExists": function () {
            if (StoreAsset.Controller.isUuidPassed()) {
                var uuidValue = Url.parameter("uuid");
                StoreAsset.Controller.changeTextToEdit();
                StoreAsset.Controller.addExistingEditDetails(uuidValue);
            }
        },

        "addExistingEditDetails": function (uuid) {
            StoreAsset.Model.asset(uuid, function (model) {
                StoreAsset.Controller.populateAssetFormFields(model);
            });
        },

        "populateAssetFormFields": function (model) {
            for (var key in model) {
                var view = StoreAsset.View.find(key);
                view.value = model[key];
            }
        },

        "changeTextToEdit": function () {
            if (StoreAsset.Controller.isUuidPassed()) {
                var createOrUpdateFormActionView = StoreAsset.View.find("createOrUpdateFormAction");
                var createOrUpdateFormActionButton = StoreAsset.View.find("createOrUpdateFormButton");
                StoreAsset.Controller.removeChildren(createOrUpdateFormActionButton);
                StoreAsset.Controller.removeChildren(createOrUpdateFormActionView);
                createOrUpdateFormActionButton.appendChild($t("Edit"));
                createOrUpdateFormActionView.appendChild($t("Edit"));
            }
        },

        "isUuidPassed": function () {
            var uuid = Url.parameter("uuid");
            return (uuid && "" != uuid);
        },

        "attachFormEvent": function (target, eventFunction) {
            var form = $(target);
            if (form.attachEvent) {
                form.attachEvent("submit", eventFunction);
            } else {
                form.addEventListener("submit", eventFunction);
            }
        },
        "attachStoreAssetFormEvent": function (target) {
            StoreAsset.Controller.attachFormEvent(target, StoreAsset.Controller.processStoreAssetForm);
        },
        "processForm": function (event, callback) {
            if (event.preventDefault) event.preventDefault();
            callback(event.target);
            return false;
        },
        "processStoreAssetForm": function (event) {
            return StoreAsset.Controller.processForm(event, function (form) {
                x().submit("/asset", form, function (model) {
                    StoreAsset.Controller.processStoreAssetReply(model);
                });
            });
        },
        "processStoreAssetReply": function (model) {
            var notificationTarget = StoreAsset.View.notification();
            StoreAsset.Controller.notifySuccessOrFailure(model, notificationTarget,
                "You've successfully added the asset.",
                "You've not added the asset.  Please try again");
        },
        "notifySuccessOrFailure": function (model, notificationTarget, successMessage, failureMessage) {
            if (model.status === "success") {
                StoreAsset.Controller.addSuccessNotification(notificationTarget, successMessage);
            } else {
                StoreAsset.Controller.addFailureNotification(notificationTarget, failureMessage);
            }
        },
        "addFailureNotification": function (target, message) {
            StoreAsset.Controller._addNotification(target, "alert alert-danger", message);
        },
        "addSuccessNotification": function (target, message) {
            StoreAsset.Controller._addNotification(target, "alert alert-success", message);
        },
        "_addNotification": function (target, className, message) {
            StoreAsset.Controller.removeChildren(target);
            target.appendChild($e("div", {"class": className}, $t(message)));
        },
        "removeChildren": function (target) {
            while (target.firstChild) {
                target.removeChild(target.firstChild);
            }
        }

    },
    "View": {
        "notification": function () {
            return StoreAsset.View.find("notification");
        },
        "find": function (target) {
            return $(target);
        }
    }
};