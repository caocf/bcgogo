;
(function () {
    var busSwfLoader = {
        _data: null,
        setData: function (value) {
            this._data = value;
        },
        getData: function () {
            return this._data;
        },

        getDomain: function () {
            var domain = null;
            App.Net.syncGet({
                dataType: "json",
                url: "help.do?method=getHelpResourceConfig",
                success: function (configData) {
                    if (configData.error) {
                        G.error("help domain 加载失败！");
                    } else {
                        domain = configData.domain;
                    }
                },
                error: function () {
                    G.error("help domain 加载失败！");
                }
            });
            return domain;
        },

        run: function (param) {
            $.ajax({
                requestType: "AJAX",
                async: true,
                type: "GET",
                url: param["url"],
                dataType: "json",
                success: function (data) {
                    busSwfLoader.setData(data);
                    if (param.isAutoLoad) {
                        busSwfLoader.renderAll(data);
                    }
                }
            });
        },

        render: function (dataItem) {
            if (document.getElementById(dataItem["id"])) {
                bcSwfLoader.remove(dataItem);
                bcSwfLoader.launch(dataItem);
            }
        },

        renderAll: function (data) {
            var item = null,
                el = null;
            for (var i = 0, len = data.length; i < len; i++) {
                item = data[i];
                el = document.getElementById(item["id"]);
                if (el) {
                    bcSwfLoader.remove(item);
                    bcSwfLoader.launch(item);
                }
            }
        }
    };

    window.busSwfLoader = busSwfLoader;
})();
