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
        "reserveAssetsContains": function (uuid) {
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
                    return  Assets.Model.person(reserveAsset.personUuid);
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
        "init": function (listTarget, addAssetButton, returnAssetButton, qrCodeButton) {
            Assets.Model.cacheModelsLocally(function () {
                Assets.Controller.writeToTarget(listTarget);
                Assets.Controller.attachAddAssetEvent(addAssetButton);
                Assets.Controller.attachReturnAssetEvent(returnAssetButton);
                Assets.Controller.attachGenerateQRCodeEvent(qrCodeButton);
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
        "attachGenerateQRCodeEvent": function (target) {
            var buttonView = Assets.View.find(target);
            buttonView.onclick = function (event) {
                window.location = "/qrcode_generator.html";
            };
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
        "assetHasBeenDeleted": function(data){
            var assetUuid = data.deleted;
            var $rowToDelete = $("row_" + assetUuid);
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
        "deleteAsset": function(uuid, deletedCallback) {
            if(confirm("Really delete?")) {
                x().post("/remove_asset/" + uuid, function (reply) {
                    deletedCallback(reply);
                });
            }
        }
    },
    "View": {
        "assetsView": function (model) {
            var assetsView = $e("div");
            var assets = model;
            assetsView.appendChild(Assets.View.tableRow({ "class": "table_row table_header" }, $t("Name"), $t("Notes"), $t("Serial Number"), $t(""), $t(""), $t(""), $t("")));
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
            var editAssetButton = $e("button", {"data-uuid": uuid, "name": "edit_asset_button", "class": "button"}, $t("Edit"));
            Assets.Controller.attachEventToStoreAsset(editAssetButton);
            var reserveAssetView;
            if (Assets.Model.reserveAssetsContains(uuid)) {
                var person = Assets.Model.personWhoReservedAsset(uuid);
                reserveAssetView = $t("Reserved by " + person.firstName + " " + person.lastName);
            } else {
                reserveAssetView = $e("button", {"data-uuid": uuid, "name": "reserve_asset_button", "class": "button"}, $t("Reserve"));
                Assets.Controller.attachEventToReserveAsset(reserveAssetView);
            }

            var deleteAssetDetailsButton = $e("button", {"data-uuid": uuid, "name": "delete_asset_details_button", "class": "button"}, $t("Delete"));
            Assets.Controller.attachEventToDeleteAsset(deleteAssetDetailsButton);

            var printAssetDetailsButton = $e("button", {"data-uuid": uuid, "name": "print_asset_details_button", "class": "button"}, $t("Print"));
            Assets.Controller.attachEventToPrintAsset(printAssetDetailsButton);

            return Assets.View.tableRow({ "id": "row_" + uuid, "class": "table_row" },
                $t(asset.name), $t(asset.notes), $t(asset.serialNumber), editAssetButton, reserveAssetView, deleteAssetDetailsButton, printAssetDetailsButton);
        },
        "tableRow": function (attributes, name, notes, serialNumber, editAssetButton, reserveAssetView,deleteAssetDetailsButton, printAssetDetailsButton) {
            var row = $e("div", attributes);
            row.appendChild($e("span", { "class": "table_cell" }, name));
            row.appendChild($e("span", { "class": "table_cell" }, notes));
            row.appendChild($e("span", { "class": "table_cell" }, serialNumber));
            row.appendChild($e("span", { "class": "button_table_cell" }, editAssetButton));
            row.appendChild($e("span", { "class": "button_table_cell" }, reserveAssetView));
            row.appendChild($e("span", { "class": "button_table_cell" }, deleteAssetDetailsButton));
            row.appendChild($e("span", { "class": "button_table_cell" }, printAssetDetailsButton));
            return row;
        },
        "find": function (target) {
            return $(target);
        }
    }
};