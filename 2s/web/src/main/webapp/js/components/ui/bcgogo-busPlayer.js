;
(function () {
    var busPlayer = {
        run: function (param) {
            $.ajax({
                requestType: "AJAX",
                async: true,
                type: "GET",
                url: param["url"],
                dataType: "json",
                success: function (data) {
                    busPlayer.render(data);
                }
            });
        },
        render: function (data) {
            var item = null,
                el = null;
            for (var i = 0, len = data.length; i < len; i++) {
                item = data[i];
                el = document.getElementById(item.id)
                if (el) {
                    bcplayer.launch(item);
                }
            }
        }
    };

    window.busPlayer = busPlayer;
})();

