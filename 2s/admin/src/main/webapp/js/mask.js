(function () {
    window.Mask = {version:1.0};
    var D = new Function('obj', 'return document.getElementById(obj);');
    var oevent = new Function('e', 'if (!e) e = window.event;return e');

    Mask.Login = function () {
        $("#mask").css('display', 'block');
        $("#mask").css('position', 'absolute');
        $("#mask").css('top', '0px');
        $("#mask").css('left', '0px');
        $("#mask").css('width', (document.documentElement.clientWidth - 2) + "px");
        $("#mask").css('height', (document.documentElement.scrollHeight - 2) + "px")
        $("#mask").css('className', 'b');
        $("#mask").css('zIndex', '3');
        $("#mask").css('filter', 'alpha(opacity=40)');
        $("#mask").css('opacity', '0.4');
        $("#mask").css('backgroundColor', '#000000');
    }

})();