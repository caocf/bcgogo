/**
 * application js
 * author:ZhangJuntao
 */

Ext.Loader.setConfig({enabled: true}); //, disableCaching: false

//commitChanges and rejectChanges for the Store in ExtJS 4
Ext.override(Ext.data.Store, {
    //when ajax is success
    commitChanges: function () {
        Ext.each(this.getUpdatedRecords(), function (rec) {
            rec.commit();
        });
        Ext.each(this.getNewRecords(), function (rec) {
            rec.commit();
            rec.phantom = false;
        });
        this.removed = [];
    },
    //when ajax is failed
    rejectChanges: function () {
        var rLength = this.removed.length;
        for (var i = 0; i < rLength; i++) {
            this.insert(this.removed[i].lastIndex || 0, this.removed[i]);
        }
        this.remove(this.getNewRecords());
        this.each(function (rec) {
            rec.reject();
        });
        this.removed = [];
    }
});
Ext.override(Ext.form.DisplayField, {
    getValue: function () {
        return this.value;
    },
    setValue: function (v) {
        this.value = v;
        this.setRawValue(this.formatValue(v));
        return this;
    },
    formatValue: function (v) {
        if (this.dateFormat) {
            return Ext.util.Format.date(new Date(Number(v)),this.dateFormat);
        }
        if (this.numberFormat) {
            return Ext.util.Format.number(v, this.numberFormat);
        }
        return v;
    }
});
Ext.Ajax.on('requestcomplete', function (conn, response, options) {
});

//异常解决
Ext.Ajax.on('requestexception', function (conn, response, options) {
    //session time out
    if (response && response.status == "999") {
        Ext.MessageBox.show({
            title: '提示',
            msg: '会话超时，请重新登录!',
            buttons: Ext.MessageBox.OK,
            fn: function () {
                window.location.href = "login.jsp";
            },
            icon: Ext.MessageBox.ERROR
        }).getEl().setStyle('z-index', '80000');
    }
});


Ext.application({
    name: 'Ext', //应用的名字 (根) 利用MVC时这时定义的包路径需要与命名空间的层次关系一致

    appFolder: 'app/js', //应用的目录

    autoCreateViewport: true,

    launch: function () {
        Ext.tip.QuickTipManager.init();
    },

    controllers: [
        'Ext.controller.Main'
    ]
});