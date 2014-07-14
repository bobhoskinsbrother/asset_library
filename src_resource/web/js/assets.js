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
                    var person = Assets.Model.person(reserveAsset.personUuid);
                    return  person;
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
        "init": function (listTarget, addAssetButton) {
            Assets.Model.cacheModelsLocally(function () {
                Assets.Controller.writeToTarget(listTarget);
                Assets.Controller.attachAddAssetEvent(addAssetButton);
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
        "attachEventToStoreAsset": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                Assets.Controller.openStoreAsset(uuid);
            };
        },
        "openStoreAsset": function (uuid) {
            window.location = "/store_asset.html?uuid=" + uuid;
        }
    },
    "View": {
        "assetsView": function (model) {
            var assetsView = $e("div");
            var assets = model;
            assetsView.appendChild(Assets.View.tableRow({ "class": "table_row table_header" }, $t("Name"), $t("Notes"), $t("Serial Number"), $t(""), $t(""), $t("")));
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
            var printAssetDetailsButton = $e("button", {"data-uuid": uuid, "name": "print_asset_details_button", "class": "button"}, $t("Print"));
            Assets.Controller.attachEventToPrintAsset(printAssetDetailsButton);

            return Assets.View.tableRow({ "id": "row_" + uuid, "class": "table_row" },
                $t(asset.name), $t(asset.notes), $t(asset.serialNumber), editAssetButton, reserveAssetView, printAssetDetailsButton);
        },
        "tableRow": function (attributes, name, notes, serialNumber, editAssetButton, reserveAssetView, printAssetDetailsButton) {
            var row = $e("div", attributes);
            row.appendChild($e("span", { "class": "table_cell" }, name));
            row.appendChild($e("span", { "class": "table_cell" }, notes));
            row.appendChild($e("span", { "class": "table_cell" }, serialNumber));
            row.appendChild($e("span", { "class": "button_table_cell" }, editAssetButton));
            row.appendChild($e("span", { "class": "button_table_cell" }, reserveAssetView));
            row.appendChild($e("span", { "class": "button_table_cell" }, printAssetDetailsButton));
            return row;
        },
        "find": function (target) {
            return $(target);
        }
    }
};