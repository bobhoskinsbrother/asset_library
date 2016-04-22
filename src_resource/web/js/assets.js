"use strict";
var Assets = {
    "Model": {
        "cacheModelsLocally": function (deferred) {
            Assets.Model.remoteAssets(deferred);
        },
        "remoteAssets": function (deferred) {
            x().get("/assets", function (reply) {
                Assets.Model.assets = reply.assets;
                Assets.Model.remoteReserveAssets(deferred);
            });
        },
        "remoteReserveAssets": function (deferred) {
            x().get("/reserve_assets", function (reply) {
                Assets.Model.reserveAssets = reply;
                Assets.Model.remotePeople(deferred);
            });
        },
        "remotePeople": function (deferred) {
            x().get("/people", function (reply) {
                Assets.Model.people = reply.people;
                deferred();
            });
        },
        "isAssetReserved": function (uuid) {
            var reserveAssets = Assets.Model.reserveAssets;
            for (var i = 0; i < reserveAssets.length; i++) {
                var reserveAsset = reserveAssets[i];
                if (reserveAsset.assetUuid === uuid) {
                    return true;
                }
            }
            return false;
        },
        "personWhoReservedAsset": function (uuid) {
            var reserveAssets = Assets.Model.reserveAssets;
            for (var i = 0; i < reserveAssets.length; i++) {
                var reserveAsset = reserveAssets[i];
                if (reserveAsset.assetUuid === uuid) {
                    return Assets.Model.person(reserveAsset.personUuid);
                }
            }
            return undefined;
        },
        "person": function (uuid) {
            var people = Assets.Model.people;
            return people[uuid];
        }
    },
    "Controller": {
        "init": function (listTarget, addAssetButton, returnAssetButton) {
            Assets.Model.cacheModelsLocally(function () {
                Assets.Controller.writeToTarget(listTarget);
                Assets.Controller.attachAddAssetEvent(addAssetButton);
                Assets.Controller.attachReturnAssetEvent(returnAssetButton);
            });
        },
        "writeToTarget": function (target) {
            var targetView = Assets.View.find(target);
            var assetsView = Assets.View.assetsView(Assets.Model.assets);
            targetView.appendChild(assetsView)
        },
        "attachAddAssetEvent": function (target) {
            var buttonView = Assets.View.find(target);
            Assets.Controller.attachEventToStoreAsset(buttonView);
        },
        "attachButtonsEvent": function (buttonName, eventAttachmentFunction) {
            var buttons = document.getElementsByName(buttonName);
            for (var i = 0; i < buttons.length; i++) {
                var buttonView = buttons[i];
                eventAttachmentFunction(buttonView);
            }
        },
        "attachEventToReserveAsset": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                window.location = "/reserve_asset.html?uuid=" + uuid;
            };
        },
        "attachEventToPrintAsset": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                window.location = "/asset_information.html?uuid=" + uuid;
            };
        },
        "assetHasBeenDeleted": function (data) {
            var assetUuid = data.deleted;
            var $rowToDelete = Assets.View.find("row_" + assetUuid);
            $rowToDelete.parentNode.removeChild($rowToDelete);
        },
        "attachEventToDeleteAsset": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                Assets.Controller.deleteAsset(uuid, Assets.Controller.assetHasBeenDeleted);
            };
        },
        "attachReturnAssetEvent": function (button) {
            var buttonView = Assets.View.find(button);
            buttonView.onclick = function (event) {
                window.location = "/return_asset.html";
            };
        },
        "attachEventToStoreAsset": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                Assets.Controller.openStoreAsset(uuid);
            };
        },
        "openStoreAsset": function (uuid) {
            var suffix = (uuid) ? "?uuid=" + uuid : "";
            window.location = "/store_asset.html" + suffix;
        },
        "deleteAsset": function (uuid, deletedCallback) {
            if (confirm("Really delete?")) {
                x().post("/remove_asset/" + uuid, function (reply) {
                    deletedCallback(reply);
                });
            }
        }
    },
    "View": {
        "assetsView": function (model) {
            var assetsView = $e("div", {"class": "row"});
            var assets = model;
            for (var key in  assets) {
                if (assets.hasOwnProperty(key)) {
                    var asset = assets[key];
                    assetsView.appendChild(Assets.View.assetView(asset));
                }
            }
            return assetsView;
        },
        "assetView": function (asset) {
            var uuid = asset.uuid;
            var editAssetButton = $e("button", {
                "data-uuid": uuid,
                "name": "edit_asset_button",
                "class": "btn btn-primary btn-space"
            }, $t("Edit"));

            Assets.Controller.attachEventToStoreAsset(editAssetButton);

            var assetReserved = Assets.Model.isAssetReserved(uuid);

            var printAssetDetailsButton = $e("button", {
                "data-uuid": uuid,
                "name": "print_asset_details_button",
                "class": "btn btn-primary btn-space"
            }, $t("Print"));

            Assets.Controller.attachEventToPrintAsset(printAssetDetailsButton);

            var deleteAssetDetailsButton = $e("button", {
                "data-uuid": uuid,
                "name": "delete_asset_details_button",
                "class": "btn btn-danger btn-primary btn-space"
            }, $t("Delete"));

            Assets.Controller.attachEventToDeleteAsset(deleteAssetDetailsButton);

            var attributes = {
                "id": "row_" + uuid,
                "class": "col-xs-12 col-sm-6 col-lg-4"
            }, name = $t(asset.name), notes = $t(asset.notes), serialNumber = $t(asset.serialNumber);

            var section = $e("div", attributes);

            section.appendChild($e("h4", {}, name));
            section.appendChild($e("small", {}, serialNumber));
            section.appendChild($e("p", {}, notes));
            if (assetReserved) {
                var person = Assets.Model.personWhoReservedAsset(uuid);
                var reservedBy = $t("Reserved by " + person.firstName + " " + person.lastName);
                section.appendChild($e("p", {"class":"alert alert-success"}, reservedBy));
            }

            section.appendChild(editAssetButton);
            if (!assetReserved) {
                var reserveAssetButton = $e("button", {
                    "data-uuid": uuid,
                    "name": "reserve_asset_button",
                    "class": "btn btn-primary btn-space"
                }, $t("Reserve"));
                Assets.Controller.attachEventToReserveAsset(reserveAssetButton);
                section.appendChild(reserveAssetButton);
            }
            section.appendChild(printAssetDetailsButton);
            section.appendChild(deleteAssetDetailsButton);
            section.appendChild($e("p", {}));

            return section;
        },
        "find": function (target) {
            return $(target);
        }
    }
};