Ext.define('Ext.utils.Common', {
    alias: 'widget.commonutils',
    //form fail 弹出框具体错误
    formFailAction: function (form, action) {
        switch (action.failureType) {
            case Ext.form.action.Action.CLIENT_INVALID:
                Ext.Msg.alert('操作失败', '无效数据！');
                break;
            case Ext.form.action.Action.CONNECT_FAILURE:
                Ext.Msg.alert('操作失败', '网络连接错误');
                break;
            case Ext.form.action.Action.SERVER_INVALID:
                Ext.Msg.alert('操作失败', action.result);
        }
    },
    //根据grid panel获得 改panel store 中的ids
    getSelectionIds: function (list) {
        var selects = list.getSelectionModel().getSelection();
        var ids = "";
        if (selects) {
            for (var i = 0, max = selects.length; i < max; i++) {
                ids += selects[i].data.id + ",";
            }
        }
        return ids;
    },
    //遮罩
    mask: function () {
        Ext.get("contentPanel").mask();
    },
    unmask: function () {
        Ext.get("contentPanel").unmask();
    },
    /**
     * 默认 异步
     * @param config
     */
    ajax: function (config) { // 封装、简化AJAX
        var url = config.url,
            me = this;
        if (!url) {
            console.log("ajax url can't be empty!");
            return;
        }
        var async = config.async ? config.async : true;
        var method = config.method ? config.method : 'post';
        Ext.Ajax.request({
            url: url,
            params: config.params,
            method: method,
            async: async,
            success: function (response, opts) {
                if (config.success) {
                    config.success(Ext.JSON.decode(response.responseText));
                }
            },
            fail: function (response, opts) {
                me.errorMessageHandler(response, opts);
            }
        });
        return false;
    },
    renderWithTip: function (_v) {
        return "<span ext:qtip=" + _v + ">" + _v + "</span>";
    },

    toolTip: function (view, record, item, index, e, columnNames) {
        var columns = view.getGridColumns();
        var column = columns[e.getTarget(view.cellSelector).cellIndex];
        if (columnNames && columnNames.indexOf(column.dataIndex) != -1) {
            Ext.fly(view.cellSelector).set({ 'data-qtip': record.get(column.dataIndex)});
        }
    },

    error: function (msg) {
        Ext.MessageBox.show({
            title: '提示',
            msg: msg,
            buttons: Ext.MessageBox.OK,
            fn: function () {
                window.location.href = "http://localhost:8080/admin/view.do?method=index&loginType=crm";
            },
            icon: Ext.MessageBox.ERROR
        }).getEl().setStyle('z-index', '80000');
    },

    /**
     * todo
     * 异常处理公用方法
     * @param obj
     * @param flag
     */
    errorMessageHandler: function (obj, flag) {
        console.log(obj);
        console.log(flag);
        var code = obj.code;
        if (obj.succ)return;
        if (code == 0) {//系统错误
            if (flag) {
                Ext.Msg.alert('提示:', obj.message);
            } else {
                Ext.Msg.alert('提示:', "操作超时，稍后再试……");
            }
        } else if (code == 4 || code == 101) {//session超时
            window.location.href = obj.obj;
        } else if (code == 6 || code == 7) {//前台session超时
            Ext.MessageBox.confirm('提示：', obj.message + ' 是否登录?', function (btn, text) {
                if (btn == 'yes') {
                    var wd = window.parent;
                    var cyc = true;
                    while (cyc) {
                        var urls = wd.document.URL.split('/');
                        if ('index.html' == urls[urls.length - 1]) {
                            cyc = false;
                        } else {
                            wd = wd.parent;
                        }
                    }
                    wd.location.href = "/display/login.html"
                }
            });
        } else {
            Ext.Msg.alert('提示:', obj.message);
        }
    },

    /**
     * 得到当前的URL位置
     */
    getContextPath: function () {
        var base = document.getElementsByTagName('base')[0];
        if (base && base.href && (base.href.length > 0)) {
            base = base.href;
        } else {
            base = document.URL;
        }
        return base.substr(0, base.indexOf("/", base.indexOf("/", base.indexOf("//") + 2) + 1));
    }
})
;