var MVC = {
    "Controller": {
        "registerHomeButton": function () {
            MVC.View.homeButton().onclick = function (event) {
                window.location = "/index.html";
            };
        }
    },
    "View": {
        "homeButton": function () {
            return $("homeButton");
        }
    }
};