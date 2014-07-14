"use strict";
var Assets = {
    "Model": {
        "assets": function (callback) {
            x().get("/assets", function (reply) {
                callback(reply);
            });
        }
    },
    "Controller": {
        "init": function (listTarget, addAssetButton) {
            Assets.Controller.writeToTarget(listTarget);
            Assets.Controller.attachAddAssetEvent(addAssetButton);
            Assets.Controller.attachEditAssetsEvent();
            Assets.Controller.attachReserveAssetEvent();
            Assets.Controller.attachPrintAssetEvent();
        },
        "attachAddAssetEvent": function (target) {
            var buttonView = Assets.View.find(target);
            Assets.Controller.attachStoreAssetEvent(buttonView);
        },
        "attachButtonsEvent": function (className, eventAttachmentFunction) {
            var buttons = document.getElementsByName(className);
            for (var i = 0; i < buttons.length; i++) {
                var buttonView = buttons[i];
                eventAttachmentFunction(buttonView);
            }
        },
        "attachReserveAssetEvent": function () {
            Assets.Controller.attachButtonsEvent("reserve_asset_button", Assets.Controller.attachReserveAsset);
        },
        "attachPrintAssetEvent": function () {
            Assets.Controller.attachButtonsEvent("print_asset_details_button", Assets.Controller.attachPrintAsset);
        },
        "attachEditAssetsEvent": function () {
            Assets.Controller.attachButtonsEvent("edit_asset_button", Assets.Controller.attachStoreAssetEvent);
        },
        "attachReserveAsset": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                window.location = "/reserve_asset.html?uuid="+uuid;
            };
        },
        "attachPrintAsset": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                window.location = "/asset_information.html?uuid="+uuid;
            };
        },
        "attachStoreAssetEvent": function (buttonView) {
            buttonView.onclick = function (event) {
                var uuid = event.target.dataset["uuid"];
                Assets.Controller.openStoreAsset(uuid);
            };
        },
        "writeToTarget": function (target) {
            var targetView = Assets.View.find(target);
            Assets.Model.assets(function (model) {
                var assetsView = Assets.View.assetsView(model);
                targetView.appendChild(assetsView)
            });
        },
        "openStoreAsset": function (uuid) {
            alert(uuid);
        }
    },
    "View": {
        "assetsView": function (model) {
            var assetsView = $e("div");
            var assets = model.assets;
            assetsView.appendChild(Assets.View.tableRow({ "class": "table_row table_header" }, $t("Name"), $t("Serial Number"), $t("Notes")));
            for (var key in  assets) {
                if (assets.hasOwnProperty(key)) {
                    var asset = assets[key];
                    assetsView.appendChild(Assets.View.assetView(asset));
                }
            }
            return assetsView;
        },
        "assetView": function (asset) {
            var editAssetButton = $e("button", {"data-uuid": asset.uuid, "name": "edit_asset_button"}, $t("Edit Asset"));
            var reserveAssetButton = $e("button", {"data-uuid": asset.uuid, "name": "reserve_asset_button"}, $t("Reserve Asset"));
            var printAssetDetailsButton = $e("button", {"data-uuid": asset.uuid, "name": "print_asset_details_button"}, $t("Print Asset Details"));
            return Assets.View.tableRow({ "id": "row_" + asset.uuid, "class": "table_row" },
                $t(asset.name), $t(asset.serialNumber), $t(asset.notes), editAssetButton, reserveAssetButton, printAssetDetailsButton);
        },
        "tableRow": function (attributes) {
            var row = $e("div", attributes);
            if (arguments.length > 1) {
                for (var i = 1; i < arguments.length; i++) {
                    var argument = arguments[i];
                    row.appendChild($e("span", { "class": "table_cell" }, argument));
                }
            }
            return row;
        },
        "find": function (target) {
            return $(target);
        }
    }
};