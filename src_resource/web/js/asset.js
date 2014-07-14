"use strict";
var Asset = {
    "Model": {
        "asset": function (uuid, callback) {
            x().get("/asset/" + uuid, function (reply) {
                callback(reply);
            });
        }
    },
    "Controller": {
        "init": function (nameTarget, qrCodeTarget, serialNumberTarget, locationCheckoutTarget) {
            Asset.Controller.writeToTarget(nameTarget, qrCodeTarget, serialNumberTarget, locationCheckoutTarget);
        },
        "writeToTarget": function (nameTarget, qrCodeTarget, serialNumberTarget, locationCheckoutTarget) {
            var uuid = Url.parameter("uuid");
            Asset.Model.asset(uuid, function (model) {
                if(model.status==="not found") {
                    Asset.View.assetInfo().style.display="none";
                    Asset.View.noAssetInfo().style.display="inline";
                } else {
                   Asset.View.assetView(model, nameTarget, qrCodeTarget, serialNumberTarget, locationCheckoutTarget);
                }
            });
        }
    },
    "View": {
        "assetInfo": function() {
            return Asset.View.find("assetInfo");
        },
        "noAssetInfo": function() {
            return Asset.View.find("noAssetInfo");
        },
        "assetView": function (model, nameTarget, qrCodeTarget, serialNumberTarget, locationCheckoutTarget) {
            Asset.View.find(nameTarget).innerHTML = model.name;
            Asset.View.find(serialNumberTarget).innerHTML = model.serialNumber;
            var host = location.host;
            var url = "http://"+host+"/reserve_asset.html?uuid=" + model.uuid;
            Asset.View.find(qrCodeTarget).appendChild(showQRCode(url));
            Asset.View.find(locationCheckoutTarget).innerHTML = url;
        },
        "find": function (target) {
            return $(target);
        }
    }
};