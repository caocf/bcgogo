(function () {
    APP_BCGOGO.namespace("Page.client");
    APP_BCGOGO.Page.client = {
        loginSplicing: function () {
            var $_clientUrl = $("#client-url"),
                ccUrl = $.cookie("clientUrl");
            if (ccUrl) {
                $_clientUrl.val(ccUrl);
            }
        },
        clearClientRedirect: function () {
            $.cookie("clientUrl", null);
        }
    }
})();
var bcClient = APP_BCGOGO.Page.client;