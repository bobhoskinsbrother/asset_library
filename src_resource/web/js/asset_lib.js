var MVC = {
    "Controller": {
        "registerHomeButton": function () {
            MVC.View.homeButton().onclick = function (event) {
                window.location = "/index.html";
            };
        },
        "removeChildren": function (target) {
            while (target.firstChild) {
                target.removeChild(target.firstChild);
            }
        }
    },
    "View": {
        "homeButton": function () {
            return $("homeButton");
        }
    }
};